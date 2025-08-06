package com.odbpp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Barcode extends Feature {
    // Text location (bottom left of first character for 0 orientation)
    private double x;
    private double y;
    
    // Barcode name (currently must be UPC39)
    private String barcodeName;
    
    // Font name
    private String font;
    
    // Polarity: P for positive, N for negative
    private Polarity polarity;
    
    // Text orientation definition
    // 0-7: legacy values for backward compatibility
    // 8: any angle rotation, no mirror
    // 9: any angle rotation, mirror in x-axis
    private int orientDef;
    
    // Rotation angle when orient_def is 8 or 9
    private double orientDefRotation;
    
    // Element width (inches or mm)
    private double width;
    
    // Barcode height (inches or mm)
    private double height;
    
    // Full ASCII: Y for full ASCII, N for partial ASCII
    private String fullAscii;
    
    // Checksum: Y for checksum, N for no checksum
    private String checksum;
    
    // Background: Y for inverted background, N for no background
    private String background;
    
    // Additional string: Y for adding a text string, N for only the barcode
    private String additionalString;
    
    // Additional string position: T for top, B for bottom
    private String additionalStringPosition;
    
    // Text string, enclosed in single quotes, centered on top or bottom of the barcode
    private String text;
    
    // Attribute number, referencing an attribute from the feature attribute names section
    private int atr;
    
    // Attribute value that depends on the type of attribute
    private String value;
    
    // Unique identifier for the feature
    private String uniqueId;
} 