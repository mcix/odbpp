package com.odbpp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemNavigator {

    private final Path root;
    private final Decompressor decompressor = new Decompressor();

    public FileSystemNavigator(Path root) {
        this.root = root;
    }

    public List<Path> findFiles() throws IOException {
        List<Path> files;
        try (Stream<Path> paths = Files.walk(root)) {
            files = paths.filter(Files::isRegularFile)
                         .collect(Collectors.toList());
        }

        List<Path> result = new ArrayList<>();
        for (Path file : files) {
            if (file.toString().endsWith(".Z")) {
                Path decompressedFile = Files.createTempFile(file.getFileName().toString(), ".tmp");
                decompressor.decompress(file, decompressedFile);
                result.add(decompressedFile);
            } else {
                result.add(file);
            }
        }
        return result;
    }
}
