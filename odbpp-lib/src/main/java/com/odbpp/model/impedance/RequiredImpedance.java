package com.odbpp.model.impedance;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class RequiredImpedance {
    @JacksonXmlProperty(isAttribute = true)
    private double ValOhms;
    @JacksonXmlProperty(isAttribute = true)
    private double PlusVal;
    @JacksonXmlProperty(isAttribute = true)
    private double MinusVal;
    @JacksonXmlProperty(isAttribute = true)
    private boolean ValPercent;
}
