package com.projetoanderson.app.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioRequestDTO {

	@NotBlank(message = "O Nome é Obrigatório.")
	@Size(min = 3, max = 100)
	private String nome;

	@NotBlank(message = "A Senha é Obrigatória.")
	private String senha;

	@NotBlank(message = "CPF é obrigatório.")
	@Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos.")
	private String cpf;

	@Email(message = "Formato de e-mail inválido.")
	@NotBlank(message = "O Email é obrigatório")
	private String email;

	@NotNull(message = "A data de nascimento é obrigatória.")
	@Past(message = "A data de nascimento deve ser no passado.")
	private LocalDate dataNascimento;

	private String telefone;
	
	@NotNull(message="O id da empresa precisa ser válido.")
	private Long empresa;

	
	public String getNome() {
		return nome;
	}

	public String getSenha() {
		return senha;
	}

	public String getCpf() {
		return cpf;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public String getTelefone() {
		return telefone;
	}
	
	public String getEmail()
	{
		return email;
	}

	public Long getEmpresa() {
		return empresa;
	}
}
