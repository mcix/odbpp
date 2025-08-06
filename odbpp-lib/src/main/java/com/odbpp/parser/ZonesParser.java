package com.odbpp.parser;

import com.odbpp.model.Zone;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ZonesParser {
    public List<Zone> parse(Path zonesFile) throws IOException {
        List<Zone> zones = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(zonesFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ZONE")) {
                    // Simplified parsing
                    zones.add(new Zone());
                }
            }
        }
        return zones;
    }
}
