package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Layer {
    @JacksonXmlProperty(isAttribute = true)
    private String LayerName;
    @JacksonXmlProperty(isAttribute = true)
    private String LayerType;
    @JacksonXmlProperty(isAttribute = true)
    private String Side;
    // ... other layer properties
}
