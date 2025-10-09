package com.projetoanderson.app.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, 
                          Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply to all controllers except those returning ResponseEntity
        return !ResponseEntity.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, 
                                MethodParameter returnType,
                                MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, 
                                ServerHttpResponse response) {
        
        // Create standardized response wrapper
        Map<String, Object> wrappedResponse = new HashMap<>();
        wrappedResponse.put("success", true);
        wrappedResponse.put("timestamp", LocalDateTime.now());
        wrappedResponse.put("data", body);
        wrappedResponse.put("message", "Request processed successfully");
        
        return wrappedResponse;
    }
}