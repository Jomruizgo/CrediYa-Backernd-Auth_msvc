package com.crediya.api.router;

import com.crediya.api.handler.AuthHandler;
import com.crediya.util.Constant;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AuthRouterRest {
    
    @Bean
    @RouterOperations({
        @RouterOperation(path = Constant.API_AUTH_LOGIN_PATH, method = RequestMethod.POST, beanClass = AuthHandler.class, beanMethod = "login"),
        @RouterOperation(path = Constant.API_AUTH_REFRESH_PATH, method = RequestMethod.POST, beanClass = AuthHandler.class, beanMethod = "refreshToken"),
        @RouterOperation(path = Constant.API_AUTH_LOGOUT_PATH, method = RequestMethod.POST, beanClass = AuthHandler.class, beanMethod = "logout")
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
        return route(POST(Constant.API_AUTH_LOGIN_PATH).and(accept(MediaType.APPLICATION_JSON)), authHandler::login)
                .andRoute(POST(Constant.API_AUTH_REFRESH_PATH).and(accept(MediaType.APPLICATION_JSON)), authHandler::refreshToken)
                .andRoute(POST(Constant.API_AUTH_LOGOUT_PATH).and(accept(MediaType.APPLICATION_JSON)), authHandler::logout);
    }
}