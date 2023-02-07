package ru.netology.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostLoginResponse {

    private String authToken;

    @JsonProperty("auth-token")
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
