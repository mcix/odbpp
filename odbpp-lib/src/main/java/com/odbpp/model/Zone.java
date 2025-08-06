package com.odbpp.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class Zone {
    private String name;
    private int uid;
    private List<Integer> layerUIDs;
    private Surface surface;
    private Map<String, String> properties;
}
