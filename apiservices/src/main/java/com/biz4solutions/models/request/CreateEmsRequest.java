package com.biz4solutions.models.request;

import java.util.List;

/*
 * Created by ketan on 12/15/2017.
 */
public class CreateEmsRequest {
    private boolean isUnconscious;
    private double latitude;
    private double longitude;
    private List<String> symptomId;

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

    public List<String> getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(List<String> symptomId) {
        this.symptomId = symptomId;
    }

    @Override
    public String toString() {
        return "CreateEmsRequest{" +
                "isUnconscious=" + isUnconscious +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", symptomId=" + symptomId +
                '}';
    }
}

