package com.crediya.gatewayPort;

import com.crediya.model.AuthenticationToken;
import com.crediya.model.User;

public interface ITokenProviderPort {
    AuthenticationToken generateAccessToken(User user);
    AuthenticationToken generateRefreshToken(User user);
    boolean validateToken(String token);
    String extractUsername(String token);
    boolean isTokenExpired(String token);
}