package com.bryan.platform.model.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String phone, String email) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}