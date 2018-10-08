package com.biz4solutions.models.request;

import com.biz4solutions.models.MedicalDisease;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserDiseaseListRequest {

    @SerializedName("userDiseaseDTOs")
    ArrayList<MedicalDisease> data;

    public ArrayList<MedicalDisease> getData() {
        return data;
    }

    public void setData(ArrayList<MedicalDisease> data) {
        this.data = data;
    }
}
