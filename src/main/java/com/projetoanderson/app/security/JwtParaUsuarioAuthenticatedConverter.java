// Crie este novo arquivo:
// src/main/java/com/projetoanderson/app/security/JwtParaUsuarioAuthenticatedConverter.java

package com.projetoanderson.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Converte um Jwt em um AbstractAuthenticationToken, usando o UserDetailsService
 * para carregar o objeto UsuarioAuthenticated completo.
 * * Isso garante que o SecurityContext.getPrincipal() retorne o seu
 * UsuarioAuthenticated, e não o Jwt padrão.
 */
@Component
public class JwtParaUsuarioAuthenticatedConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserDetailsService userDetailsService;

    // Injete o seu UserDetailsServiceImpl existente
    @Autowired
    public JwtParaUsuarioAuthenticatedConverter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // 1. Pega o 'subject' do token (que no seu caso é o CPF, 
        //    baseado no seu JwtService e UsuarioDetailsServiceImpl)
        String cpf = jwt.getSubject();
        
        // 2. Carrega o seu UsuarioAuthenticated usando o serviço que você já criou
        UserDetails userDetails = userDetailsService.loadUserByUsername(cpf);
        
        // 3. Retorna um token de autenticação padrão do Spring
        //    colocando o SEU userDetails (UsuarioAuthenticated) como o 'principal'
        return new UsernamePasswordAuthenticationToken(
            userDetails, // <-- Este é o 'principal'
            null,
            userDetails.getAuthorities()
        );
    }
}