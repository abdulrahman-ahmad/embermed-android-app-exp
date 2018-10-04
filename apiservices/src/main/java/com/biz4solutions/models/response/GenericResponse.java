package com.biz4solutions.models.response;

/*
 * Created by saurabh on 10/4/2018.
 */

public class GenericResponse<T> extends EmptyResponse {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
