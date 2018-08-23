package com.biz4solutions.models.response;

import com.biz4solutions.models.EmsRequest;

import java.util.List;

public class EmsRequestDetailsResponse extends EmptyResponse {
    private EmsRequest data;

    public EmsRequest getData() {
        return data;
    }

    public void setData(EmsRequest data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EmsRequestDetailsResponse{" +
                "data=" + data +
                '}';
    }
}
