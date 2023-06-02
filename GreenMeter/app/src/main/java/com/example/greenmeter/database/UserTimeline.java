package com.example.greenmeter.database;

import java.util.Date;

/**
 * 사용자 타임라인 모델 클래스
 */

public class UserTimeline {
    private String idToken;         // Firebase Uid (고유 토큰정보)

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
