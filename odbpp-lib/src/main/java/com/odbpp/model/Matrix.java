package com.odbpp.model;

import lombok.Data;
import java.util.List;

/**
 * Represents the product model matrix from the matrix/matrix file.
 * This file is mandatory.
 */
@Data
public class Matrix {
    private List<MatrixLayer> layers;
    private List<Step> steps;
}
