package com.projetoanderson.app.security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por gerar tokens JWT.
 */
@Service
public class JwtService {
	private final JwtEncoder encoder;

	public JwtService(JwtEncoder encoder) {
		this.encoder = encoder;
	}
	
	/**
	 * Generate a JWT token for the given authentication.
	 * 
	 * @param authentication the authentication object containing the user's authorities
	 * @return the generated JWT token as a String
	 */
	public String generateToken(Authentication authentication) {
		Instant now = Instant.now();
		long expiry = 3600L; // 1 hour in seconds
		
		String scopes = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(" "));
		
		var claims = JwtClaimsSet.builder()
				.issuer("spring-security-jwt")
				.issuedAt(now)
				.expiresAt(now.plusSeconds(expiry))
				.subject(authentication.getName())
				.claim("scope", scopes)
				.build();
		
		return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}
}
