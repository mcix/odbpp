package com.odbpp.model;

import lombok.Data;
import java.util.List;

/**
 * Represents a netlist file (e.g., cadnet/netlist).
 * This file is optional.
 */
@Data
public class Netlist {
    private boolean optimized;
    private boolean staggered;
    private List<Net> nets;
    private List<NetPoint> netPoints;

    @Data
    public static class Net {
        private int serialNum;
        private String name;
    }

    @Data
    public static class NetPoint {
        private int netNum;
        private double radius;
        private double x;
        private double y;
        private String side;
        // ... other properties
    }
}
