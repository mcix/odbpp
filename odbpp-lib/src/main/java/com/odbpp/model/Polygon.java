package com.odbpp.model;

import lombok.Data;

@Data
public class Polygon {
    private Type type;
    private double endX;
    private double endY;
    private double xCenter;
    private double yCenter;
    private boolean isClockwise;

    public enum Type {
        SEGMENT,
        ARC
    }
}
