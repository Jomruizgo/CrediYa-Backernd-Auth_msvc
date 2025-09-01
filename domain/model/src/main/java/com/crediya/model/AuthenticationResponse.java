package com.crediya.model;

public class AuthenticationResponse {
    private final AuthenticationToken accessToken;
    private final AuthenticationToken refreshToken;
    private final User user;

    public AuthenticationResponse(AuthenticationToken accessToken, AuthenticationToken refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public AuthenticationToken getAccessToken() {
        return accessToken;
    }

    public AuthenticationToken getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "accessToken=" + accessToken +
                ", refreshToken=" + refreshToken +
                ", user=" + user +
                '}';
    }
}