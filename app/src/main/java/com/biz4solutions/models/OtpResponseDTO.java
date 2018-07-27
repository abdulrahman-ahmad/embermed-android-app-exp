package com.biz4solutions.models;

/*
 * Created by ketan on 12/6/2017.
 */

public class OtpResponseDTO extends EmptyResponse {
    private OtpResponseDataDTO data;

    public OtpResponseDataDTO getData() {
        return data;
    }

    public void setData(OtpResponseDataDTO data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OtpResponseDTO{" +
                "data=" + data +
                '}';
    }
}
