package com.crediya.api.router;

import com.crediya.api.handler.UserHandler;
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
public class UserRouterRest {
    
    @Bean
    @RouterOperations({
        @RouterOperation(path = Constant.API_USER_PATH, method = RequestMethod.POST, beanClass = UserHandler.class, beanMethod = "createUser"),
        @RouterOperation(path = Constant.API_USER_PATH + "/{id}", method = RequestMethod.GET, beanClass = UserHandler.class, beanMethod = "getUserById"),
        @RouterOperation(path = Constant.API_USER_PATH + "/search", method = RequestMethod.GET, beanClass = UserHandler.class, beanMethod = "getUserByEmail"),
        @RouterOperation(path = Constant.API_USER_PATH + "/{id}", method = RequestMethod.PUT, beanClass = UserHandler.class, beanMethod = "updateUser"),
        @RouterOperation(path = Constant.API_USER_PATH + "/{id}", method = RequestMethod.DELETE, beanClass = UserHandler.class, beanMethod = "deleteUser"),
        @RouterOperation(path = Constant.API_USER_PATH, method = RequestMethod.GET, beanClass = UserHandler.class, beanMethod = "getAllUsers")
    })
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return route(POST(Constant.API_USER_PATH).and(accept(MediaType.APPLICATION_JSON)), userHandler::createUser)
                .andRoute(GET(Constant.API_USER_PATH + "/{id}"), userHandler::getUserById)
                .andRoute(GET(Constant.API_USER_PATH + "/search").and(queryParam("email", t -> true)), userHandler::getUserByEmail)
                .andRoute(PUT(Constant.API_USER_PATH + "/{id}").and(accept(MediaType.APPLICATION_JSON)), userHandler::updateUser)
                .andRoute(DELETE(Constant.API_USER_PATH + "/{id}"), userHandler::deleteUser)
                .andRoute(GET(Constant.API_USER_PATH), userHandler::getAllUsers);
    }
}
