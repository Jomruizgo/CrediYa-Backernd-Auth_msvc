package com.crediya.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuthenticationToken {
    private final String value;
    private final LocalDateTime expiresAt;
    private final TokenType type;
    private final Long userId;

    public AuthenticationToken(String value, LocalDateTime expiresAt, TokenType type, Long userId) {
        this.value = value;
        this.expiresAt = expiresAt;
        this.type = type;
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public TokenType getType() {
        return type;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationToken that = (AuthenticationToken) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "AuthenticationToken{" +
                "value='[PROTECTED]'" +
                ", expiresAt=" + expiresAt +
                ", type=" + type +
                ", userId=" + userId +
                '}';
    }
}