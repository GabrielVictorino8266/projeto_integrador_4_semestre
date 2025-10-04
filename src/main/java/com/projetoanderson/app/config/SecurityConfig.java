package com.projetoanderson.app.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

/**
 * Configuração da segurança da aplicação
 * 
 * <p>Classe que define a configuração de segurança com Spring Security, usando o JWT baseado em chaves RSA. 
 * Aqui também desabilita CSRF para permitir o uso API REST.</p>
 * <p>As chaves são injetadas a partir dos arquivos .properties.</p>
 * 
 * @author Gabriel Victorino
 * @since 2025-04-10
 * */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${jwt.public.key}")
	private RSAPublicKey key;
	@Value("${jwt.private.key}")
	private RSAPrivateKey priv;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(
				auth -> auth.requestMatchers("/authenticate").permitAll()
					.anyRequest().authenticated())
		.httpBasic(Customizer.withDefaults())
		.oauth2ResourceServer(
		conf -> conf.jwt(Customizer.withDefaults()));
		
		return http.build();
	}
	
	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(key).build();
	}
	
	@Bean
	JwtEncoder jwtEncoder() {
		var jwk = new RSAKey.Builder(key).privateKey(priv).build();
		var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
 	
}
