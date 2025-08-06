package com.odbpp.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a component record (CMP) in ODB++ format.
 * 
 * According to the ODB++ specification, each CMP line is followed by 0 or more property
 * (PRP) records, and 0 or more TOP records. The pkg_ref field references the sequential
 * order of the PKG records in the eda/data file, starting from 0.
 * 
 * Format: CMP <pkg_ref> <x> <y> <rot> <mirror> <comp_name> <part_name>; <attributes>;ID=<id>
 * 
 * Field explanations:
 * - pkg_ref: Reference number of the package in the eda/data file
 * - x, y: Board location of the component in inches or mm
 * - rot: Rotation of the component, in degrees, clockwise
 * - mirror: N for not mirrored, M for mirrored
 * - comp_name: Unique reference designator (component name); single string without spaces
 * - part_name: Part identification; single string without spaces
 * - attributes: Comma-separated list of attribute assignments
 * - ID=<id>: Unique identifier for the component
 */
@Data
public class Component {
    /**
     * Reference number of the package in the eda/data file.
     * References the sequential order of PKG records, starting from 0.
     */
    private int pkgRef;
    
    /**
     * X coordinate of the component location on the board (in inches or mm)
     */
    private double x;
    
    /**
     * Y coordinate of the component location on the board (in inches or mm)
     */
    private double y;
    
    /**
     * Rotation of the component in degrees, clockwise
     */
    private double rotation;
    
    /**
     * Mirror setting: N for not mirrored, M for mirrored
     */
    private MirrorType mirror;
    
    /**
     * Unique reference designator (component name).
     * Single string of ASCII characters without spaces.
     * Upper case and lower case characters are not equivalent.
     */
    private String compName;
    
    /**
     * Part identification.
     * Single string of ASCII characters without spaces.
     */
    private String partName;
    
    /**
     * List of component attributes.
     * Comma-separated list of attribute assignments where:
     * - BOOLEAN: n (attribute n is set)
     * - OPTION: n=m (attribute n has value m)
     * - INTEGER: n=i (attribute n has value i)
     * - FLOAT: n=f (attribute n has value f)
     * - TEXT: n=s (attribute n has the value associated with index s in the attribute text string lookup table)
     */
    private List<ComponentAttribute> attributes = new ArrayList<>();
    
    /**
     * Unique identifier for the component (ID=<id>)
     */
    private String uniqueId;
    
    /**
     * List of property records (PRP) that follow this component record
     */
    private List<PropertyRecord> propertyRecords = new ArrayList<>();
    
    /**
     * List of toeprint records (TOP) that follow this component record
     */
    private List<ToeprintRecord> toeprintRecords = new ArrayList<>();
    
    /**
     * Map of attribute assignments for quick lookup.
     * Key: attribute index, Value: attribute value
     */
    private Map<Integer, String> attributeLookupTable = new HashMap<>();
    
    /**
     * Adds a component attribute to this component
     * 
     * @param attribute the component attribute to add
     */
    public void addAttribute(ComponentAttribute attribute) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(attribute);
        
        // Also add to lookup table for quick access
        if (attribute.getValue() != null) {
            attributeLookupTable.put(attribute.getAttributeIndex(), attribute.getValue());
        }
    }
    
    /**
     * Adds a property record to this component
     * 
     * @param propertyRecord the property record to add
     */
    public void addPropertyRecord(PropertyRecord propertyRecord) {
        if (propertyRecords == null) {
            propertyRecords = new ArrayList<>();
        }
        propertyRecords.add(propertyRecord);
    }
    
    /**
     * Adds a toeprint record to this component
     * 
     * @param toeprintRecord the toeprint record to add
     */
    public void addToeprintRecord(ToeprintRecord toeprintRecord) {
        if (toeprintRecords == null) {
            toeprintRecords = new ArrayList<>();
        }
        toeprintRecords.add(toeprintRecord);
    }
    
    /**
     * Gets the attribute value for a given attribute index
     * 
     * @param attributeIndex the attribute index
     * @return the attribute value, or null if not found
     */
    public String getAttributeValue(int attributeIndex) {
        return attributeLookupTable.get(attributeIndex);
    }
    
    /**
     * Checks if the component has a specific attribute set
     * 
     * @param attributeIndex the attribute index
     * @return true if the attribute is set, false otherwise
     */
    public boolean hasAttribute(int attributeIndex) {
        return attributes.stream()
                .anyMatch(attr -> attr.getAttributeIndex() == attributeIndex);
    }
}
