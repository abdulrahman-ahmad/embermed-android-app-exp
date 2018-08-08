package com.biz4solutions.models.request;

/*
 * Created by ketan on 12/6/2017.
 */

public class LoginRequest {
    private String email;
    private String password;
    private String signupType;
    private String roleName;

    public LoginRequest(String signupType, String roleName) {
        this.signupType = signupType;
        this.roleName = roleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSignupType() {
        return signupType;
    }

    public void setSignupType(String signupType) {
        this.signupType = signupType;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", signupType='" + signupType + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
