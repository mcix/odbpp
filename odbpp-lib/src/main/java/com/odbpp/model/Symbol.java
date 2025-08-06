package com.odbpp.model;

import lombok.Data;

/**
 * Represents a user-defined symbol.
 */
@Data
public class Symbol {
    private String name;
    /** Optional */
    private AttrList attrList;
    private Features features;
}
