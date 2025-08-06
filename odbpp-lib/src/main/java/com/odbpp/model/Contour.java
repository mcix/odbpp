package com.odbpp.model;

import lombok.Data;
import java.util.List;

@Data
public class Contour {
    private Type type;
    private double xStart;
    private double yStart;
    private List<ContourPolygon> polygons;

    public enum Type {
        ISLAND,
        HOLE
    }
}
