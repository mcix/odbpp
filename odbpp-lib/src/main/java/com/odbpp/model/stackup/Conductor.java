package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Conductor {
    @JacksonXmlProperty(isAttribute = true)
    private String ConductorType;
    @JacksonXmlProperty(isAttribute = true)
    private double CopperWeight_oz_ft2;
}
