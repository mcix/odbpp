package com.odbpp;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class XmlParser {

    private final XmlMapper xmlMapper = new XmlMapper();

    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(Path file) throws IOException {
        return xmlMapper.readValue(file.toFile(), Map.class);
    }
}
