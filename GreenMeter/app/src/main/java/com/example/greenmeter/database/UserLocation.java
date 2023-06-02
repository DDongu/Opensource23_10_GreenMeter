package com.example.greenmeter.database;

import java.util.Date;

public class UserLocation {
    private String idToken;         // Firebase Uid (고유 토큰정보)

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
