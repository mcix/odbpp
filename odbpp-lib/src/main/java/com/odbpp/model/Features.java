package com.odbpp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Features {
    private List<Feature> features = new ArrayList<>();
}