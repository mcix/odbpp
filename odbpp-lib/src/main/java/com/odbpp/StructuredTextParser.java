package com.odbpp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StructuredTextParser {

    public Map<String, String> parse(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file)) {
            return lines.map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .map(line -> line.split("=", 2))
                        .filter(parts -> parts.length == 2)
                        .collect(Collectors.toMap(parts -> parts[0].trim(), parts -> parts[1].trim()));
        }
    }
}
