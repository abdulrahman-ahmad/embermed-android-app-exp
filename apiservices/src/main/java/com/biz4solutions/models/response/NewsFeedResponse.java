package com.biz4solutions.models.response;

public class NewsFeedResponse extends EmptyResponse {
    private NewsFeedDataResponse data;

    public NewsFeedDataResponse getData() {
        return data;
    }

    public void setData(NewsFeedDataResponse data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NewsFeedResponse{" +
                "data=" + data +
                '}';
    }
}
