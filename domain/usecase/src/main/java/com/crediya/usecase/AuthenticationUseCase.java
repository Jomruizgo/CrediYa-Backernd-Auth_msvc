package com.crediya.usecase;

import com.crediya.exception.InvalidCredentialsException;
import com.crediya.exception.UserNotFoundException;
import com.crediya.gatewayPort.IPasswordEncoderPort;
import com.crediya.gatewayPort.ITokenProviderPort;
import com.crediya.model.AuthenticationResponse;
import com.crediya.model.AuthenticationToken;
import com.crediya.model.User;
import com.crediya.servicePort.IAuthenticationService;
import com.crediya.servicePort.IUserService;
import com.crediya.util.Constant;
import reactor.core.publisher.Mono;

public class AuthenticationUseCase implements IAuthenticationService {
    
    private final IUserService userService;
    private final IPasswordEncoderPort passwordEncoder;
    private final ITokenProviderPort tokenProvider;

    public AuthenticationUseCase(IUserService userService, 
                               IPasswordEncoderPort passwordEncoder,
                               ITokenProviderPort tokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<AuthenticationResponse> authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Mono.error(new InvalidCredentialsException(Constant.INVALID_CREDENTIALS));
        }
        
        return userService.findByEmail(email.trim().toLowerCase())
            .cast(User.class)
            .filter(User::canLogin)  // Check status ACTIVE + has credentials
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .switchIfEmpty(Mono.error(new InvalidCredentialsException(Constant.INVALID_CREDENTIALS)))
            .flatMap(this::generateTokensForUser);
    }

    @Override
    public Mono<AuthenticationResponse> refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return Mono.error(new InvalidCredentialsException(Constant.INVALID_REFRESH_TOKEN));
        }
        
        if (!tokenProvider.validateToken(refreshToken)) {
            return Mono.error(new InvalidCredentialsException(Constant.INVALID_REFRESH_TOKEN));
        }
        
        String username = tokenProvider.extractUsername(refreshToken);
        return userService.findByEmail(username)
            .cast(User.class)
            .flatMap(this::generateTokensForUser)
            .onErrorMap(UserNotFoundException.class, 
                ex -> new InvalidCredentialsException(Constant.INVALID_REFRESH_TOKEN));
    }

    @Override
    public Mono<Void> logout(String refreshToken) {
        // In this simple implementation, since JWT is stateless, 
        // logout is handled on client side by removing tokens
        // In implementation with blacklist or refresh tokens in DB, 
        // here they would be marked as revoked
        return Mono.empty();
    }

    private Mono<AuthenticationResponse> generateTokensForUser(User user) {
        AuthenticationToken accessToken = tokenProvider.generateAccessToken(user);
        AuthenticationToken refreshToken = tokenProvider.generateRefreshToken(user);
        
        return Mono.just(new AuthenticationResponse(accessToken, refreshToken, user));
    }
}