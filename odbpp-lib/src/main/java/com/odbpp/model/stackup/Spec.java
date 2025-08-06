package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import java.util.List;

@Data
public class Spec {
    @JacksonXmlProperty(isAttribute = true)
    private String SpecName;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Material")
    private List<Material> material;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Impedance")
    private List<Impedance> impedance;
}
