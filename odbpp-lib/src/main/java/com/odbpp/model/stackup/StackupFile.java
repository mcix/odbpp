package com.odbpp.model.stackup;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "StackupFile")
public class StackupFile {
    // Optional per spec, pg 54
    private EdaData EdaData;
    // Optional per spec, pg 54
    private SupplierData SupplierData;
}
