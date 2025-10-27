package com.projetoanderson.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.projetoanderson.app.dto.UsuarioResponseDTO;
import com.projetoanderson.app.security.JwtService;

@Service
public class AuthenticationService {
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    
    
    public AuthenticationService(JwtService jwtService, UsuarioService usuarioService) {
		this.jwtService = jwtService;
        this.usuarioService = usuarioService;
	}

	public String authenticate(Authentication authentication){
        return jwtService.generateToken(authentication);
    }

    public UsuarioResponseDTO getCurrentUser() {
        return usuarioService.getUsuarioLogado();
    }
}