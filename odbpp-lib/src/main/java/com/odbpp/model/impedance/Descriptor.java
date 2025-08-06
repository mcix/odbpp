package com.odbpp.model.impedance;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Descriptor {
    @JacksonXmlProperty(isAttribute = true)
    private int Id;
    @JacksonXmlProperty(isAttribute = true)
    private String TraceLayerName;

    private RequiredImpedance RequiredImpedance;
    private OriginalTraceWidth OriginalTraceWidth;
    private OriginalSpacing OriginalSpacing;
    private RefPlane RefPlane;
}
