package com.grepp.spring.filter

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.*


fun handlerFilterFnc() : HandlerFilterFunction<ServerResponse, ServerResponse>{
    return HandlerFilterFunction { request, next ->
        val userId = request.attributes()["x-member-id"].toString() ?: "ANONYMOUS"
        val role = request.attributes()["x-member-role"].toString() ?: "ROLE_ANONYMOUS"

        val modified = ServerRequest.from(request)
            .header("x-member-id", userId)
            .header("x-member-role", role)
            .build()

        next.handle(modified)
    }
}

@Configuration
class RouteConfiguration{

    @Bean
    fun headerExistsRoute(): RouterFunction<ServerResponse> {
        return route("general-route")
            .GET("/**", http())
            .POST("/**", http())
            .filter(handlerFilterFnc() )
            .before(uri("http://localhost:8082"))
            .build()
    }
}