package com.biz4solutions.models.response;

import com.biz4solutions.models.UrgentCare;

import java.io.Serializable;
import java.util.ArrayList;

public class UrgentCaresDataResponse implements Serializable{

    private ArrayList<UrgentCare> list;

    public ArrayList<UrgentCare> getList() {
        return list;
    }

    public void setList(ArrayList<UrgentCare> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "UrgentCaresResponse{" +
                "list=" + list +
                '}';
    }
}
