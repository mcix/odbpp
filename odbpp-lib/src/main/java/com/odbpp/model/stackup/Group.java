package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import java.util.List;

@Data
public class Group {
    @JacksonXmlProperty(isAttribute = true)
    private String GroupName;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Layer")
    private List<Layer> layer;
}
