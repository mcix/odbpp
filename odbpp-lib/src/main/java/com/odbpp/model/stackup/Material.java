package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Material {
    @JacksonXmlProperty(isAttribute = true)
    private String MaterialName;

    private Dielectric Dielectric;
    private Conductor Conductor;
    private DefaultThickness Default_Thickness;
}
