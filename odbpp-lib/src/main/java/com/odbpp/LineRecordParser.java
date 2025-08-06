package com.odbpp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineRecordParser {

    public List<String> parse(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file)) {
            return lines.collect(Collectors.toList());
        }
    }
}
