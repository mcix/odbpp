package com.odbpp.model;

import lombok.Data;

/**
 * Represents the basic information about the product model from the misc/info file.
 * This file is mandatory.
 */
@Data
public class MiscInfo {
    private String productModelName;
    private int odbVersionMajor;
    private int odbVersionMinor;
    private String odbSource;
    private String creationDate;
    private String saveDate;
    private String saveApp;
    private String saveUser;
    private String units;
    private long maxUid;
}
