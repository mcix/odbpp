package com.odbpp.model;

/**
 * Represents the qualification status of a part (vendor+mpn) for production.
 * According to the ODB++ BOM specification:
 * - -1: Not qualified
 * - 0: Unknown
 * - 1: Qualified
 */
public enum QualificationStatus {
    NOT_QUALIFIED(-1, "Not qualified"),
    UNKNOWN(0, "Unknown"),
    QUALIFIED(1, "Qualified");
    
    private final int value;
    private final String description;
    
    QualificationStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static QualificationStatus fromValue(int value) {
        for (QualificationStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid qualification status value: " + value);
    }
} 