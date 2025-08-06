package com.odbpp.model;

import lombok.Data;
import java.util.List;

/**
 * Represents a wheel (aperture table).
 */
@Data
public class Wheel {
    private String name;
    /** Optional */
    private AttrList attrList;
    private List<DCode> dcodes;
}
