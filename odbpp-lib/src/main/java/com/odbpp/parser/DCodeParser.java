package com.odbpp.parser;

import com.odbpp.model.DCode;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DCodeParser {
    public List<DCode> parse(Path dcodesFile) throws IOException {
        List<DCode> dcodes = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(dcodesFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || !line.startsWith("dcode")) {
                    continue;
                }
                dcodes.add(parseDCode(line));
            }
        }
        return dcodes;
    }

    private DCode parseDCode(String line) {
        String[] parts = line.substring(5).trim().split(" ");
        DCode dcode = new DCode();
        dcode.setCode(Integer.parseInt(parts[0]));
        dcode.setSymbolName(parts[1]);
        dcode.setAngle(Double.parseDouble(parts[2]));
        dcode.setMirror("mirror".equals(parts[3]));
        return dcode;
    }
}
