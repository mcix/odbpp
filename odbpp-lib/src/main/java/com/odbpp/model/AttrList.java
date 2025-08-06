package com.odbpp.model;

import lombok.Data;
import java.util.Map;

/**
 * Represents an attribute list from an attrlist file.
 * This file is optional.
 */
@Data
public class AttrList {
    private String units;
    private Map<String, String> attributes;
}
