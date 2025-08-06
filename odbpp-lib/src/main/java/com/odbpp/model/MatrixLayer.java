package com.odbpp.model;

import lombok.Data;

@Data
public class MatrixLayer {
    private int row;
    private String context;
    private String type;
    private String name;
    private String polarity;
    private String startName;
    private String endName;
    private String oldName; // Optional
    private String addType; // Optional
    private String dielectricType; // Optional
    private String dielectricName; // Optional
    private String form; // Optional
    private int cuTop; // Optional
    private int cuBottom; // Optional
    private int ref; // Optional
    private int color; // Optional
    private int id; // Optional
}
