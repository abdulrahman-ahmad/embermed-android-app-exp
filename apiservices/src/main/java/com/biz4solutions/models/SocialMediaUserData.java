package com.biz4solutions.models;

/**
 * Created by ketan on 12/20/2017.
 */

public class SocialMediaUserData {
    private String email;
    private String firstName;
    private String lastName;
    private String signupType;
    private String socialLoginId;
    private String socialLoginToken;
    private String roleName;
    private String invitationCode;

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSignupType() {
        return signupType;
    }

    public void setSignupType(String signupType) {
        this.signupType = signupType;
    }

    public String getSocialLoginId() {
        return socialLoginId;
    }

    public void setSocialLoginId(String socialLoginId) {
        this.socialLoginId = socialLoginId;
    }

    public String getSocialLoginToken() {
        return socialLoginToken;
    }

    public void setSocialLoginToken(String socialLoginToken) {
        this.socialLoginToken = socialLoginToken;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "SocialMediaUserData{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", signupType='" + signupType + '\'' +
                ", socialLoginId='" + socialLoginId + '\'' +
                ", socialLoginToken='" + socialLoginToken + '\'' +
                ", roleName='" + roleName + '\'' +
                ", invitationCode='" + invitationCode + '\'' +
                '}';
    }
}
