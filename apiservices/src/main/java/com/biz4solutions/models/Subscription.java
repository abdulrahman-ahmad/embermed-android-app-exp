package com.biz4solutions.models;

public class Subscription {

    private Boolean isApproved;
    private Boolean isProviderSubscribed;

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean approved) {
        isApproved = approved;
    }

    public Boolean getIsProviderSubscribed() {
        return isProviderSubscribed;
    }

    public void setIsProviderSubscribed(Boolean providerSubscribed) {
        isProviderSubscribed = providerSubscribed;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "isApproved=" + isApproved +
                ", isProviderSubscribed=" + isProviderSubscribed +
                '}';
    }
}
