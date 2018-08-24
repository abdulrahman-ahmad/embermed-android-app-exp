package com.biz4solutions.models.response;

import com.biz4solutions.models.Symptom;

import java.util.List;

public class SymptomDataResponse {

    private List<Symptom> list;
    private int totalCount;
    private int totalPages;

    public List<Symptom> getList() {
        return list;
    }

    public void setList(List<Symptom> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return "SymptomDataResponse{" +
                "list=" + list +
                ", totalCount=" + totalCount +
                ", totalPages=" + totalPages +
                '}';
    }
}
