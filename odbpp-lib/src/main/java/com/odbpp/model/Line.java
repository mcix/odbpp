package com.odbpp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Line extends Feature {
    // Start point coordinates
    private double xs;
    private double ys;
    
    // End point coordinates
    private double xe;
    private double ye;
    
    // Symbol number - index in the feature symbol names section
    private int symbolNumber;
    
    // Polarity: P for positive, N for negative
    private Polarity polarity;
    
    // Gerber dcode number or Excellon tool number (0 if not defined)
    private int dcode;
    
    // Attribute number, referencing an attribute from the feature attribute names section
    private Integer attributeNumber;
    
    // Attribute value that depends on the type of attribute
    private String attributeValue;
    
    // Unique identifier for the feature
    private String uniqueId;
}