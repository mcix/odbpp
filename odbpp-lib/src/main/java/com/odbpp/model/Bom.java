package com.odbpp.model;

import lombok.Data;
import java.util.List;

@Data
public class Bom {
    private String name;
    private List<BomItem> items;
}
