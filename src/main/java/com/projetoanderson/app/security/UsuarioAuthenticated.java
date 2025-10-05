package com.projetoanderson.app.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.projetoanderson.app.model.entity.Usuario;


public class UsuarioAuthenticated implements UserDetails{

	private static final long serialVersionUID = 3233335824584300651L;
	
	private final Usuario user;
	

	public UsuarioAuthenticated(Usuario user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.user.getFuncoes();
	}

	@Override
	public String getPassword() {
		return user.getSenha();
	}

	@Override
	public String getUsername() {
		return user.getCpf();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override		
	public boolean isEnabled() {
		return this.user.isAtivo();
	}

}
