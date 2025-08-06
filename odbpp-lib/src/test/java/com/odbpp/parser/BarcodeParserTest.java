package com.odbpp.parser;

import com.odbpp.model.Barcode;
import com.odbpp.model.Features;
import com.odbpp.model.Polarity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BarcodeParserTest {

    @Test
    public void testParseBarcodeRecord(@TempDir Path tempDir) throws IOException {
        // Create a test features file with a barcode record
        String barcodeLine = "B 1.5 2.5 UPC39 standard P 0 E 0.1 0.2 Y Y N Y T 'SAMPLE123';ID=12345";
        Path featuresFile = tempDir.resolve("test.features");
        Files.write(featuresFile, List.of(barcodeLine));

        // Parse the file
        FeaturesFileParser parser = new FeaturesFileParser();
        Features features = parser.parse(featuresFile);

        // Verify the barcode was parsed correctly
        assertEquals(1, features.getFeatures().size());
        assertTrue(features.getFeatures().get(0) instanceof Barcode);

        Barcode barcode = (Barcode) features.getFeatures().get(0);
        assertEquals(1.5, barcode.getX(), 0.001);
        assertEquals(2.5, barcode.getY(), 0.001);
        assertEquals("UPC39", barcode.getBarcodeName());
        assertEquals("standard", barcode.getFont());
        assertEquals(Polarity.POSITIVE, barcode.getPolarity());
        assertEquals(0, barcode.getOrientDef());
        assertEquals(0.1, barcode.getWidth(), 0.001);
        assertEquals(0.2, barcode.getHeight(), 0.001);
        assertEquals("Y", barcode.getFullAscii());
        assertEquals("Y", barcode.getChecksum());
        assertEquals("N", barcode.getBackground());
        assertEquals("Y", barcode.getAdditionalString());
        assertEquals("T", barcode.getAdditionalStringPosition());
        assertEquals("SAMPLE123", barcode.getText());
        assertEquals("12345", barcode.getUniqueId());
    }

    @Test
    public void testParseBarcodeWithRotation(@TempDir Path tempDir) throws IOException {
        // Create a test features file with a barcode record that has rotation
        String barcodeLine = "B 1.5 2.5 UPC39 standard P 8 45.0 E 0.1 0.2 Y Y N Y T 'SAMPLE123'";
        Path featuresFile = tempDir.resolve("test.features");
        Files.write(featuresFile, List.of(barcodeLine));

        // Parse the file
        FeaturesFileParser parser = new FeaturesFileParser();
        Features features = parser.parse(featuresFile);

        // Verify the barcode was parsed correctly
        assertEquals(1, features.getFeatures().size());
        assertTrue(features.getFeatures().get(0) instanceof Barcode);

        Barcode barcode = (Barcode) features.getFeatures().get(0);
        assertEquals(8, barcode.getOrientDef());
        assertEquals(45.0, barcode.getOrientDefRotation(), 0.001);
    }

    @Test
    public void testParseBarcodeWithAttributes(@TempDir Path tempDir) throws IOException {
        // Create a test features file with a barcode record that has attributes
        String barcodeLine = "B 1.5 2.5 UPC39 standard P 0 E 0.1 0.2 Y Y N Y T 'SAMPLE123';1=value1;2=value2;ID=12345";
        Path featuresFile = tempDir.resolve("test.features");
        Files.write(featuresFile, List.of(barcodeLine));

        // Parse the file
        FeaturesFileParser parser = new FeaturesFileParser();
        Features features = parser.parse(featuresFile);

        // Verify the barcode was parsed correctly
        assertEquals(1, features.getFeatures().size());
        assertTrue(features.getFeatures().get(0) instanceof Barcode);

        Barcode barcode = (Barcode) features.getFeatures().get(0);
        assertEquals(1, barcode.getAtr());
        assertEquals("value1", barcode.getValue());
        assertEquals("12345", barcode.getUniqueId());
    }

    @Test
    public void testParseBarcodeNegativePolarity(@TempDir Path tempDir) throws IOException {
        // Create a test features file with a barcode record with negative polarity
        String barcodeLine = "B 1.5 2.5 UPC39 standard N 0 E 0.1 0.2 Y Y N Y T 'SAMPLE123'";
        Path featuresFile = tempDir.resolve("test.features");
        Files.write(featuresFile, List.of(barcodeLine));

        // Parse the file
        FeaturesFileParser parser = new FeaturesFileParser();
        Features features = parser.parse(featuresFile);

        // Verify the barcode was parsed correctly
        assertEquals(1, features.getFeatures().size());
        assertTrue(features.getFeatures().get(0) instanceof Barcode);

        Barcode barcode = (Barcode) features.getFeatures().get(0);
        assertEquals(Polarity.NEGATIVE, barcode.getPolarity());
    }
} 