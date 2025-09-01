package com.crediya.jwtsecurity;

import com.crediya.gatewayPort.ITokenProviderPort;
import com.crediya.jwtsecurity.util.JwtMessages;
import com.crediya.model.AuthenticationToken;
import com.crediya.model.TokenType;
import com.crediya.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProviderAdapter implements ITokenProviderPort {

    private final SecretKey secretKey;
    private final int accessTokenValidityInMinutes;
    private final int refreshTokenValidityInDays;

    public JwtTokenProviderAdapter(
            @Value("${security.jwt.secret:mySecretKey123456789012345678901234567890}") String secret,
            @Value("${security.jwt.access-token-validity-minutes:15}") int accessTokenValidityInMinutes,
            @Value("${security.jwt.refresh-token-validity-days:7}") int refreshTokenValidityInDays) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidityInMinutes = accessTokenValidityInMinutes;
        this.refreshTokenValidityInDays = refreshTokenValidityInDays;
    }

    @Override
    public AuthenticationToken generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());
        
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(accessTokenValidityInMinutes);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        return new AuthenticationToken(token, expirationTime, TokenType.ACCESS, user.getId());
    }

    @Override
    public AuthenticationToken generateRefreshToken(User user) {
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(refreshTokenValidityInDays);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
        
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        return new AuthenticationToken(token, expirationTime, TokenType.REFRESH, user.getId());
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.debug(JwtMessages.TOKEN_VALIDATION_FAILED, e);
            return false;
        }
    }

    @Override
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            log.debug(JwtMessages.FAILED_TO_EXTRACT_USERNAME, e);
            return null;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.debug(JwtMessages.FAILED_TO_CHECK_TOKEN_EXPIRATION, e);
            return true;
        }
    }
}