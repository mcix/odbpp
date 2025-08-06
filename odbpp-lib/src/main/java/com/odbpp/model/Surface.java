package com.odbpp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a surface definition in ODB++ format.
 * A surface definition must have at least one polygon defined. The surface usually consists of multiple records.
 * 
 * Format: S <polarity> <dcode>;<atr>=<value>,...;ID=<id>
 *         <polygon_1>
 *         ...
 *         <polygon_n>
 *         SE
 * 
 * The S line is followed by polygon definitions. Each polygon is a collection of segments (lines without width) 
 * and curves (arcs without width). The polygons must meet the restrictions described in "Surfaces".
 * 
 * It is recommended that each polygon be represented as a single island, because a multi-island polygon is
 * electrically disconnected. As a single feature, it should be connected to a single net.
 * 
 * A polygon begins with an OB command, contains OS (segment) or OC (arc) commands, and ends with an OE command:
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
@EqualsAndHashCode(callSuper = true)
public class Surface extends Feature {
    /**
     * Polarity: P for positive, N for negative
     */
    private Polarity polarity;
    
    /**
     * Gerber dcode number or Excellon tool number (0 if not defined)
     */
    private int dcode;
    
    /**
     * Map of attribute numbers to their values
     * Key: attribute number (referencing an attribute from the feature attribute names section)
     * Value: attribute value that depends on the type of attribute:
     *   - BOOLEAN: No value is necessary. If the index of the attribute is listed, the attribute is set to TRUE.
     *   - FLOAT or INTEGER: A number.
     *   - OPTION: An option number.
     *   - TEXT: A number referencing the feature attribute text strings section of the lookup table.
     */
    private Map<Integer, String> attributes = new HashMap<>();
    
    /**
     * Unique identifier for the feature (ID=<id>)
     */
    private String uniqueId;
    
    /**
     * List of contour polygons that make up this surface.
     * Each polygon is a collection of segments (lines without width) and curves (arcs without width).
     * A surface must have at least one polygon defined.
     */
    private List<ContourPolygon> polygons = new ArrayList<>();
    
    /**
     * Validates that the surface has at least one polygon defined as required by the specification.
     * 
     * @return true if the surface is valid (has at least one polygon)
     */
    public boolean isValid() {
        return polygons != null && !polygons.isEmpty();
    }
    
    /**
     * Adds a polygon to this surface.
     * 
     * @param polygon the polygon to add
     */
    public void addPolygon(ContourPolygon polygon) {
        if (polygons == null) {
            polygons = new ArrayList<>();
        }
        polygons.add(polygon);
    }
}