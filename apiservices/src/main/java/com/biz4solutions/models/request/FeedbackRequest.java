package com.biz4solutions.models.request;

import java.util.List;

/*
 * Created by Karan.
 */
public class FeedbackRequest {
    private String comment;
    private double rating;
    private String requestId;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "FeedbackRequest{" +
                "comment='" + comment + '\'' +
                ", rating=" + rating +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}

