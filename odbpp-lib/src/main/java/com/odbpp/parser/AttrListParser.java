package com.odbpp.parser;

import com.odbpp.model.AttrList;
import com.odbpp.StructuredTextParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class AttrListParser {
    private final StructuredTextParser structuredTextParser = new StructuredTextParser();

    public AttrList parse(Path attrlistFile) throws IOException {
        Map<String, String> data = structuredTextParser.parse(attrlistFile);
        AttrList attrList = new AttrList();
        attrList.setUnits(data.remove("UNITS"));
        attrList.setAttributes(data);
        return attrList;
    }
}
