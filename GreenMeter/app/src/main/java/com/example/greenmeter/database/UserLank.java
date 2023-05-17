package com.example.greenmeter.database;

/**
 * 사용자 랭크 모델 클래스
 */

public class UserLank {
    private String idToken;         // Firebase Uid (고유 토큰정보)
    private Long lank;              // 순위
    private String nickname;        // 사용자 닉네임
    private Double avg_carbonEm;    // 평균탄소배출량

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Long getLank() {
        return lank;
    }

    public void setLank(Long lank) {
        this.lank = lank;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Double getAvg_carbonEm() {
        return avg_carbonEm;
    }

    public void setAvg_carbonEm(Double avg_carbonEm) {
        this.avg_carbonEm = avg_carbonEm;
    }
}
