package com.odbpp.parser;

import com.odbpp.model.StepHdr;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StepHdrParser {

    public StepHdr parse(Path stepHdrFile) throws IOException {
        StepHdr stepHdr = new StepHdr();
        stepHdr.setStepRepeats(new ArrayList<>());

        try (BufferedReader reader = Files.newBufferedReader(stepHdrFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("STEP-REPEAT {")) {
                    stepHdr.getStepRepeats().add(parseStepRepeat(reader));
                } else {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        setStepHdrProperty(stepHdr, parts[0].trim(), parts[1].trim());
                    }
                }
            }
        }
        return stepHdr;
    }

    private void setStepHdrProperty(StepHdr stepHdr, String key, String value) {
        switch (key) {
            case "UNITS":
                stepHdr.setUnits(value);
                break;
            case "X_DATUM":
                stepHdr.setXDatum(Double.parseDouble(value));
                break;
            case "Y_DATUM":
                stepHdr.setYDatum(Double.parseDouble(value));
                break;
            case "ID":
                stepHdr.setId(Integer.parseInt(value));
                break;
            // Add other simple properties here
        }
    }

    private StepHdr.StepRepeat parseStepRepeat(BufferedReader reader) throws IOException {
        StepHdr.StepRepeat stepRepeat = new StepHdr.StepRepeat();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equals("}")) {
                break;
            }
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                setStepRepeatProperty(stepRepeat, parts[0].trim(), parts[1].trim());
            }
        }
        return stepRepeat;
    }

    private void setStepRepeatProperty(StepHdr.StepRepeat stepRepeat, String key, String value) {
        switch (key) {
            case "NAME":
                stepRepeat.setName(value);
                break;
            case "X":
                stepRepeat.setX(Double.parseDouble(value));
                break;
            case "Y":
                stepRepeat.setY(Double.parseDouble(value));
                break;
            // Add other step repeat properties here
        }
    }
}
