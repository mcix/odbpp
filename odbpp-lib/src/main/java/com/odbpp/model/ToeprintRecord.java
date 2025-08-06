package com.odbpp.model;

import lombok.Data;

/**
 * Represents a toeprint record (TOP) that follows component records.
 * According to the ODB++ specification, toeprint records define the connection points
 * of components to the board.
 * 
 * Format: TOP <pin_number> <x> <y> <rot> <mirror> <net_number> <subnet_number> <name>
 */
@Data
public class ToeprintRecord {
    /**
     * Pin number (references the package pin)
     */
    private int pinNumber;
    
    /**
     * X coordinate of the toeprint location (in inches or mm)
     */
    private double x;
    
    /**
     * Y coordinate of the toeprint location (in inches or mm)
     */
    private double y;
    
    /**
     * Rotation of the toeprint in degrees, clockwise
     */
    private double rotation;
    
    /**
     * Mirror setting: N for not mirrored, M for mirrored
     */
    private MirrorType mirror;
    
    /**
     * Net number (references NET records in the eda/data file)
     */
    private int netNumber;
    
    /**
     * Subnet number (references subnet records in the eda/data file)
     */
    private int subnetNumber;
    
    /**
     * Pin name
     */
    private String name;
} 