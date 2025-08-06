package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class DefaultThickness {
    @JacksonXmlProperty(isAttribute = true)
    private double Thickness;
}
