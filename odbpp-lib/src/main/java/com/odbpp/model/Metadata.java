package com.odbpp.model;

import lombok.Data;
import java.util.Map;

/**
 * Represents the metadata.xml file. Optional.
 */
@Data
public class Metadata {
    private Map<String, Object> data;
}
