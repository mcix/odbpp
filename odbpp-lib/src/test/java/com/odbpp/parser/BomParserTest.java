package com.odbpp.parser;

import com.odbpp.model.Bom;
import com.odbpp.model.BomItem;
import com.odbpp.model.QualificationStatus;
import com.odbpp.model.ChosenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BomParserTest {

    private BomParser parser;

    @BeforeEach
    void setUp() {
        parser = new BomParser();
    }

    @Test
    void testParseAlternatePartsExample() {
        // Example from the user's request
        List<String> lines = Arrays.asList(
            "CPN 69K9KMH12B",
            "LNFILE 51 name_of_bom_file.txt",
            "VPL_MPN MAX5156ACEE",
            "VPL_VND MAXIM",
            "MPN MAX5156ACEE",
            "LNFILE 51 name_of_bom_file.txt",
            "VND MAXIM",
            "LNFILE 51 name_of_bom_file.txt",
            "QLF 0",
            "CHS 1",
            "PRIORITY 1",
            "CPN 69K9KMH12B",
            "LNFILE 51 name_of_bom_file.txt",
            "VPL_MPN CY22150FC",
            "VPL_VND CYPRESS",
            "MPN CY22150FC",
            "LNFILE 52 name_of_bom_file.txt",
            "VND CYPRESS",
            "LNFILE 52 name_of_bom_file.txt",
            "QLF 0",
            "CHS 0",
            "PRIORITY 2",
            "CPN 69K9KMH12B",
            "LNFILE 51 name_of_bom_file.txt",
            "VPL_MPN AT93C66-10SC",
            "VPL_VND ATMEL",
            "MPN AT93C66-10SC",
            "LNFILE 53 name_of_bom_file.txt",
            "VND ATMEL",
            "LNFILE 53 name_of_bom_file.txt",
            "QLF -1",
            "CHS 0",
            "PRIORITY 0"
        );

        Bom bom = parser.parseLines(lines, "test_bom");

        // Verify BOM structure
        assertNotNull(bom);
        assertEquals("test_bom", bom.getName());
        assertEquals(3, bom.getItems().size());

        // Verify first item (chosen part)
        BomItem firstItem = bom.getItems().get(0);
        assertEquals("69K9KMH12B", firstItem.getCpn());
        assertEquals("MAX5156ACEE", firstItem.getVplMpn());
        assertEquals("MAXIM", firstItem.getVplVnd());
        assertEquals("MAX5156ACEE", firstItem.getMpn());
        assertEquals("MAXIM", firstItem.getVnd());
        assertEquals(51, firstItem.getItemNumber());
        assertEquals(QualificationStatus.UNKNOWN, firstItem.getQualificationStatus());
        assertEquals(ChosenStatus.CHOSEN, firstItem.getChosenStatus());
        assertEquals(1, firstItem.getPriority());

        // Verify second item (not chosen)
        BomItem secondItem = bom.getItems().get(1);
        assertEquals("69K9KMH12B", secondItem.getCpn());
        assertEquals("CY22150FC", secondItem.getVplMpn());
        assertEquals("CYPRESS", secondItem.getVplVnd());
        assertEquals("CY22150FC", secondItem.getMpn());
        assertEquals("CYPRESS", secondItem.getVnd());
        assertEquals(52, secondItem.getItemNumber());
        assertEquals(QualificationStatus.UNKNOWN, secondItem.getQualificationStatus());
        assertEquals(ChosenStatus.NOT_CHOSEN, secondItem.getChosenStatus());
        assertEquals(2, secondItem.getPriority());

        // Verify third item (not qualified)
        BomItem thirdItem = bom.getItems().get(2);
        assertEquals("69K9KMH12B", thirdItem.getCpn());
        assertEquals("AT93C66-10SC", thirdItem.getVplMpn());
        assertEquals("ATMEL", thirdItem.getVplVnd());
        assertEquals("AT93C66-10SC", thirdItem.getMpn());
        assertEquals("ATMEL", thirdItem.getVnd());
        assertEquals(53, thirdItem.getItemNumber());
        assertEquals(QualificationStatus.NOT_QUALIFIED, thirdItem.getQualificationStatus());
        assertEquals(ChosenStatus.NOT_CHOSEN, thirdItem.getChosenStatus());
        assertEquals(0, thirdItem.getPriority());
    }

    @Test
    void testParseWithReader() throws IOException {
        String bomData = String.join("\n",
            "CPN TEST123",
            "VPL_MPN TEST_MPN",
            "VPL_VND TEST_VENDOR",
            "MPN TEST_MPN",
            "VND TEST_VENDOR",
            "QLF 1",
            "CHS 1",
            "PRIORITY 1"
        );

        try (StringReader reader = new StringReader(bomData)) {
            Bom bom = parser.parse(reader, "test_bom");
            
            assertNotNull(bom);
            assertEquals("test_bom", bom.getName());
            assertEquals(1, bom.getItems().size());
            
            BomItem item = bom.getItems().get(0);
            assertEquals("TEST123", item.getCpn());
            assertEquals("TEST_MPN", item.getVplMpn());
            assertEquals("TEST_VENDOR", item.getVplVnd());
            assertEquals("TEST_MPN", item.getMpn());
            assertEquals("TEST_VENDOR", item.getVnd());
            assertEquals(QualificationStatus.QUALIFIED, item.getQualificationStatus());
            assertEquals(ChosenStatus.CHOSEN, item.getChosenStatus());
            assertEquals(1, item.getPriority());
        }
    }

    @Test
    void testParseWithFile(@TempDir Path tempDir) throws IOException {
        String bomData = String.join("\n",
            "CPN FILE123",
            "VPL_MPN FILE_MPN",
            "VPL_VND FILE_VENDOR",
            "MPN FILE_MPN",
            "VND FILE_VENDOR",
            "QLF 0",
            "CHS 0",
            "PRIORITY 5"
        );

        Path bomFile = tempDir.resolve("test.bom");
        Files.write(bomFile, bomData.getBytes());

        Bom bom = parser.parse(bomFile);
        
        assertNotNull(bom);
        assertEquals("test", bom.getName()); // Parent directory name
        assertEquals(1, bom.getItems().size());
        
        BomItem item = bom.getItems().get(0);
        assertEquals("FILE123", item.getCpn());
        assertEquals("FILE_MPN", item.getVplMpn());
        assertEquals("FILE_VENDOR", item.getVplVnd());
        assertEquals("FILE_MPN", item.getMpn());
        assertEquals("FILE_VENDOR", item.getVnd());
        assertEquals(QualificationStatus.UNKNOWN, item.getQualificationStatus());
        assertEquals(ChosenStatus.NOT_CHOSEN, item.getChosenStatus());
        assertEquals(5, item.getPriority());
    }

    @Test
    void testParseWithEmptyLines() {
        List<String> lines = Arrays.asList(
            "",
            "CPN TEST123",
            "  ",
            "VPL_MPN TEST_MPN",
            "",
            "VPL_VND TEST_VENDOR",
            "MPN TEST_MPN",
            "VND TEST_VENDOR",
            "QLF 1",
            "CHS 1",
            "PRIORITY 1",
            ""
        );

        Bom bom = parser.parseLines(lines, "test_bom");
        
        assertNotNull(bom);
        assertEquals(1, bom.getItems().size());
        
        BomItem item = bom.getItems().get(0);
        assertEquals("TEST123", item.getCpn());
        assertEquals("TEST_MPN", item.getVplMpn());
        assertEquals("TEST_VENDOR", item.getVplVnd());
    }

    @Test
    void testParseWithAdditionalFields() {
        List<String> lines = Arrays.asList(
            "CPN TEST123",
            "PKG SOIC-8",
            "IPN INT123",
            "DSC Test description 1",
            "DSC Test description 2",
            "VPL_MPN TEST_MPN",
            "VPL_VND TEST_VENDOR",
            "MPN TEST_MPN",
            "VND TEST_VENDOR",
            "QLF 1",
            "CHS 1",
            "PRIORITY 1"
        );

        Bom bom = parser.parseLines(lines, "test_bom");
        
        assertNotNull(bom);
        assertEquals(1, bom.getItems().size());
        
        BomItem item = bom.getItems().get(0);
        assertEquals("TEST123", item.getCpn());
        assertEquals("SOIC-8", item.getPkg());
        assertEquals("INT123", item.getIpn());
        assertNotNull(item.getDescriptions());
        assertEquals(2, item.getDescriptions().size());
        assertEquals("Test description 1", item.getDescriptions().get(0));
        assertEquals("Test description 2", item.getDescriptions().get(1));
    }

    @Test
    void testParseMultipleItems() {
        List<String> lines = Arrays.asList(
            "CPN ITEM1",
            "VPL_MPN MPN1",
            "VPL_VND VND1",
            "MPN MPN1",
            "VND VND1",
            "QLF 1",
            "CHS 1",
            "PRIORITY 1",
            "CPN ITEM2",
            "VPL_MPN MPN2",
            "VPL_VND VND2",
            "MPN MPN2",
            "VND VND2",
            "QLF 0",
            "CHS 0",
            "PRIORITY 2"
        );

        Bom bom = parser.parseLines(lines, "test_bom");
        
        assertNotNull(bom);
        assertEquals(2, bom.getItems().size());
        
        BomItem item1 = bom.getItems().get(0);
        assertEquals("ITEM1", item1.getCpn());
        assertEquals("MPN1", item1.getMpn());
        assertEquals(ChosenStatus.CHOSEN, item1.getChosenStatus());
        
        BomItem item2 = bom.getItems().get(1);
        assertEquals("ITEM2", item2.getCpn());
        assertEquals("MPN2", item2.getMpn());
        assertEquals(ChosenStatus.NOT_CHOSEN, item2.getChosenStatus());
    }

    @Test
    void testExtractValue() {
        assertEquals("TEST123", parser.extractValue("CPN TEST123", "CPN"));
        assertEquals("TEST_MPN", parser.extractValue("VPL_MPN TEST_MPN", "VPL_MPN"));
        assertEquals("TEST_VENDOR", parser.extractValue("VPL_VND TEST_VENDOR", "VPL_VND"));
        assertEquals("", parser.extractValue("INVALID_LINE", "CPN"));
    }

    @Test
    void testParseItemField() {
        BomItem item = new BomItem();
        
        parser.parseItemField(item, "VPL_MPN TEST_MPN");
        assertEquals("TEST_MPN", item.getVplMpn());
        
        parser.parseItemField(item, "VPL_VND TEST_VENDOR");
        assertEquals("TEST_VENDOR", item.getVplVnd());
        
        parser.parseItemField(item, "MPN TEST_MPN");
        assertEquals("TEST_MPN", item.getMpn());
        
        parser.parseItemField(item, "VND TEST_VENDOR");
        assertEquals("TEST_VENDOR", item.getVnd());
        
        parser.parseItemField(item, "QLF 1");
        assertEquals(QualificationStatus.QUALIFIED, item.getQualificationStatus());
        
        parser.parseItemField(item, "CHS 1");
        assertEquals(ChosenStatus.CHOSEN, item.getChosenStatus());
        
        parser.parseItemField(item, "PRIORITY 5");
        assertEquals(5, item.getPriority());
        
        parser.parseItemField(item, "PKG SOIC-8");
        assertEquals("SOIC-8", item.getPkg());
        
        parser.parseItemField(item, "IPN INT123");
        assertEquals("INT123", item.getIpn());
        
        parser.parseItemField(item, "DSC Test description");
        assertNotNull(item.getDescriptions());
        assertEquals(1, item.getDescriptions().size());
        assertEquals("Test description", item.getDescriptions().get(0));
    }
} 