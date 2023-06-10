/**
 * 코드 작성자
 * 김동민 - 차종과 탄소배출량을 저장하는 클래스 생성
 * */

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