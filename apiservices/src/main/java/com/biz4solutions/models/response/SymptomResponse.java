package com.biz4solutions.models.response;

public class SymptomResponse extends EmptyResponse {
    private SymptomDataResponse data;
    private int page;

    public SymptomDataResponse getData() {
        return data;
    }

    public void setData(SymptomDataResponse data) {
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
        return "SymptomResponse{" +
                "data=" + data +
                ", page=" + page +
                '}';
    }
}
