package com.biz4solutions.models;

import java.io.Serializable;

public class EmsRequest implements Serializable {
    private String id;
    private boolean isUnconscious;
    private double latitude;
    private double longitude;
    private String priority;
    private String requestStatus;
    private String providerId;
    private String userId;
    private long requestTime;
    private User userDetails;
    private int amount;
    private String patientDisease;
    private String triageCallStatus;
    private String providerFeedback;
    private boolean isPatientFeedbackSubmitted;
    private String providerName;
    private String patientName;
    private String providerSpecialization;
    private String patientSymptoms;
    private String videoCallStatus;
    private String providerFeedbackReason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsUnconscious() {
        return isUnconscious;
    }

    public void setIsUnconscious(boolean unconscious) {
        isUnconscious = unconscious;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(User userDetails) {
        this.userDetails = userDetails;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPatientDisease() {
        return patientDisease;
    }

    public void setPatientDisease(String patientDisease) {
        this.patientDisease = patientDisease;
    }

    public String getTriageCallStatus() {
        return triageCallStatus;
    }

    public void setTriageCallStatus(String triageCallStatus) {
        this.triageCallStatus = triageCallStatus;
    }

    public String getProviderFeedback() {
        return providerFeedback;
    }

    public void setProviderFeedback(String providerFeedback) {
        this.providerFeedback = providerFeedback;
    }

    public boolean getIsPatientFeedbackSubmitted() {
        return isPatientFeedbackSubmitted;
    }

    public void setPatientFeedbackSubmitted(boolean patientFeedbackSubmitted) {
        isPatientFeedbackSubmitted = patientFeedbackSubmitted;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getProviderSpecialization() {
        return providerSpecialization;
    }

    public void setProviderSpecialization(String providerSpecialization) {
        this.providerSpecialization = providerSpecialization;
    }

    public String getPatientSymptoms() {
        return patientSymptoms;
    }

    public void setPatientSymptoms(String patientSymptoms) {
        this.patientSymptoms = patientSymptoms;
    }

    public String getVideoCallStatus() {
        return videoCallStatus;
    }

    public void setVideoCallStatus(String videoCallStatus) {
        this.videoCallStatus = videoCallStatus;
    }

    public String getProviderFeedbackReason() {
        return providerFeedbackReason;
    }

    public void setProviderFeedbackReason(String providerFeedbackReason) {
        this.providerFeedbackReason = providerFeedbackReason;
    }

    @Override
    public String toString() {
        return "EmsRequest{" +
                "id='" + id + '\'' +
                ", isUnconscious=" + isUnconscious +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", priority='" + priority + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                ", providerId='" + providerId + '\'' +
                ", userId='" + userId + '\'' +
                ", requestTime=" + requestTime +
                ", userDetails=" + userDetails +
                ", amount=" + amount +
                ", patientDisease='" + patientDisease + '\'' +
                ", triageCallStatus='" + triageCallStatus + '\'' +
                ", providerFeedback='" + providerFeedback + '\'' +
                ", isPatientFeedbackSubmitted=" + isPatientFeedbackSubmitted +
                ", providerName='" + providerName + '\'' +
                ", patientName='" + patientName + '\'' +
                ", providerSpecialization='" + providerSpecialization + '\'' +
                ", patientSymptoms='" + patientSymptoms + '\'' +
                ", videoCallStatus='" + videoCallStatus + '\'' +
                ", providerFeedbackReason='" + providerFeedbackReason + '\'' +
                '}';
    }
}
