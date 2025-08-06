package com.odbpp.model;

public enum Polarity {
    POSITIVE("P"),
    NEGATIVE("N");
    
    private final String value;
    
    Polarity(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static Polarity fromString(String value) {
        if ("P".equals(value)) {
            return POSITIVE;
        } else if ("N".equals(value)) {
            return NEGATIVE;
        }
        throw new IllegalArgumentException("Invalid polarity value: " + value);
    }
}
