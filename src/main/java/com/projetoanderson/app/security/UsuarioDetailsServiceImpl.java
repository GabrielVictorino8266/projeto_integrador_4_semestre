package com.projetoanderson.app.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projetoanderson.app.repository.UsuarioRepository;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService{
	private final UsuarioRepository usuarioRepository;
	
	
	public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
		return usuarioRepository.findByCpf(cpf)
				.map(UsuarioAuthenticated::new)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
	}

}
