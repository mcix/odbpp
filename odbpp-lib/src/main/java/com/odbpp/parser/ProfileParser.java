package com.odbpp.parser;

import com.odbpp.model.ContourPolygon;
import com.odbpp.model.Profile;
import com.odbpp.model.Surface;
import com.odbpp.model.Polarity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for ODB++ profile files.
 * Parses board outline data from steps/{step_name}/profile files.
 */
@Slf4j
public class ProfileParser {
    @Getter
    private String units = "MM";

    @Getter
    private double scale = 1.0;

    @Getter
    private int id;

    @Getter
    private final List<Surface> surfaces = new ArrayList<>();

    // Current parsing state
    private Surface currentSurface = null;
    private ContourPolygon currentPolygon = null;

    // Regex patterns for parsing
    private static final Pattern UNITS_PATTERN = Pattern.compile("UNITS=(.+)");
    private static final Pattern ID_PATTERN = Pattern.compile("ID=(\\d+)");
    private static final Pattern FEATURES_COUNT_PATTERN = Pattern.compile("F\\s+(\\d+)");
    private static final Pattern SURFACE_PATTERN = Pattern.compile("S\\s+([PN])\\s+(\\d+)(?:;([^;]*);ID=(\\d+))?");
    private static final Pattern OUTLINE_BEGIN_PATTERN = Pattern.compile("OB\\s+([\\d.-]+)\\s+([\\d.-]+)\\s+([IH])");
    private static final Pattern OUTLINE_SEGMENT_PATTERN = Pattern.compile("OS\\s+([\\d.-]+)\\s+([\\d.-]+)");
    private static final Pattern OUTLINE_CURVE_PATTERN = Pattern.compile("OC\\s+([\\d.-]+)\\s+([\\d.-]+)\\s+([\\d.-]+)\\s+([\\d.-]+)\\s+([YN])");
    private static final Pattern OUTLINE_END_PATTERN = Pattern.compile("OE");
    private static final Pattern SURFACE_END_PATTERN = Pattern.compile("SE");

