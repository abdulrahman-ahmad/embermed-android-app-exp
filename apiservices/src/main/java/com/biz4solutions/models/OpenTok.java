package com.biz4solutions.models;

public class OpenTok {
    private String patientToken;
    private String providerToken;
    private String sessionId;
    private String videoCallStatus;
    private long startTime;

    public String getPatientToken() {
        return patientToken;
    }

    public void setPatientToken(String patientToken) {
        this.patientToken = patientToken;
    }

    public String getProviderToken() {
        return providerToken;
    }

    public void setProviderToken(String providerToken) {
        this.providerToken = providerToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getVideoCallStatus() {
        return videoCallStatus;
    }

    public void setVideoCallStatus(String videoCallStatus) {
        this.videoCallStatus = videoCallStatus;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "OpenTok{" +
                "patientToken='" + patientToken + '\'' +
                ", providerToken='" + providerToken + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", videoCallStatus='" + videoCallStatus + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
