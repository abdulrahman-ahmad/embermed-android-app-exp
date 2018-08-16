package com.biz4solutions.models.response;

import com.biz4solutions.models.EmsRequest;

import java.util.List;

public class EmsRequestResponse extends EmptyResponse {
    private List<EmsRequest> data;

    public List<EmsRequest> getData() {
        return data;
    }

    public void setData(List<EmsRequest> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EmsRequestResponse{" +
                "data=" + data +
                '}';
    }
}
