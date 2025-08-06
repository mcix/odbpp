package com.odbpp.parser;

import com.odbpp.model.Matrix;
import com.odbpp.model.MatrixLayer;
import com.odbpp.model.Step;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MatrixParser {
    public Matrix parse(Path matrixFile) throws IOException {
        Matrix matrix = new Matrix();
        matrix.setLayers(new ArrayList<>());
        matrix.setSteps(new ArrayList<>());

        try (BufferedReader reader = Files.newBufferedReader(matrixFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("STEP {")) {
                    matrix.getSteps().add(parseStepBlock(reader));
                } else if (line.startsWith("LAYER {")) {
                    matrix.getLayers().add(parseLayerBlock(reader));
                }
            }
        }
        return matrix;
    }

    private Step parseStepBlock(BufferedReader reader) throws IOException {
        Step step = new Step();
        Map<String, String> data = parseBlock(reader);
        step.setCol(Integer.parseInt(data.get("COL")));
        step.setId(Integer.parseInt(data.get("ID")));
        step.setName(data.get("NAME"));
        return step;
    }

    private MatrixLayer parseLayerBlock(BufferedReader reader) throws IOException {
        MatrixLayer layer = new MatrixLayer();
        Map<String, String> data = parseBlock(reader);
        layer.setRow(Integer.parseInt(data.get("ROW")));
        layer.setContext(data.get("CONTEXT"));
        layer.setType(data.get("TYPE"));
        layer.setName(data.get("NAME"));
        layer.setPolarity(data.get("POLARITY"));
        layer.setStartName(data.get("START_NAME"));
        layer.setEndName(data.get("END_NAME"));
        // Set optional fields
        if (data.containsKey("OLD_NAME")) layer.setOldName(data.get("OLD_NAME"));
        if (data.containsKey("ADD_TYPE")) layer.setAddType(data.get("ADD_TYPE"));
        if (data.containsKey("COLOR")) layer.setColor(Integer.parseInt(data.get("COLOR")));
        if (data.containsKey("ID")) layer.setId(Integer.parseInt(data.get("ID")));
        return layer;
    }

    private Map<String, String> parseBlock(BufferedReader reader) throws IOException {
        Map<String, String> data = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equals("}")) {
                break;
            }
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                data.put(parts[0].trim(), parts[1].trim());
            }
        }
        return data;
    }
}
