package com.odbpp.model.impedance;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "Impedances")
public class ImpedanceFile {
    @JacksonXmlProperty(isAttribute = true)
    private String Version;
    @JacksonXmlProperty(isAttribute = true)
    private int MaxImpIdValUsed;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Descriptor")
    private List<Descriptor> descriptor;
}
