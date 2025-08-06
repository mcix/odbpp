package com.odbpp.parser;

import com.odbpp.model.Bom;
import com.odbpp.model.BomItem;
import com.odbpp.model.QualificationStatus;
import com.odbpp.model.ChosenStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BomParser {
    
    /**
     * Parse a BOM file from a file path
     */
    public Bom parse(Path bomFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(bomFile)) {
            return parse(reader, bomFile.getParent().getFileName().toString());
        }
    }
    
    /**
     * Parse a BOM from a Reader - useful for testing with StringReader
     */
    public Bom parse(Reader reader, String bomName) throws IOException {
        Bom bom = new Bom();
        bom.setName(bomName);
        bom.setItems(new ArrayList<>());
        
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            BomItem currentItem = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                if (line.startsWith("CPN")) {
                    if (currentItem != null) {
                        bom.getItems().add(currentItem);
                    }
                    currentItem = new BomItem();
                    currentItem.setCpn(extractValue(line, "CPN"));
                } else if (currentItem != null) {
                    parseItemField(currentItem, line);
                }
            }
            if (currentItem != null) {
                bom.getItems().add(currentItem);
            }
        }
        return bom;
    }
    
    /**
     * Parse a single field line for a BOM item
     */
    protected void parseItemField(BomItem item, String line) {
        if (line.startsWith("LNFILE")) {
            // LNFILE format: "LNFILE 51 name_of_bom_file.txt"
            String[] parts = line.split("\\s+", 3);
            if (parts.length >= 3) {
                item.setItemNumber(Integer.parseInt(parts[1]));
                // Could store filename if needed
            }
        } else if (line.startsWith("VPL_MPN")) {
            item.setVplMpn(extractValue(line, "VPL_MPN"));
        } else if (line.startsWith("VPL_VND")) {
            item.setVplVnd(extractValue(line, "VPL_VND"));
        } else if (line.startsWith("MPN")) {
            item.setMpn(extractValue(line, "MPN"));
        } else if (line.startsWith("VND")) {
            item.setVnd(extractValue(line, "VND"));
        } else if (line.startsWith("QLF")) {
            int qlfValue = Integer.parseInt(extractValue(line, "QLF"));
            item.setQualificationStatus(QualificationStatus.fromValue(qlfValue));
        } else if (line.startsWith("CHS")) {
            int chsValue = Integer.parseInt(extractValue(line, "CHS"));
            item.setChosenStatus(ChosenStatus.fromValue(chsValue));
        } else if (line.startsWith("PRIORITY")) {
            item.setPriority(Integer.parseInt(extractValue(line, "PRIORITY")));
        } else if (line.startsWith("PKG")) {
            item.setPkg(extractValue(line, "PKG"));
        } else if (line.startsWith("IPN")) {
            item.setIpn(extractValue(line, "IPN"));
        } else if (line.startsWith("DSC")) {
            if (item.getDescriptions() == null) {
                item.setDescriptions(new ArrayList<>());
            }
            item.getDescriptions().add(extractValue(line, "DSC"));
        }
    }
    
    /**
     * Extract the value part from a key-value line
     */
    protected String extractValue(String line, String key) {
        if (line.startsWith(key + " ")) {
            return line.substring(key.length() + 1).trim();
        }
        return "";
    }
    
    /**
     * Parse multiple lines of BOM data - useful for testing
     */
    public Bom parseLines(List<String> lines, String bomName) {
        Bom bom = new Bom();
        bom.setName(bomName);
        bom.setItems(new ArrayList<>());
        
        BomItem currentItem = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            if (line.startsWith("CPN")) {
                if (currentItem != null) {
                    bom.getItems().add(currentItem);
                }
                currentItem = new BomItem();
                currentItem.setCpn(extractValue(line, "CPN"));
            } else if (currentItem != null) {
                parseItemField(currentItem, line);
            }
        }
        if (currentItem != null) {
            bom.getItems().add(currentItem);
        }
        return bom;
    }
}
