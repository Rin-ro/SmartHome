package com.example.smarthome.models;

public class SignUpRequest {
    public String email;
    public String password;
    public SignUpRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}