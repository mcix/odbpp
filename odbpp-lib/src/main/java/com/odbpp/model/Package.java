package com.odbpp.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class Package {
    private String name;
    private int index;
    private List<Pin> pins;
    private Map<String, Pin> pinsByName;
}
