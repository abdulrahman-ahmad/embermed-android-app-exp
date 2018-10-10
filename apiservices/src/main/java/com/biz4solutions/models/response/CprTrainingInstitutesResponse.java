package com.biz4solutions.models.response;

import com.biz4solutions.models.CprInstitute;
import com.biz4solutions.models.Occupation;

import java.util.ArrayList;

public class CprTrainingInstitutesResponse extends EmptyResponse{

    private ArrayList<CprInstitute> data;

    public ArrayList<CprInstitute> getData() {
        return data;
    }

    public void setData(ArrayList<CprInstitute> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OccupationResponse{" +
                "data=" + data +
                '}';
    }
}
