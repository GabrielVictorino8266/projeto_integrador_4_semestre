package com.projetoanderson.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.projetoanderson.app.security.JwtService;

@Service
public class AuthenticationService {
    private final JwtService jwtService;
    
    
    public AuthenticationService(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	public String authenticate(Authentication authentication){
        return jwtService.generateToken(authentication);
    }
}
