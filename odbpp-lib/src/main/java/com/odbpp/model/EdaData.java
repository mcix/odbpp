package com.odbpp.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Represents the EDA data from the eda/data file.
 * This file is optional.
 */
@Data
public class EdaData {
    private String units;
    private String source;
    private List<String> layerNames;
    private List<String> attributeNames;
    private List<String> attributeTextValues;
    private List<NetRecord> netRecords;
    private Map<String, NetRecord> netRecordsByName;
    private List<PackageRecord> packageRecords;
    private Map<String, PackageRecord> packageRecordsByName;

    @Data
    public static class NetRecord {
        private String name;
        private int index;
        private List<SubnetRecord> subnetRecords;
        private Map<String, String> attributes;
    }

    @Data
    public static class SubnetRecord {
        // Subnet properties
    }

    @Data
    public static class PackageRecord {
        private String name;
        private double pitch;
        private double xMin, yMin, xMax, yMax;
        private int index;
        private List<PinRecord> pinRecords;
        private Map<String, PinRecord> pinRecordsByName;
        private Map<String, String> attributes;
    }

    @Data
    public static class PinRecord {
        private String name;
        private String type;
        private double xCenter;
        private double yCenter;
        private String electricalType;
        private String mountType;
        private int id;
        private int index;
    }
}
