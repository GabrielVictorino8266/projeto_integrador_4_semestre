package com.projetoanderson.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetoanderson.app.service.AuthenticationService;

/**
 * Controlador REST responsável pela autenticação de usuários.
 * 
 * <p>Este controlador expõe endpoints para autenticação e geração de tokens JWT.
 * A autenticação é feita através do Spring Security usando HTTP Basic Authentication,
 * e retorna um token JWT válido para acesso aos recursos protegidos.</p>
 * 
 * @author Gabriel Victorino
 * @since 2025-04-10
 */
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Endpoint para autenticação de usuários.
     * 
     * <p>Recebe as credenciais do usuário via HTTP Basic Authentication e retorna
     * um token JWT válido caso a autenticação seja bem-sucedida.</p>
     * 
     * @param authentication objeto de autenticação fornecido pelo Spring Security
     * @return token JWT como String
     * 
     */
    @PostMapping("/authenticate") // MELHORIA: Adicionar barra inicial
    public String authenticate(Authentication authentication){
        return authenticationService.authenticate(authentication);
    }
}
