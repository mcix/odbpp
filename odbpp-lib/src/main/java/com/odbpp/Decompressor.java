package com.odbpp;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Decompressor {

    public void decompress(Path source, Path target) throws IOException {
        try (InputStream in = Files.newInputStream(source);
             ZCompressorInputStream zIn = new ZCompressorInputStream(in);
             OutputStream out = Files.newOutputStream(target)) {
            final byte[] buffer = new byte[8192];
            int n;
            while ((n = zIn.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        }
    }
}
