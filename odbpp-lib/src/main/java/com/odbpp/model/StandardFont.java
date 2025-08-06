package com.odbpp.model;

import lombok.Data;
import java.util.List;

/**
 * Represents the standard font file. Optional.
 */
@Data
public class StandardFont {
    private double xSize;
    private double ySize;
    private double offset;
    private List<CharacterDefinition> characters;

    @Data
    public static class CharacterDefinition {
        private char character;
        private List<LineDefinition> lines;
    }

    @Data
    public static class LineDefinition {
        private double xs, ys, xe, ye;
        private char polarity;
        private char shape;
        private double width;
    }
}
