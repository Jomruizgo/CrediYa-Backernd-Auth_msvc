package com.crediya.servicePort;

import com.crediya.model.AuthenticationResponse;
import reactor.core.publisher.Mono;

public interface IAuthenticationService {
    Mono<AuthenticationResponse> authenticate(String email, String password);
    Mono<AuthenticationResponse> refreshToken(String refreshToken);
}