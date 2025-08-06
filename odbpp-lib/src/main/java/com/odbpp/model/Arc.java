package com.odbpp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Arc extends Feature {
    // Start point coordinates
    private double xs;
    private double ys;
    
    // End point coordinates  
    private double xe;
    private double ye;
    
    // Center point coordinates
    private double xc;
    private double yc;
    
    // Symbol number - index in the feature symbol names section
    private int symbolNumber;
    
    // Polarity: P for positive, N for negative
    private Polarity polarity;
    
    // Gerber dcode number or Excellon tool number (0 if not defined)
    private int dcode;
    
    // Clockwise direction: Y for clockwise, N for counter clockwise
    private String cw;
    
    // Attribute number, referencing an attribute from the feature attribute names section
    private int atr;
    
    // Attribute value that depends on the type of attribute
    private String value;
    
    // Unique identifier for the feature
    private String uniqueId;
}