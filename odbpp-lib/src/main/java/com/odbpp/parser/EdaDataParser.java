package com.odbpp.parser;

import com.odbpp.model.EdaData;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class EdaDataParser {
    public EdaData parse(Path dataFile) throws IOException {
        EdaData edaData = new EdaData();
        edaData.setNetRecords(new ArrayList<>());
        edaData.setPackageRecords(new ArrayList<>());
        edaData.setNetRecordsByName(new HashMap<>());
        edaData.setPackageRecordsByName(new HashMap<>());

        try (BufferedReader reader = Files.newBufferedReader(dataFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("NET ")) {
                    // Simplified parsing
                    EdaData.NetRecord net = new EdaData.NetRecord();
                    net.setName(line.split(" ")[1]);
                    edaData.getNetRecords().add(net);
                    edaData.getNetRecordsByName().put(net.getName(), net);
                } else if (line.startsWith("PKG ")) {
                    // Simplified parsing
                    EdaData.PackageRecord pkg = new EdaData.PackageRecord();
                    pkg.setName(line.split(" ")[1]);
                    edaData.getPackageRecords().add(pkg);
                    edaData.getPackageRecordsByName().put(pkg.getName(), pkg);
                }
            }
        }
        return edaData;
    }
}
