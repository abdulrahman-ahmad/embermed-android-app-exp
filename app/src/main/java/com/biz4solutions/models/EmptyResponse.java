package com.biz4solutions.models;

import java.io.Serializable;

/*
 * Created by Ketan on 4/18/2016.
 */
public class EmptyResponse implements Serializable {
    private String status;
    private int code;
    private String error_type;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError_type() {
        return error_type;
    }

    public void setError_type(String error_type) {
        this.error_type = error_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "EmptyResponse{" +
                "status='" + status + '\'' +
                ", code=" + code +
                ", error_type='" + error_type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