    /**
     * Parse profile file from the given file path
     */
    public void parseFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            parseFromReader(reader);
        }
    }

    /**
     * Parse profile data from a BufferedReader
     */
    public void parseFromReader(BufferedReader reader) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            parseLine(line);
        }

        log.info("Parsed {} surfaces from profile", surfaces.size());
    }

    /**
     * Parse a single line from the profile file
     */
    private void parseLine(String line) {
        // Parse units
        Matcher unitsMatcher = UNITS_PATTERN.matcher(line);
        if (unitsMatcher.matches()) {
            units = unitsMatcher.group(1).trim().toUpperCase();
            scale = calculateScale(units);
            log.debug("Units: {}, Scale: {}", units, scale);
            return;
        }

        // Parse ID
        Matcher idMatcher = ID_PATTERN.matcher(line);
        if (idMatcher.matches()) {
            id = Integer.parseInt(idMatcher.group(1));
            log.debug("Profile ID: {}", id);
            return;
        }

        // Parse features count
        Matcher featuresMatcher = FEATURES_COUNT_PATTERN.matcher(line);
        if (featuresMatcher.matches()) {
            int featuresCount = Integer.parseInt(featuresMatcher.group(1));
            log.debug("Expected features count: {}", featuresCount);
            return;
        }

        // Parse surface start
        Matcher surfaceMatcher = SURFACE_PATTERN.matcher(line);
        if (surfaceMatcher.matches()) {
            parseSurfaceStart(surfaceMatcher);
            return;
        }

        // Parse outline begin
        Matcher obMatcher = OUTLINE_BEGIN_PATTERN.matcher(line);
        if (obMatcher.matches()) {
            parseOutlineBegin(obMatcher);
            return;
        }

        // Parse outline segment
        Matcher osMatcher = OUTLINE_SEGMENT_PATTERN.matcher(line);
        if (osMatcher.matches()) {
            parseOutlineSegment(osMatcher);
            return;
        }

        // Parse outline curve
        Matcher ocMatcher = OUTLINE_CURVE_PATTERN.matcher(line);
        if (ocMatcher.matches()) {
            parseOutlineCurve(ocMatcher);
            return;
        }

        // Parse outline end
        if (OUTLINE_END_PATTERN.matcher(line).matches()) {
            parseOutlineEnd();
            return;
        }

        // Parse surface end
        if (SURFACE_END_PATTERN.matcher(line).matches()) {
            parseSurfaceEnd();
            return;
        }

        log.debug("Unrecognized line: {}", line);
    }

    /**
     * Parse surface start record
     */
    private void parseSurfaceStart(Matcher matcher) {
        currentSurface = new Surface();
        currentSurface.setPolarity(Polarity.fromString(matcher.group(1)));
        currentSurface.setDcode(Integer.parseInt(matcher.group(2)));

        // Parse attributes if present
        if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
            // TODO: Parse surface attributes
        }

        // Parse unique ID if present
        if (matcher.group(4) != null) {
            currentSurface.setUniqueId(matcher.group(4));
        }

        log.debug("Started surface: polarity={}, dcode={}",
                currentSurface.getPolarity(), currentSurface.getDcode());
    }

    /**
     * Parse outline begin record
     */
    private void parseOutlineBegin(Matcher matcher) {
        if (currentSurface == null) {
            log.warn("OB command found without surface context");
            return;
        }

        currentPolygon = new ContourPolygon();
        currentPolygon.setXStart(Double.parseDouble(matcher.group(1)) * scale);
        currentPolygon.setYStart(Double.parseDouble(matcher.group(2)) * scale);
        currentPolygon.setType(ContourPolygon.Type.fromString(matcher.group(3)));

        log.debug("Started polygon: type={}, start=({}, {})",
                currentPolygon.getType(), currentPolygon.getXStart(), currentPolygon.getYStart());
    }

    /**
     * Parse outline segment record
     */
    private void parseOutlineSegment(Matcher matcher) {
        if (currentPolygon == null) {
            log.warn("OS command found without polygon context");
            return;
        }

        ContourPolygon.PolygonPart segment = new ContourPolygon.PolygonPart();
        segment.setType(ContourPolygon.PolygonPart.Type.SEGMENT);
        segment.setEndX(Double.parseDouble(matcher.group(1)) * scale);
        segment.setEndY(Double.parseDouble(matcher.group(2)) * scale);

        currentPolygon.getPolygonParts().add(segment);

        log.debug("Added segment to ({}, {})", segment.getEndX(), segment.getEndY());
    }

    /**
     * Parse outline curve record
     */
    private void parseOutlineCurve(Matcher matcher) {
        if (currentPolygon == null) {
            log.warn("OC command found without polygon context");
            return;
        }

        ContourPolygon.PolygonPart arc = new ContourPolygon.PolygonPart();
        arc.setType(ContourPolygon.PolygonPart.Type.ARC);
        arc.setEndX(Double.parseDouble(matcher.group(1)) * scale);
        arc.setEndY(Double.parseDouble(matcher.group(2)) * scale);
        arc.setXCenter(Double.parseDouble(matcher.group(3)) * scale);
        arc.setYCenter(Double.parseDouble(matcher.group(4)) * scale);
        arc.setClockwise("Y".equals(matcher.group(5)));

        currentPolygon.getPolygonParts().add(arc);

        log.debug("Added arc to ({}, {}) with center ({}, {}), clockwise={}",
                arc.getEndX(), arc.getEndY(), arc.getXCenter(), arc.getYCenter(), arc.isClockwise());
    }

    /**
     * Parse outline end record
     */
    private void parseOutlineEnd() {
        if (currentPolygon == null || currentSurface == null) {
            log.warn("OE command found without proper context");
            return;
        }

        currentSurface.addPolygon(currentPolygon);
        log.debug("Completed polygon with {} parts", currentPolygon.getPolygonParts().size());
        currentPolygon = null;
    }

    /**
     * Parse surface end record
     */
    private void parseSurfaceEnd() {
        if (currentSurface == null) {
            log.warn("SE command found without surface context");
            return;
        }

        surfaces.add(currentSurface);
        log.debug("Completed surface with {} polygons", currentSurface.getPolygons().size());
        currentSurface = null;
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
     * Get board outline information suitable for SVG rendering
     */
    public BoardOutline getBoardOutlineForSVG() {
        BoardOutline outline = new BoardOutline();

        for (Surface surface : surfaces) {
            if (surface.getPolarity() == Polarity.POSITIVE) {
                for (ContourPolygon polygon : surface.getPolygons()) {
                    if (polygon.getType() == ContourPolygon.Type.ISLAND) {
                        outline.addIsland(convertPolygonToSVGPath(polygon));
                    } else {
                        outline.addHole(convertPolygonToSVGPath(polygon));
                    }
                }
            }
        }

        return outline;
    }

    /**
     * Convert a contour polygon to SVG path data
     */
    private String convertPolygonToSVGPath(ContourPolygon polygon) {
        StringBuilder pathData = new StringBuilder();

        // Move to start point
        pathData.append(String.format("M %.3f %.3f", polygon.getXStart(), polygon.getYStart()));

        double currentX = polygon.getXStart();
        double currentY = polygon.getYStart();

        for (ContourPolygon.PolygonPart part : polygon.getPolygonParts()) {
            if (part.getType() == ContourPolygon.PolygonPart.Type.SEGMENT) {
                pathData.append(String.format(" L %.3f %.3f", part.getEndX(), part.getEndY()));
                currentX = part.getEndX();
                currentY = part.getEndY();
            } else if (part.getType() == ContourPolygon.PolygonPart.Type.ARC) {
                // Calculate radius
                double radius = Math.sqrt(Math.pow(currentX - part.getXCenter(), 2) +
                        Math.pow(currentY - part.getYCenter(), 2));

                // Calculate angle span to determine large arc flag
                double startAngle = Math.atan2(currentY - part.getYCenter(), currentX - part.getXCenter());
                double endAngle = Math.atan2(part.getEndY() - part.getYCenter(), part.getEndX() - part.getXCenter());
                double angleDiff = endAngle - startAngle;

                // Normalize angle difference
                while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
                while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

                int largeArcFlag = Math.abs(angleDiff) > Math.PI ? 1 : 0;
                int sweepFlag = part.isClockwise() ? 1 : 0;

                pathData.append(String.format(" A %.3f %.3f 0 %d %d %.3f %.3f",
                        radius, radius, largeArcFlag, sweepFlag, part.getEndX(), part.getEndY()));

                currentX = part.getEndX();
                currentY = part.getEndY();
            }
        }

        pathData.append(" Z"); // Close path

        return pathData.toString();
    }

    /**
     * Board outline information for SVG rendering
     */
    @Getter
    public static class BoardOutline {
        private final List<String> islands = new ArrayList<>();
        private final List<String> holes = new ArrayList<>();

        public void addIsland(String pathData) {
            islands.add(pathData);
        }

        public void addHole(String pathData) {
            holes.add(pathData);
        }

        /**
         * Calculate bounding box of the board outline
         */
        public BoundingBox getBoundingBox() {
            // This is a simplified implementation
            // In a real implementation, you would parse the path data to find min/max coordinates
            return new BoundingBox(-50, -50, 100, 100); // Default values
        }
    }

    /**
     * Bounding box representation
     */
    @Getter
    public static class BoundingBox {
        private final double minX;
        private final double minY;
        private final double width;
        private final double height;

        public BoundingBox(double minX, double minY, double width, double height) {
            this.minX = minX;
            this.minY = minY;
            this.width = width;
            this.height = height;
        }

        public double getMaxX() {
            return minX + width;
        }

        public double getMaxY() {
            return minY + height;
        }
    }
}