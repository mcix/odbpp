package com.odbpp.model;

/**
 * Represents the mirror field in component records.
 * According to the ODB++ specification:
 * - N: Not mirrored
 * - M: Mirrored
 */
public enum MirrorType {
    NOT_MIRRORED("N"),
    MIRRORED("M");
    
    private final String value;
    
    MirrorType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static MirrorType fromString(String value) {
        if ("N".equals(value)) {
            return NOT_MIRRORED;
        } else if ("M".equals(value)) {
            return MIRRORED;
        }
        throw new IllegalArgumentException("Invalid mirror value: " + value);
    }
} 