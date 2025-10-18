package com.projetoanderson.app.model.entity;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcoes")
public class Funcao implements GrantedAuthority {
	private static final long serialVersionUID = -4693004502376613634L;
	
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_MOTORISTA = "ROLE_MOTORISTA";
	public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome", nullable = false, unique = true, length = 50)
	private String nome;

	@Override
	public String getAuthority() {
		return this.nome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
