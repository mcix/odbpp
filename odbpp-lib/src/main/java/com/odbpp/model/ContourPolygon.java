package com.odbpp.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a contour polygon in ODB++ format.
 * A polygon begins with an OB command, contains OS (segment) or OC (arc) commands, and ends with an OE command.
 * 
 * Format:
 * OB <xbs> <ybs> <poly_type>
 * OS <x> <y>
 * ...
 * OC <xe> <ye> <xc> <yc> <cw>
 * ...
 * OE
 * 
 * The last OS or OC coordinate should be the same as the OB coordinate.
 */
@Data
public class ContourPolygon {
    /**
     * Beginning point of the polygon (xbs, ybs from OB command)
     */
    private double xStart;
    private double yStart;
    
    /**
     * Polygon type: I for island, H for hole
     */
    private Type type;
    
    /**
     * List of polygon parts (segments and arcs) that make up this polygon
     */
    private List<PolygonPart> polygonParts = new ArrayList<>();

    public enum Type {
        ISLAND("I"),
        HOLE("H");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Type fromString(String value) {
            for (Type type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid polygon type: " + value);
        }
    }

    /**
     * Represents a part of a polygon - either a segment (OS) or arc (OC).
     */
    @Data
    public static class PolygonPart {
        /**
         * Type of polygon part: SEGMENT (OS) or ARC (OC)
         */
        private Type type;
        
        /**
         * End point coordinates (x, y for segments; xe, ye for arcs)
         */
        private double endX;
        private double endY;
        
        /**
         * Curve center point (only used for arcs: xc, yc)
         */
        private double xCenter;
        private double yCenter;
        
        /**
         * Clockwise direction (only used for arcs: cw - Y for clockwise, N for counter-clockwise)
         */
        private boolean isClockwise;

        public enum Type {
            SEGMENT("OS"),
            ARC("OC");

            private final String value;

            Type(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }

            public static Type fromString(String value) {
                for (Type type : values()) {
                    if (type.value.equals(value)) {
                        return type;
                    }
                }
                throw new IllegalArgumentException("Invalid polygon part type: " + value);
            }
        }
    }
} 