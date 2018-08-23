package com.biz4solutions.models.response;

import com.biz4solutions.models.EmsRequest;


/*
 * Created by ketan on 12/15/2017.
 */
public class CreateEmsResponse extends EmptyResponse {
    private EmsRequest data;

    public EmsRequest getData() {
        return data;
    }

    public void setData(EmsRequest data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CreateEmsResponse{" +
                "data=" + data +
                '}';
    }
}

