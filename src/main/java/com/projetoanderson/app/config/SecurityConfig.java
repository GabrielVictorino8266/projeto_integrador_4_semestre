package com.projetoanderson.app.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // <-- IMPORTADO
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // <-- ADICIONADO
public class SecurityConfig {

	@Value("${jwt.public.key}")
	private RSAPublicKey key;
	@Value("${jwt.private.key}")
	private RSAPrivateKey priv;
	
	@Autowired
    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
            // ----- HABILITA E CONFIGURA O CORS -----
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // ---------------------------------------
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF (comum para APIs stateless)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/authenticate").permitAll() // Permite acesso ao endpoint de autenticação
                .anyRequest().authenticated() // Exige autenticação para qualquer outra requisição
            )
            .httpBasic(Customizer.withDefaults()) // Permite autenticação Basic (usada para obter o token inicial)
            .oauth2ResourceServer(conf -> conf
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter) // Usa seu conversor customizado
                )
            );
		
		return http.build();
	}

    // ----- BEAN QUE DEFINE AS REGRAS DO CORS -----
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// Lista de origens permitidas (seu frontend)
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:8888")); 
		// Métodos HTTP permitidos
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); 
		// Cabeçalhos que o frontend pode enviar
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control", "X-Requested-With")); 
		// Permite que o navegador envie credenciais (como o token JWT no header Authorization)
		configuration.setAllowCredentials(true); 
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// Aplica essa configuração para todos os endpoints da sua API
		source.registerCorsConfiguration("/api/**", configuration); 
		return source;
	}
    // --------------------------------------------
	
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