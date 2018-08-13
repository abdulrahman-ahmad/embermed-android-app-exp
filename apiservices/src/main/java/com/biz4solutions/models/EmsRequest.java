package com.biz4solutions.models;

public class EmsRequest {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "EmsRequest{" +
                "id='" + id + '\'' +
                '}';
    }
}
