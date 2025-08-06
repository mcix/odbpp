package com.odbpp.parser;

import com.odbpp.model.AttributeDefinition;
import com.odbpp.model.AttributeType;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributeDefinitionParser {

    public Map<String, AttributeDefinition> parse(Path attrDefFile) throws IOException {
        Map<String, AttributeDefinition> definitions = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(attrDefFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || !Character.isUpperCase(line.charAt(0))) {
                    continue;
                }
                String[] parts = line.split(" ", 2);
                if (parts.length > 0) {
                    String typeStr = parts[0];
                    if (typeStr.endsWith("{")) {
                        typeStr = typeStr.substring(0, typeStr.length() - 1).trim();
                    }
                    AttributeType type = AttributeType.valueOf(typeStr);
                    AttributeDefinition def = parseDefinitionBlock(reader, type);
                    definitions.put(def.getName(), def);
                }
            }
        }
        return definitions;
    }

    private AttributeDefinition parseDefinitionBlock(BufferedReader reader, AttributeType type) throws IOException {
        AttributeDefinition def = new AttributeDefinition();
        def.setType(type);
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equals("}")) {
                break;
            }
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                setAttributeProperty(def, key, value);
            }
        }
        return def;
    }

    private void setAttributeProperty(AttributeDefinition def, String key, String value) {
        switch (key) {
            case "NAME":
                def.setName(value);
                break;
            case "PROMPT":
                def.setPrompt(value);
                break;
            case "ENTITY":
                def.setEntities(Arrays.asList(value.split(";")));
                break;
            case "GROUP":
                def.setGroup(value);
                break;
            case "DEF":
                def.setDefaultValue(value);
                break;
            case "MIN_LEN":
                def.setMinLen(Integer.parseInt(value));
                break;
            case "MAX_LEN":
                def.setMaxLen(Integer.parseInt(value));
                break;
            case "OPTIONS":
                def.setOptions(Arrays.asList(value.split(";")));
                break;
            case "DELETED":
                def.setDeletedOptions(Arrays.stream(value.split(";"))
                        .map("YES"::equalsIgnoreCase)
                        .collect(Collectors.toList()));
                break;
            case "MIN_VAL":
                if (def.getType() == AttributeType.INTEGER) def.setMinValInt(Integer.parseInt(value));
                else def.setMinValFloat(Double.parseDouble(value));
                break;
            case "MAX_VAL":
                if (def.getType() == AttributeType.INTEGER) def.setMaxValInt(Integer.parseInt(value));
                else def.setMaxValFloat(Double.parseDouble(value));
                break;
            case "UNIT_TYPE":
                def.setUnitType(value);
                break;
            case "UNITS":
                def.setUnits(value);
                break;
        }
    }
}
