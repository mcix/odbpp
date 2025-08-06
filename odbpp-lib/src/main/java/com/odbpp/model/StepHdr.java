package com.odbpp.model;

import lombok.Data;
import java.util.List;

/**
 * Represents a stephdr file.
 * This file is mandatory for each step.
 */
@Data
public class StepHdr {
    private String units;
    private double xDatum;
    private double yDatum;
    private int id;
    private double xOrigin;
    private double yOrigin;
    private String affectingBom;
    private boolean affectingBomChanged;
    private List<StepRepeat> stepRepeats;

    @Data
    public static class StepRepeat {
        private String name;
        private double x;
        private double y;
        private double dx;
        private double dy;
        private int nx;
        private int ny;
        private double angle;
        private boolean flip;
        private boolean mirror;
    }
}
