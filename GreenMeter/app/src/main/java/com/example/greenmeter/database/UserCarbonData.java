package com.example.greenmeter.database;

/**
 * 사용자 랭크 모델 클래스
 */

public class UserCarbonData {
    private String car_model;         // 차종
    private Double total_distance;  // 누적이동거리
    private Double total_carbonEm;  // 누적탄소배출량
    private Double avg_carbonEm;    // 평균탄소배출량

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public Double getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(Double total_distance) {
        this.total_distance = total_distance;
    }

    public Double getTotal_carbonEm() {
        return total_carbonEm;
    }

    public void setTotal_carbonEm(Double total_carbonEm) {
        this.total_carbonEm = total_carbonEm;
    }

    public Double getAvg_carbonEm() {
        return avg_carbonEm;
    }

    public void setAvg_carbonEm(Double avg_carbonEm) {
        this.avg_carbonEm = avg_carbonEm;
    }
}
