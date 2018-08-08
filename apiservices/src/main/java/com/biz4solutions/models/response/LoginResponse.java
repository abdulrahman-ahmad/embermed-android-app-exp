package com.biz4solutions.models.response;

import com.biz4solutions.models.User;

/*
 * Created by ketan on 12/11/2017.
 */
public class LoginResponse extends EmptyResponse {
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "data=" + data +
                '}';
    }
}
