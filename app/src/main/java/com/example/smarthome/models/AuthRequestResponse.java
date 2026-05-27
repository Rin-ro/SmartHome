package com.example.smarthome.models;

public class SignUpRequest {
    public String email;
    public String password;
    public SignUpRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

public class SignInRequest {
    public String email;
    public String password;
    public SignInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

public class AuthResponse {
    public String access_token;
    public String token_type;
    public User user;
    public static class User {
        public String id;
        public String email;
    }
}