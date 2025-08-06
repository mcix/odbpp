package com.odbpp.model.impedance;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class RefPlane {
    @JacksonXmlProperty(isAttribute = true)
    private String Val;
}
