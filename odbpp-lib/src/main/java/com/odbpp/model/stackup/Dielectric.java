package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Dielectric {
    @JacksonXmlProperty(isAttribute = true)
    private String DielectricType;
    @JacksonXmlProperty(isAttribute = true)
    private String OtherSubType;
}
