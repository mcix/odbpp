package com.odbpp.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Components {
    private List<Component> components = new ArrayList<>();
}