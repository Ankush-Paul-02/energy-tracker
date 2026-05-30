package com.paul.apigateway.route;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class InsightServiceRoute {

    @Bean
    public RouterFunction<ServerResponse> insightRoute() {
        return route("insight-service")
                .route(RequestPredicates.path("api/v1/insights/**"), http())
                .before(uri("http://localhost:8086/"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "insight-service-circuitbreaker",
                        URI.create("forward:/fallbackRoute")
                ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> insightFallbackRoute() {
        return route("fallbackRoute")
                .route(
                        RequestPredicates.path("/fallbackRoute"),
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Service is down!!!")
                )
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> insightServiceApiDocs() {
        return GatewayRouterFunctions.route("insight-service-api-docs")
                .route(RequestPredicates.path("/docs/insight-service/v3/api-docs"),
                        http())
                .before(uri("http://localhost:8086"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }
}
