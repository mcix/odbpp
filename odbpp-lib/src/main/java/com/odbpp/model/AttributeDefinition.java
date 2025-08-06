package com.odbpp.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AttributeDefinition {
    private String name;
    private AttributeType type;
    private String prompt;
    private List<String> entities;
    private String group;
    private String defaultValue;

    // For TEXT
    private Integer minLen;
    private Integer maxLen;

    // For OPTION
    private List<String> options;
    private List<Boolean> deletedOptions;

    // For INTEGER
    private Integer minValInt;
    private Integer maxValInt;

    // For FLOAT
    private Double minValFloat;
    private Double maxValFloat;
    private String unitType;
    private String units;
}
