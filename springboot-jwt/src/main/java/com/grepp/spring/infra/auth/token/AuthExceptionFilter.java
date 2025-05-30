package com.grepp.spring.infra.auth.token;

import com.grepp.spring.infra.error.exceptions.AuthApiException;
import com.grepp.spring.infra.error.exceptions.AuthWebException;
import com.grepp.spring.infra.error.exceptions.CommonException;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class AuthExceptionFilter extends OncePerRequestFilter {
    
    private final HandlerExceptionResolver handlerExceptionResolver;
    
    public AuthExceptionFilter(
        @Qualifier("handlerExceptionResolver")
        HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        
        try{
            filterChain.doFilter(request,response);
        }catch (CommonException ex){
            if(request.getRequestURI().startsWith("/api")){
                handlerExceptionResolver.resolveException(request, response, null, new AuthApiException(ex.code()));
                return;
            }
            handlerExceptionResolver.resolveException(request, response, null, new AuthWebException(ex.code()));
        }catch (JwtException ex){
            if(request.getRequestURI().startsWith("/api")){
                handlerExceptionResolver
                    .resolveException(request, response, null, new AuthApiException(ResponseCode.UNAUTHORIZED));
                return;
            }
            handlerExceptionResolver
                .resolveException(request, response, null, new AuthWebException(ResponseCode.UNAUTHORIZED));
        }
    }
}