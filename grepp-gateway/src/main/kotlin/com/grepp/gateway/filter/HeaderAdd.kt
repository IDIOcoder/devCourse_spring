package com.grepp.gateway.filter

import com.grepp.gateway.filter.SampleHandlerFilterFunctions.instrument
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.*


internal object SampleHandlerFilterFunctions {
    fun instrument(): HandlerFilterFunction<ServerResponse?, ServerResponse> {
        return HandlerFilterFunction { request: ServerRequest?, next: HandlerFunction<ServerResponse?> ->
            val modified = ServerRequest.from(
                request!!
            )
                .header("X-Member-Id", "test")
                .header("X-Member-Role", "ROLE_USER")
                .build()
            next.handle(modified)
        }
    }
}

@Configuration
internal class RouteConfiguration {
    @Bean
    fun instrumentRoute(): RouterFunction<ServerResponse> {
        return route("instrument_route")
            .GET("/**", http())
            .filter(instrument())
            .before(uri("http://localhost:8082"))
            .build()
    }
}