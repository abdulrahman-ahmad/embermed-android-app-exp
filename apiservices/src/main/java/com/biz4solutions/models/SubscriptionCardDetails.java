package com.biz4solutions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionCardDetails {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("subscriptionTitle")
    @Expose
    private String subscriptionTitle;
    @SerializedName("startTime")
    @Expose
    private long startTime;
    @SerializedName("endTime")
    @Expose
    private long endTime;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("subscriptionDuration")
    @Expose
    private String subscriptionDuration;
    @SerializedName("subscriptionStatus")
    @Expose
    private String subscriptionStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriptionTitle() {
        return subscriptionTitle;
    }

    public void setSubscriptionTitle(String subscriptionTitle) {
        this.subscriptionTitle = subscriptionTitle;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscriptionDuration() {
        return subscriptionDuration;
    }

    public void setSubscriptionDuration(String subscriptionDuration) {
        this.subscriptionDuration = subscriptionDuration;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    @Override
    public String toString() {
        return "SubscriptionCardDetails{" +
                "id='" + id + '\'' +
                ", subscriptionTitle='" + subscriptionTitle + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", subscriptionDuration='" + subscriptionDuration + '\'' +
                ", subscriptionStatus='" + subscriptionStatus + '\'' +
                '}';
    }
}
