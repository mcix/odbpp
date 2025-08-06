package com.odbpp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Profile {
    private List<Surface> surfaces = new ArrayList<>();
}