package com.odbpp.parser;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.odbpp.model.stackup.StackupFile;
import java.io.IOException;
import java.nio.file.Path;

public class StackupParser {
    private final XmlMapper xmlMapper = new XmlMapper();

    public StackupFile parse(Path stackupFile) throws IOException {
        return xmlMapper.readValue(stackupFile.toFile(), StackupFile.class);
    }
}
