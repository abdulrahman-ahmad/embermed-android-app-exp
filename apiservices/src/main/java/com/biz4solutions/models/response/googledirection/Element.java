package com.biz4solutions.models.response.googledirection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Element {

    @SerializedName("elements")
    @Expose
    private List<Leg> elements = new ArrayList<>();


    public List<Leg> getElements() {
        return elements;
    }

    public void setElements(List<Leg> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "Element{" +
                "elements=" + elements +
                '}';
    }
}
