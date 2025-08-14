package com.bryan.platform.model.request;

import com.google.gson.annotations.SerializedName;

public class UserUpdateRequest {

    @SerializedName("username")
    private String username;   // 可空

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;      // 可空

    public UserUpdateRequest() {}

    public UserUpdateRequest(String username, String phone, String email) {
        this.username = username;
        this.phone = phone;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /* getter & setter 省略 */
}