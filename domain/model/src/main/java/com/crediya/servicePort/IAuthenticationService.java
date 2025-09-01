package com.crediya.servicePort;

import com.crediya.model.AuthenticationResponse;
import com.crediya.model.AuthenticationToken;
import reactor.core.publisher.Mono;

public interface IAuthenticationService {
    Mono<AuthenticationResponse> authenticate(String email, String password);
    Mono<AuthenticationResponse> refreshToken(String refreshToken);
    Mono<Void> logout(String refreshToken);
}