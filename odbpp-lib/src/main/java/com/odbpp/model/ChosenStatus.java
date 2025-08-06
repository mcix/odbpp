package com.odbpp.model;

/**
 * Represents whether a part is chosen from among the alternate parts for the CPN.
 * According to the ODB++ BOM specification:
 * - 0: Not chosen
 * - 1: Chosen (only one part can be a chosen part)
 */
public enum ChosenStatus {
    NOT_CHOSEN(0, "Not chosen"),
    CHOSEN(1, "Chosen");
    
    private final int value;
    private final String description;
    
    ChosenStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ChosenStatus fromValue(int value) {
        for (ChosenStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid chosen status value: " + value);
    }
} 