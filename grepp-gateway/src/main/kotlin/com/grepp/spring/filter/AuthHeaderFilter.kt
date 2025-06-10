package com.grepp.spring.filter

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.*


fun handlerFilterFnc() : HandlerFilterFunction<ServerResponse, ServerResponse>{
    return HandlerFilterFunction { request, next ->
        if(!request.headers().asHttpHeaders().containsKey("x-member-id")){
            val userId = request.attributes()["x-member-id"] ?: "ANONYMOUS"
            val role = request.attributes()["x-member-role"] ?: "ROLE_ANONYMOUS"
            val modified = ServerRequest.from(request)
                .header("x-member-id", userId.toString())
                .header("x-member-role", role.toString())
                .build()
            next.handle(modified)
        }else{
            next.handle(request)
        }
    }
}

@Configuration
class RouteConfiguration{
    val userService = route("user-service")
        .GET("/api/member/**", http())
        .POST("/api/member/**", http())
        .before(uri("http://localhost:8082"))
        .build()

    val mailService = route("mail-service")
        .GET("/mail/**", http())
        .POST("/mail/**", http())
        .before(uri("http://localhost:8083"))
        .build()

    @Bean
    fun headerExistsRoute(): RouterFunction<ServerResponse> {
        return userService
            .and(mailService)
            .filter(handlerFilterFnc())
    }
}
