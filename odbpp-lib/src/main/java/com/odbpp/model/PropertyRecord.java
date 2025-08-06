package com.odbpp.model;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a property record (PRP) that follows component records.
 * According to the ODB++ specification, property records have the format:
 * PRP <name> <value>; <float_values>
 * 
 * Where:
 * - name: Property name
 * - value: Property value
 * - float_values: Optional list of floating-point values
 */
@Data
public class PropertyRecord {
    /**
     * Property name
     */
    private String name;
    
    /**
     * Property value
     */
    private String value;
    
    /**
     * Optional list of floating-point values
     */
    private List<Double> floatValues = new ArrayList<>();
    
    /**
     * Adds a float value to the property record
     * 
     * @param floatValue the float value to add
     */
    public void addFloatValue(double floatValue) {
        if (floatValues == null) {
            floatValues = new ArrayList<>();
        }
        floatValues.add(floatValue);
    }
} 