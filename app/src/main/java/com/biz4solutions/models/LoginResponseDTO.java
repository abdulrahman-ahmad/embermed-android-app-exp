package com.biz4solutions.models;

/*
 * Created by ketan on 12/11/2017.
 */
public class LoginResponseDTO extends EmptyResponse {
    private UserDTO data;

    public UserDTO getData() {
        return data;
    }

    public void setData(UserDTO data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "data=" + data +
                '}';
    }
}
