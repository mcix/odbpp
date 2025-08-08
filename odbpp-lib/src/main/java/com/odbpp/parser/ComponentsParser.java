package com.odbpp.parser;

import com.odbpp.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for ODB++ component files.
 * Parses component data from steps/{step_name}/layers/comp_+_top or comp_+_bot files.
 */
@Slf4j
public class ComponentsParser {

    @Getter
    private String units = "MM";

    @Getter
    private double scale = 1.0;

    @Getter
    private int id;

    @Getter
    private final List<Component> components = new ArrayList<>();

    @Getter
    private final Map<Integer, String> attributeNames = new HashMap<>();

    @Getter
    private final Map<Integer, String> attributeTextStrings = new HashMap<>();

    // Regex patterns for parsing
    private static final Pattern UNITS_PATTERN = Pattern.compile("UNITS=(.+)");
    private static final Pattern ID_PATTERN = Pattern.compile("ID=(\\d+)");
    private static final Pattern ATTRIBUTE_NAME_PATTERN = Pattern.compile("@(\\d+)\\s+(.+)");
    private static final Pattern ATTRIBUTE_TEXT_PATTERN = Pattern.compile("&(\\d+)\\s+(.+)");
    private static final Pattern CMP_PATTERN = Pattern.compile("CMP\\s+(\\d+)\\s+([\\d.-]+)\\s+([\\d.-]+)\\s+([\\d.-]+)\\s+([NM])\\s+(\\S+)\\s+(\\S+)\\s*;([^;]*);ID=(\\d+)");
    private static final Pattern PRP_PATTERN = Pattern.compile("PRP\\s+(\\S+)\\s+'([^']*)'");
    private static final Pattern TOP_PATTERN = Pattern.compile("TOP\\s+(\\d+)\\s+([\\d.-]+)\\s+([\\d.-]+)\\s+([\\d.-]+)\\s+([NM])\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

    /**
     * Parse component file from the given file path
     */
    public void parseFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            parseFromReader(reader);
        }
    }

    /**
     * Parse component data from a BufferedReader
     */
    public void parseFromReader(BufferedReader reader) throws IOException {
        String line;
        Component currentComponent = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // Parse header information
            if (parseLine(line)) {
                continue;
            }

            // Parse component record
            Matcher cmpMatcher = CMP_PATTERN.matcher(line);
            if (cmpMatcher.matches()) {
                currentComponent = parseComponentRecord(cmpMatcher);
                components.add(currentComponent);
                continue;
            }

            // Parse property record
            if (currentComponent != null) {
                Matcher prpMatcher = PRP_PATTERN.matcher(line);
                if (prpMatcher.matches()) {
                    PropertyRecord property = parsePropertyRecord(prpMatcher);
                    currentComponent.addPropertyRecord(property);
                    continue;
                }

                // Parse toeprint record
                Matcher topMatcher = TOP_PATTERN.matcher(line);
                if (topMatcher.matches()) {
                    ToeprintRecord toeprint = parseToeprintRecord(topMatcher);
                    currentComponent.addToeprintRecord(toeprint);
                    continue;
                }
            }
        }

        log.info("Parsed {} components from file", components.size());
    }

    /**
     * Parse header lines (UNITS, ID, attribute definitions)
     */
    private boolean parseLine(String line) {
        // Parse units
        Matcher unitsMatcher = UNITS_PATTERN.matcher(line);
        if (unitsMatcher.matches()) {
            units = unitsMatcher.group(1).trim().toUpperCase();
            scale = calculateScale(units);
            log.debug("Units: {}, Scale: {}", units, scale);
            return true;
        }

        // Parse ID
        Matcher idMatcher = ID_PATTERN.matcher(line);
        if (idMatcher.matches()) {
            id = Integer.parseInt(idMatcher.group(1));
            log.debug("ID: {}", id);
            return true;
        }

        // Parse attribute names
        Matcher attrNameMatcher = ATTRIBUTE_NAME_PATTERN.matcher(line);
        if (attrNameMatcher.matches()) {
            int index = Integer.parseInt(attrNameMatcher.group(1));
            String name = attrNameMatcher.group(2).trim();
            attributeNames.put(index, name);
            log.debug("Attribute name {}: {}", index, name);
            return true;
        }

        // Parse attribute text strings
        Matcher attrTextMatcher = ATTRIBUTE_TEXT_PATTERN.matcher(line);
        if (attrTextMatcher.matches()) {
            int index = Integer.parseInt(attrTextMatcher.group(1));
            String text = attrTextMatcher.group(2).trim();
            attributeTextStrings.put(index, text);
            log.debug("Attribute text {}: {}", index, text);
            return true;
        }

        return false;
    }

    /**
     * Parse a component record
     */
    private Component parseComponentRecord(Matcher matcher) {
        Component component = new Component();

        component.setPkgRef(Integer.parseInt(matcher.group(1)));
        component.setX(Double.parseDouble(matcher.group(2)) * scale);
        component.setY(Double.parseDouble(matcher.group(3)) * scale);
        component.setRotation(Double.parseDouble(matcher.group(4)));
        component.setMirror(MirrorType.fromString(matcher.group(5)));
        component.setCompName(matcher.group(6));
        component.setPartName(matcher.group(7));
        component.setUniqueId(matcher.group(9));

        // Parse attributes if present
        String attributesStr = matcher.group(8);
        if (!attributesStr.isEmpty()) {
            parseComponentAttributes(component, attributesStr);
        }

        log.debug("Parsed component: {} at ({}, {})", component.getCompName(),
                component.getX(), component.getY());

        return component;
    }

    /**
     * Parse component attributes from the attributes string
     */
    private void parseComponentAttributes(Component component, String attributesStr) {
        String[] attributePairs = attributesStr.split(",");
        for (String pair : attributePairs) {
            pair = pair.trim();
            if (pair.isEmpty()) continue;

            if (pair.contains("=")) {
                String[] parts = pair.split("=", 2);
                int attrIndex = Integer.parseInt(parts[0]);
                String value = parts[1];

                // Determine attribute type based on value
                ComponentAttribute attr;
                if (value.matches("\\d+")) {
                    // Could be integer or text reference
                    if (attributeTextStrings.containsKey(Integer.parseInt(value))) {
                        attr = ComponentAttribute.createText(attrIndex, Integer.parseInt(value));
                    } else {
                        attr = ComponentAttribute.createInteger(attrIndex, Integer.parseInt(value));
                    }
                } else if (value.matches("[\\d.-]+")) {
                    attr = ComponentAttribute.createFloat(attrIndex, Double.parseDouble(value));
                } else {
                    attr = ComponentAttribute.createOption(attrIndex, value);
                }

                component.addAttribute(attr);
            } else {
                // Boolean attribute
                int attrIndex = Integer.parseInt(pair);
                ComponentAttribute attr = ComponentAttribute.createBoolean(attrIndex);
                component.addAttribute(attr);
            }
        }
    }

    /**
     * Parse a property record
     */
    private PropertyRecord parsePropertyRecord(Matcher matcher) {
        PropertyRecord property = new PropertyRecord();
        property.setName(matcher.group(1));
        property.setValue(matcher.group(2));

        log.debug("Parsed property: {} = {}", property.getName(), property.getValue());

        return property;
    }

    /**
     * Parse a toeprint record
     */
    private ToeprintRecord parseToeprintRecord(Matcher matcher) {
        ToeprintRecord toeprint = new ToeprintRecord();

        toeprint.setPinNumber(Integer.parseInt(matcher.group(1)));
        toeprint.setX(Double.parseDouble(matcher.group(2)) * scale);
        toeprint.setY(Double.parseDouble(matcher.group(3)) * scale);
        toeprint.setRotation(Double.parseDouble(matcher.group(4)));
        toeprint.setMirror(MirrorType.fromString(matcher.group(5)));
        toeprint.setNetNumber(Integer.parseInt(matcher.group(6)));
        toeprint.setSubnetNumber(Integer.parseInt(matcher.group(7)));
        toeprint.setName(matcher.group(8));

        log.debug("Parsed toeprint: pin {} at ({}, {})", toeprint.getPinNumber(),
                toeprint.getX(), toeprint.getY());

        return toeprint;
    }

    /**
     * Calculate scale factor based on units
     */
    private double calculateScale(String units) {
        switch (units.toUpperCase()) {
            case "MM":
            case "MILLIMETERS":
                return 1.0;
            case "INCH":
            case "IN":
                return 25.4; // Convert inches to mm
            case "MIL":
                return 0.0254; // Convert mils to mm
            default:
                log.warn("Unknown units: {}, defaulting to MM", units);
                return 1.0;
        }
    }

    /**
     * Get component information suitable for SVG rendering
     */
    public List<ComponentInfo> getComponentInfoForSVG() {
        List<ComponentInfo> componentInfos = new ArrayList<>();

        for (Component component : components) {
            ComponentInfo info = new ComponentInfo();
            info.setName(component.getCompName());
            info.setPartName(component.getPartName());
            info.setX(component.getX());
            info.setY(component.getY());
            info.setRotation(component.getRotation());
            info.setMirror(component.getMirror());

            // Extract pin1 location from first toeprint
            if (!component.getToeprintRecords().isEmpty()) {
                ToeprintRecord firstToeprint = component.getToeprintRecords().get(0);
                info.setPin1X(firstToeprint.getX());
                info.setPin1Y(firstToeprint.getY());
            }

            // Try to extract component size from properties or attributes
            extractComponentSize(component, info);

            componentInfos.add(info);
        }

        return componentInfos;
    }

    /**
     * Extract component size information from properties or attributes
     */
    private void extractComponentSize(Component component, ComponentInfo info) {
        // Look for size information in properties
        for (PropertyRecord prop : component.getPropertyRecords()) {
            String name = prop.getName().toUpperCase();
            String value = prop.getValue();

            if (name.contains("FOOTPRINT") || name.contains("PACKAGE")) {
                info.setPackageType(value);
            }

            // Try to extract dimensions from footprint name (e.g., "0201", "0402", etc.)
            if (value.matches(".*\\d{4}.*")) {
                String[] parts = value.replaceAll("[^\\d]", " ").trim().split("\\s+");
                if (parts.length >= 1 && parts[0].length() == 4) {
                    try {
                        double width = Double.parseDouble(parts[0].substring(0, 2)) / 100.0;
                        double height = Double.parseDouble(parts[0].substring(2, 4)) / 100.0;
                        info.setWidth(width);
                        info.setHeight(height);
                    } catch (NumberFormatException e) {
                        // Ignore parsing errors
                    }
                }
            }
        }

        // Set default size if not found
        if (info.getWidth() == 0 || info.getHeight() == 0) {
            info.setWidth(1.0); // Default 1mm
            info.setHeight(1.0); // Default 1mm
        }
    }

    /**
     * Component information for SVG rendering
     */
    @Getter
    public static class ComponentInfo {
        private String name;
        private String partName;
        private String packageType;
        private double x;
        private double y;
        private double rotation;
        private MirrorType mirror;
        private double pin1X;
        private double pin1Y;
        private double width = 1.0;
        private double height = 1.0;

        // Setters
        public void setName(String name) { this.name = name; }
        public void setPartName(String partName) { this.partName = partName; }
        public void setPackageType(String packageType) { this.packageType = packageType; }
        public void setX(double x) { this.x = x; }
        public void setY(double y) { this.y = y; }
        public void setRotation(double rotation) { this.rotation = rotation; }
        public void setMirror(MirrorType mirror) { this.mirror = mirror; }
        public void setPin1X(double pin1X) { this.pin1X = pin1X; }
        public void setPin1Y(double pin1Y) { this.pin1Y = pin1Y; }
        public void setWidth(double width) { this.width = width; }
        public void setHeight(double height) { this.height = height; }
    }
}
