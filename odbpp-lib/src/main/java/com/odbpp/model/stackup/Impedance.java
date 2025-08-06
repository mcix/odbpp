package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Impedance {
    @JacksonXmlProperty(isAttribute = true)
    private String ImpName;
    // ... other impedance properties
}
