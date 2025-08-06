package com.odbpp.parser;

import com.odbpp.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeaturesFileParser {
    private static final Pattern PAD_PATTERN = Pattern.compile(
            "^P\\s+([\\d.]+)\\s+([\\d.]+)\\s+(\\d+)\\s+(P|N)\\s+(\\d+)\\s*");
    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^L\\s+([\\d.]+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+(\\d+)\\s*");
    private static final Pattern ARC_PATTERN = Pattern.compile(
            "^A\\s+([\\d.]+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+(\\d+)\\s*");
    private static final Pattern TEXT_PATTERN = Pattern.compile(
            "^T\\s+([\\d.]+)\\s+([\\d.]+)\\s+'(.*)'\\s+(\\d+)\\s+(\\d+)\\s+(Y|N)\\s*");
    private static final Pattern BARCODE_PATTERN = Pattern.compile(
            "^B\\s+([\\d.]+)\\s+([\\d.]+)\\s+(\\S+)\\s+(\\S+)\\s+(P|N)\\s+(\\d+)(?:\\s+([\\d.]+))?\\s+E\\s+([\\d.]+)\\s+([\\d.]+)\\s+(Y|N)\\s+(Y|N)\\s+(Y|N)\\s+(Y|N)\\s+(T|B)\\s+'(.*?)'(?:;(\\d+)=(.*?))?(?:;ID=(.*?))?\\s*$");
    private static final Pattern SURFACE_PATTERN = Pattern.compile(
            "^S\\s+(P|N)\\s+(\\d+)(?:;(\\d+)=(.*?))?(?:;ID=(.*?))?\\s*$");

    private final SurfaceParser surfaceParser = new SurfaceParser();

    public Features parse(Path featuresFile) throws IOException {
        Features features = new Features();
        Map<Integer, String> symbolMap = new HashMap<>();
        List<String> lines = Files.readAllLines(featuresFile, StandardCharsets.ISO_8859_1);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("$")) {
                parseSymbolDefinition(line, symbolMap);
            } else if (line.startsWith("P")) {
                parsePad(line, features);
            } else if (line.startsWith("L")) {
                parseLine(line, features);
            } else if (line.startsWith("A")) {
                parseArc(line, features);
            } else if (line.startsWith("S")) {
                parseSurface(line, features, lines, i);
            } else if (line.startsWith("T")) {
                parseText(line, features);
            } else if (line.startsWith("B")) {
                parseBarcode(line, features);
            }
        }
        return features;
    }

    private void parseSymbolDefinition(String line, Map<Integer, String> symbolMap) {
        String[] parts = line.substring(1).split("\\s+", 2);
        symbolMap.put(Integer.parseInt(parts[0]), parts[1]);
    }

    private void parsePad(String line, Features features) {
        Matcher matcher = PAD_PATTERN.matcher(line);
        if (matcher.find()) {
            Pad pad = new Pad();
            pad.setX(Double.parseDouble(matcher.group(1)));
            pad.setY(Double.parseDouble(matcher.group(2)));
            pad.setSymbolNumber(Integer.parseInt(matcher.group(3)));
            // Polarity is in group 4, but we are not using it yet
            pad.setRotation(Double.parseDouble(matcher.group(5)));
            features.getFeatures().add(pad);
        }
    }

    private void parseLine(String line, Features features) {
        Matcher matcher = LINE_PATTERN.matcher(line);
        if (matcher.find()) {
            Line lineFeature = new Line();
            lineFeature.setXs(Double.parseDouble(matcher.group(1)));
            lineFeature.setYs(Double.parseDouble(matcher.group(2)));
            lineFeature.setXe(Double.parseDouble(matcher.group(3)));
            lineFeature.setYe(Double.parseDouble(matcher.group(4)));
            lineFeature.setSymbolNumber(Integer.parseInt(matcher.group(5)));
            features.getFeatures().add(lineFeature);
        }
    }

    private void parseArc(String line, Features features) {
        Matcher matcher = ARC_PATTERN.matcher(line);
        if (matcher.find()) {
            Arc arc = new Arc();
            arc.setXs(Double.parseDouble(matcher.group(1)));
            arc.setYs(Double.parseDouble(matcher.group(2)));
            arc.setXe(Double.parseDouble(matcher.group(3)));
            arc.setYe(Double.parseDouble(matcher.group(4)));
            arc.setXc(Double.parseDouble(matcher.group(5)));
            arc.setYc(Double.parseDouble(matcher.group(6)));
            arc.setSymbolNumber(Integer.parseInt(matcher.group(7)));
            features.getFeatures().add(arc);
        }
    }

    private void parseSurface(String line, Features features, List<String> lines, int currentIndex) {
        Matcher matcher = SURFACE_PATTERN.matcher(line);
        if (matcher.find()) {
            Surface surface = new Surface();
            
            // Parse polarity
            String polarityStr = matcher.group(1);
            surface.setPolarity(Polarity.fromString(polarityStr));
            
            // Parse dcode
            int dcode = Integer.parseInt(matcher.group(2));
            surface.setDcode(dcode);
            
            // Parse attributes if present
            if (matcher.group(3) != null && matcher.group(4) != null) {
                int attrNumber = Integer.parseInt(matcher.group(3));
                String attrValue = matcher.group(4);
                surface.getAttributes().put(attrNumber, attrValue);
            }
            
            // Parse unique ID if present
            if (matcher.group(5) != null) {
                surface.setUniqueId(matcher.group(5));
            }
            
            features.getFeatures().add(surface);
            surfaceParser.parse(lines, currentIndex, surface);
        }
    }

    private void parseText(String line, Features features) {
        Matcher matcher = TEXT_PATTERN.matcher(line);
        if (matcher.find()) {
            Text text = new Text();
            text.setX(Double.parseDouble(matcher.group(1)));
            text.setY(Double.parseDouble(matcher.group(2)));
            text.setValue(matcher.group(3));
            text.setSymbolNumber(Integer.parseInt(matcher.group(4)));
            text.setRotation(Double.parseDouble(matcher.group(5)));
            text.setMirror("Y".equals(matcher.group(6)));
            features.getFeatures().add(text);
        }
    }

    private void parseBarcode(String line, Features features) {
        Matcher matcher = BARCODE_PATTERN.matcher(line);
        if (matcher.find()) {
            Barcode barcode = new Barcode();
            
            // Parse x, y coordinates
            barcode.setX(Double.parseDouble(matcher.group(1)));
            barcode.setY(Double.parseDouble(matcher.group(2)));
            
            // Parse barcode name
            barcode.setBarcodeName(matcher.group(3));
            
            // Parse font
            barcode.setFont(matcher.group(4));
            
            // Parse polarity
            String polarityStr = matcher.group(5);
            barcode.setPolarity(Polarity.fromString(polarityStr));
            
            // Parse orientation definition
            barcode.setOrientDef(Integer.parseInt(matcher.group(6)));
            
            // Parse rotation if present (for orient_def 8 or 9)
            if (matcher.group(7) != null) {
                barcode.setOrientDefRotation(Double.parseDouble(matcher.group(7)));
            }
            
            // Parse width and height
            barcode.setWidth(Double.parseDouble(matcher.group(8)));
            barcode.setHeight(Double.parseDouble(matcher.group(9)));
            
            // Parse flags
            barcode.setFullAscii(matcher.group(10));
            barcode.setChecksum(matcher.group(11));
            barcode.setBackground(matcher.group(12));
            barcode.setAdditionalString(matcher.group(13));
            
            // Parse additional string position
            barcode.setAdditionalStringPosition(matcher.group(14));
            
            // Parse text string
            barcode.setText(matcher.group(15));
            
            // Parse attributes if present
            if (matcher.group(16) != null && matcher.group(17) != null) {
                int attrNumber = Integer.parseInt(matcher.group(16));
                String attrValue = matcher.group(17);
                barcode.setAtr(attrNumber);
                barcode.setValue(attrValue);
            }
            
            // Parse unique ID if present
            if (matcher.group(18) != null) {
                barcode.setUniqueId(matcher.group(18));
            }
            
            features.getFeatures().add(barcode);
        }
    }
}