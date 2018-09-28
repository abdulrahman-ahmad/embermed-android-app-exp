package com.biz4solutions.models.response;

public class ServerTimeDiffResponse extends EmptyResponse {
    private long data;

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ServerTimeDiffResponse{" +
                "data=" + data +
                '}';
    }
}
