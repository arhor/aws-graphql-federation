package com.github.arhor.aws.graphql.federation.comments.api.routes;

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Configuration(proxyBeanMethods = false)
public class ConfigureMainRouter {

    @Bean
    public RouterFunction<ServerResponse> mainRouter() {
        return RouterFunctions.route()
            .GET("favicon.ico", req -> ServerResponse.status(NO_CONTENT).build())
            .onError(EntityNotFoundException.class, (err, req) -> ServerResponse.status(NOT_FOUND).build())
            .onError(EntityDuplicateException.class, (err, req) -> ServerResponse.status(CONFLICT).build())
            .onError(DgsBadRequestException.class, (err, req) -> ServerResponse.status(BAD_REQUEST).build())
            .build();
    }
}
