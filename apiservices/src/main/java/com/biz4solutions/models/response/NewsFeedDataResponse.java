package com.biz4solutions.models.response;

import com.biz4solutions.models.Location;

import java.util.List;

public class NewsFeedDataResponse {
    private List<Location> providerLocationList;
    private long totalLifeSave;
    private long totalTriageCall;

    public List<Location> getProviderLocationList() {
        return providerLocationList;
    }

    public void setProviderLocationList(List<Location> providerLocationList) {
        this.providerLocationList = providerLocationList;
    }

    public long getTotalLifeSave() {
        return totalLifeSave;
    }

    public void setTotalLifeSave(long totalLifeSave) {
        this.totalLifeSave = totalLifeSave;
    }

    public long getTotalTriageCall() {
        return totalTriageCall;
    }

    public void setTotalTriageCall(long totalTriageCall) {
        this.totalTriageCall = totalTriageCall;
    }

    @Override
    public String toString() {
        return "NewsFeedDataResponse{" +
                "providerLocationList=" + providerLocationList +
                ", totalLifeSave=" + totalLifeSave +
                ", totalTriageCall=" + totalTriageCall +
                '}';
    }
}
