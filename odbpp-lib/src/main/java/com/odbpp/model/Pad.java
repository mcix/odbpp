package com.odbpp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Pad extends Feature {
    // Center point coordinates
    private double x;
    private double y;
    
    // apt_def - symbol definition with optional resize factor
    private int symbolNumber;
    private Double resizeFactor; // null if not resized, otherwise the resize factor
    
    // polarity - P for Positive, N for Negative
    private String polarity;
    
    // dcode - Gerber dcode number or Excellon tool number (0 if not defined)
    private int dcode;
    
    // orient_def - orientation definition
    // For legacy values 0-7: 0=0°, 1=90°, 2=180°, 3=270°, 4=0°+mirror, 5=90°+mirror, 6=180°+mirror, 7=270°+mirror
    // For new format: 8=any angle no mirror, 9=any angle with mirror
    private int orientationType; // 0-9
    private Double customRotation; // null for legacy values, otherwise the rotation angle
    
    // atr - attribute number referencing feature attribute names section
    private Integer attributeNumber;
    
    // value - attribute value (depends on attribute type)
    private String attributeValue;
    
    // ID - unique identifier (inherited from Feature class)
    // The id field is already available from the parent Feature class
}