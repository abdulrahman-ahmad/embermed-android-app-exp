package com.biz4solutions.models.response;

public class UrgentCaresResponse extends EmptyResponse {

    private UrgentCaresDataResponse data;

    public UrgentCaresDataResponse getData() {
        return data;
    }

    public void setData(UrgentCaresDataResponse data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UrgentCaresResponse{" +
                "data=" + data +
                '}';
    }
}
