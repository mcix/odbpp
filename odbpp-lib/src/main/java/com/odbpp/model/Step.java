package com.odbpp.model;

import lombok.Data;
import java.util.Map;
import com.odbpp.model.impedance.ImpedanceFile;
import java.util.List;

@Data
public class Step {
    private String name;
    private Map<String, Layer> layersByName;
    private EdaData edaData;
    private Map<String, Netlist> netlistsByName;
    private AttrList attrList;
    private Features profile;
    private StepHdr stepHdr;
    private Bom bom;
    private ImpedanceFile impedance;
    private List<Zone> zones;
    private int col;
    private int id;
}