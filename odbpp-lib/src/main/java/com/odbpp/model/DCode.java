package com.odbpp.model;

import lombok.Data;

/**
 * Represents a D-code in a wheel file.
 */
@Data
public class DCode {
    private int code;
    private String symbolName;
    private double angle;
    private boolean mirror;
}
