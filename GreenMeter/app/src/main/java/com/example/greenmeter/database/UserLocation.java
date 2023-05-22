package com.example.greenmeter.database;

import java.util.Date;

public class UserLocation {
    private String idToken;         // Firebase Uid (고유 토큰정보)
    private String recode_time;       // 기록시간
    private Double Lat;             // 위도
    private Double Lng;             // 경도
    private String type_trans;      // 이동수단

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRecode_time() {
        return recode_time;
    }

    public void setRecode_time(String recode_time) {
        this.recode_time = recode_time;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }

    public String getType_trans() {
        return type_trans;
    }

    public void setType_trans(String type_trans) {
        this.type_trans = type_trans;
    }
}
