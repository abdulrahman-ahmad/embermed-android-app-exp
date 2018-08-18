package com.biz4solutions.models.response;

import com.biz4solutions.models.EmsRequest;

import java.util.List;

public class EmsRequestResponse extends EmptyResponse {
    private List<EmsRequest> data;
    private int page;

    public List<EmsRequest> getData() {
        return data;
    }

    public void setData(List<EmsRequest> data) {
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "EmsRequestResponse{" +
                "data=" + data +
                "page=" + page +
                '}';
    }
}
