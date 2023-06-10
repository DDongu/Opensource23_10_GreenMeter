package com.example.greenmeter;

public class Car {
    private String name;
    private int carbonValue;

    public Car(String name, int carbonValue) {
        this.name = name;
        this.carbonValue = carbonValue;
    }

    public String getName() {
        return name;
    }

    public int getCarbonValue() {
        return carbonValue;
    }
}