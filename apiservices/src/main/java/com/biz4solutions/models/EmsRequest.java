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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isUnconscious() {
        return isUnconscious;
    }

    public void setUnconscious(boolean unconscious) {
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
                '}';
    }
}
