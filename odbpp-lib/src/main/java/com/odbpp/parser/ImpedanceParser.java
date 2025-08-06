package com.odbpp.parser;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.odbpp.model.impedance.ImpedanceFile;
import java.io.IOException;
import java.nio.file.Path;

public class ImpedanceParser {
    private final XmlMapper xmlMapper = new XmlMapper();

    public ImpedanceFile parse(Path impedanceFile) throws IOException {
        return xmlMapper.readValue(impedanceFile.toFile(), ImpedanceFile.class);
    }
}
