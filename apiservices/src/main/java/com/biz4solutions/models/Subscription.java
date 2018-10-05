package com.biz4solutions.models;

public class Subscription {

    private boolean isApproved;
    private boolean isProviderSubscribed;

    public boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean getIsProviderSubscribed() {
        return isProviderSubscribed;
    }

    public void setIsProviderSubscribed(boolean providerSubscribed) {
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
