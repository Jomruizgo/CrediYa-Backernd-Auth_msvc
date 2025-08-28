package com.crediya.api.router;

import com.crediya.api.handler.UserHandler;
import com.crediya.util.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouterRest {
    
    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return route(POST(Constant.API_USER_PATH).and(accept(MediaType.APPLICATION_JSON)), userHandler::createUser)
                .andRoute(GET(Constant.API_USER_PATH + "/{id}"), userHandler::getUserById)
                .andRoute(GET(Constant.API_USER_PATH + "/search").and(queryParam("email", t -> true)), userHandler::getUserByEmail)
                .andRoute(PUT(Constant.API_USER_PATH + "/{id}").and(accept(MediaType.APPLICATION_JSON)), userHandler::updateUser)
                .andRoute(DELETE(Constant.API_USER_PATH + "/{id}"), userHandler::deleteUser)
                .andRoute(GET(Constant.API_USER_PATH), userHandler::getAllUsers);
    }
}
