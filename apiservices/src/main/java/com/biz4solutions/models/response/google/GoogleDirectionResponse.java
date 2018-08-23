package com.biz4solutions.models.response.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GoogleDirectionResponse {
    @SerializedName("routes")
    @Expose
    private List<Route> routes = new ArrayList<>();

    /**
     * @return The routes
     */
    public List<Route> getRoutes() {
        return routes;
    }

    /**
     * @param routes The routes
     */
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}