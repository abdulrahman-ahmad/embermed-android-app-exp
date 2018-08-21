package com.biz4solutions.models.response.googledirection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GoogleDistanceDurationResponse {
    @SerializedName("rows")
    @Expose
    private List<Element> rows = new ArrayList<>();

    /**
     * @return The rows
     */
    public List<Element> getRows() {
        return rows;
    }

    /**
     * @param rows The rows
     */
    public void setRows(List<Element> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "GoogleDistanceDurationResponse{" +
                "rows=" + rows +
                '}';
    }
}