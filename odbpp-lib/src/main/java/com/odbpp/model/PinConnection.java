package com.odbpp.model;

import lombok.Data;

@Data
public class PinConnection {
    private String name;
    private Component component;
    private Pin pin;
}
