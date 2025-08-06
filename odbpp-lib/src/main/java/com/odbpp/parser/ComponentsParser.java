package com.odbpp.parser;

import com.odbpp.model.Component;
import com.odbpp.model.Components;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentsParser {
    private static final Pattern CMP_PATTERN = Pattern.compile(
            "^CMP\\s+\\d+\\s+([\\d.]+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+(N|Y)\\s+([\\w\\d]+)\\s+([\\w\\d-]+)");
    private static final Pattern PRP_PATTERN = Pattern.compile("^PRP\\s+([\\w_]+)\\s+'(.*)'");

    public Components parse(Path componentsFile) throws IOException {
        Components components = new Components();
        List<String> lines = Files.readAllLines(componentsFile, StandardCharsets.ISO_8859_1);
        Component currentComponent = null;

        for (String line : lines) {
            Matcher cmpMatcher = CMP_PATTERN.matcher(line);
            if (cmpMatcher.find()) {
                currentComponent = new Component();
                currentComponent.setX(Double.parseDouble(cmpMatcher.group(1)));
                currentComponent.setY(Double.parseDouble(cmpMatcher.group(2)));
                currentComponent.setRotation(Double.parseDouble(cmpMatcher.group(3)));
                currentComponent.setMirror(cmpMatcher.group(4).equals("Y"));
                currentComponent.setName(cmpMatcher.group(5));
                currentComponent.setPartName(cmpMatcher.group(6));
                components.getComponents().add(currentComponent);
            }

            Matcher prpMatcher = PRP_PATTERN.matcher(line);
            if (prpMatcher.find() && currentComponent != null) {
                currentComponent.getProperties().put(prpMatcher.group(1), prpMatcher.group(2));
            }
        }
        return components;
    }
}
