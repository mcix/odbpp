package com.odbpp.model.impedance;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class OriginalSpacing {
    @JacksonXmlProperty(isAttribute = true)
    private double Val;
    @JacksonXmlProperty(isAttribute = true)
    private String Units;
}
