package com.biz4solutions.models;

/*
 * Created by ketan on 12/15/2017.
 */
public class SignUpRequest {
    private String email;
    private String name;
    private String password;
    private String signupType;

    public SignUpRequest(String signupType) {
        this.signupType = signupType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "SignUpRequest{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", signupType='" + signupType + '\'' +
                '}';
    }
}

