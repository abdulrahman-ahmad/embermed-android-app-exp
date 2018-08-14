package com.biz4solutions.models;

public class EmsRequest {
    private String id;
    private boolean isUnconscious;
    private double latitude;
    private double longitude;
    private String priority;
    private String requestStatus;
    private String userId;

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

    @Override
    public String toString() {
        return "EmsRequest{" +
                "id='" + id + '\'' +
                ", isUnconscious=" + isUnconscious +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", priority='" + priority + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
