package com.biz4solutions.models.response;

import com.biz4solutions.models.Occupation;

import java.util.ArrayList;

public class OccupationResponse extends EmptyResponse{

    private ArrayList<Occupation> data;

    public ArrayList<Occupation> getData() {
        return data;
    }

    public void setData(ArrayList<Occupation> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OccupationResponse{" +
                "data=" + data +
                '}';
    }
}
