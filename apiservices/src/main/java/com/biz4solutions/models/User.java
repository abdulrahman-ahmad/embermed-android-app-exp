package com.biz4solutions.models;

import java.io.Serializable;

/*
 * Created by ketan on 12/11/2017.
 */
public class User implements Serializable {

    private String userId;
    private String firstName = "";
    private String lastName = "";
    private String authToken = "";
    private String email = "";
    private String roleName = "";

    private String patientCurrentRequestId = "";
    private String providerCurrentRequestId = "";
    private String currentRequestPriority = "";

    private long dob;
    private String profileUrl;
    private String deviceId = "";
    private int age;
    private String gender;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPatientCurrentRequestId() {
        return patientCurrentRequestId;
    }

    public void setPatientCurrentRequestId(String patientCurrentRequestId) {
        this.patientCurrentRequestId = patientCurrentRequestId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProviderCurrentRequestId() {
        return providerCurrentRequestId;
    }

    public void setProviderCurrentRequestId(String providerCurrentRequestId) {
        this.providerCurrentRequestId = providerCurrentRequestId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrentRequestPriority() {
        return currentRequestPriority;
    }

    public void setCurrentRequestPriority(String currentRequestPriority) {
        this.currentRequestPriority = currentRequestPriority;
    }

    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", authToken='" + authToken + '\'' +
                ", email='" + email + '\'' +
                ", roleName='" + roleName + '\'' +
                ", patientCurrentRequestId='" + patientCurrentRequestId + '\'' +
                ", providerCurrentRequestId='" + providerCurrentRequestId + '\'' +
                ", currentRequestPriority='" + currentRequestPriority + '\'' +
                ", dob=" + dob +
                ", profileUrl='" + profileUrl + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }
}

