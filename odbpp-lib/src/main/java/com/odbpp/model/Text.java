package com.odbpp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Text extends Feature {
    // Text location (bottom left of first character for 0 orientation)
    private double x;
    private double y;
    
    // Font name
    private String font;
    
    // P for positive, N for negative
    private Polarity polarity;
    
    // Text orientation definition
    // 0|1|2|3|4|5|6|7|8<rotation>|9<rotation>
    // Legacy values 0-7 or new format 8/9 with rotation
    private String orientDef;
    
    // Character size
    private double xsize; // Character width including the following space
    private double ysize; // Height of a capital character
    
    // Width of character segment (in units of 12 mils)
    private double widthFactor;
    
    // Text string enclosed in single quotes
    // 
    // Dynamic Text Variables:
    // A text string can include dynamic text variables that change textual value according to status or condition.
    // For example, a variable for the date always displays the current date, as set by the system. Dynamic text
    // variables can be located anywhere within a text string. They are distinguished by the prefix $$, as in 
    // $$DATE for the current date.
    // 
    // Example of a dynamic text record:
    // T 11.890963 13.697185 standard P 8 0 5.08 5.08 2.00000 '$$DATE-MMDDYY' 1
    // Where the variable $$DATE-MMDDYY displays the date as 05/30/17.
    // 
    // The ODB++ format supports these dynamic text variables:
    // - DATE-MMDDYY: Date expressed as month/day/year (short)
    // - DATE-DDMMYY: Date expressed as day/month/year (short)
    // - DATE-MMDDYYYY: Date expressed as month/day/year
    // - DATE-DDMMYYYY: Date expressed as day/month/year
    // - DD: Day of month (01-31)
    // - WEEK-DAY: Day of week (Sunday-Saturday)
    // - MM: Month of year (01-14)
    // - YY: Year (14)
    // - YYYY: Year (2014)
    // - WW: Week of year (01-52)
    // - TIME: Current time
    // - JOB: Name of current product module
    // - STEP: Name of step where text is placed
    // - LAYER: Name of layer where text is placed
    // - X: Bottom left x-coordinate of where text is placed (inch)
    // - Y: Bottom left y-coordinate of where text is placed (inch)
    // - X_MM: Bottom left x-coordinate of where text is placed (mm)
    // - Y_MM: Bottom left y-coordinate of where text is placed (mm)
    private String text;
    
    // Version - how first character is placed relative to insertion point
    // 0 - Lower left corner of text limit box coincides with insertion point
    // 1 - Lower left corner of widest character coincides with insertion point
    private int version;
    
    // Attribute number, referencing an attribute from the feature attribute names section
    private Integer atr;
    
    // Attribute value that depends on the type of attribute
    private String value;
}