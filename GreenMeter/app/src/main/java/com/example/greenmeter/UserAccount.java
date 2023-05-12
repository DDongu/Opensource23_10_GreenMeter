package com.example.greenmeter;

/**
 * 사용자 계정 정보 모델 클래스
 */

public class UserAccount {
    private String idToken;         // Firebase Uid (고유 토큰정보)
    private String emailId;         // 이매일 아이디
    private String password;        // 비밀번호
    private String username;        // 유저이름
    private String ph;              // 전화번호
    private String nickname;        // 닉네임
    private Double carbonEm;        // 차량탄소배출량
    private Double total_distance;  // 누적이동거리
    private Double total_carbonEm;  // 누적탄소배출량
    private Double avg_carbonEm;    // 평균탄소배출량


    public UserAccount() { } // firebase에서는 무조건 빈 생성자가 있어야 됨

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Double getCarbonEm() {
        return carbonEm;
    }

    public void setCarbonEm(Double carbonEm) {
        this.carbonEm = carbonEm;
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
