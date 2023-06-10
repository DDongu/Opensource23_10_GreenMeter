/**
 * 코드 작성자
 * 김동규 - 사용자 위치 정보를 저장하기 위한 클래스 생성
 * */

package com.example.greenmeter.database;

public class UserLocation {
    private String idToken;         // Firebase Uid (고유 토큰정보)

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
