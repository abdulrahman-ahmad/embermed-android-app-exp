package com.biz4solutions.models;

/*
 * Created by ketan on 12/11/2017.
 */
public class UserDTO {
    private String email;
    private String name;
    private boolean pushNotificationActive;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPushNotificationActive() {
        return pushNotificationActive;
    }

    public void setPushNotificationActive(boolean pushNotificationActive) {
        this.pushNotificationActive = pushNotificationActive;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", pushNotificationActive=" + pushNotificationActive +
                '}';
    }
}

