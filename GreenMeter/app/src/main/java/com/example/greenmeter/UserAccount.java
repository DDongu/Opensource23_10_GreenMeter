package com.example.greenmeter;

/**
 * 사용자 계정 정보 모델 클래스
 */

public class UserAccount {
    private String idToken; // Firebase Uid (고유 토큰정보)
    private String emailId; // 이매일 아이디
    private String password; // 비밀번호

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
}
