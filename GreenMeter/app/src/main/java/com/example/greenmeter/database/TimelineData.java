/**
 * 코드 작성자
 * 김동규 - 사용자 타임라인 실시간 위치 데어터 클래스 생성
 * */

package com.example.greenmeter.database;

public class TimelineData {
    private String recode_date;       // 기록날짜
    private String mv_trans;       // 이동수단
    private Double mv_distance;     // 이동거리
    private Double mv_time;         // 이동시간
    private Double carbonEm;        // 탄소배출량

    public String getRecode_date() {
        return recode_date;
    }

    public void setRecode_date(String recode_date) {
        this.recode_date = recode_date;
    }

    public String getMv_trans() {
        return mv_trans;
    }

    public void setMv_trans(String mv_trans) {
        this.mv_trans = mv_trans;
    }

    public Double getMv_distance() {
        return mv_distance;
    }

    public void setMv_distance(Double mv_distance) {
        this.mv_distance = mv_distance;
    }

    public Double getMv_time() {
        return mv_time;
    }

    public void setMv_time(Double mv_time) {
        this.mv_time = mv_time;
    }

    public Double getCarbonEm() {
        return carbonEm;
    }

    public void setCarbonEm(Double carbonEm) {
        this.carbonEm = carbonEm;
    }
}
