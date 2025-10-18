// CRIE ESTE NOVO ARQUIVO:
// package com.projetoanderson.app.dto;

package com.projetoanderson.app.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AdminCriaUsuarioRequestDTO {

    @NotBlank(message = "O Nome é Obrigatório.")
    @Size(min = 3, max = 100)
    private String nome;

    @NotBlank(message = "A Senha é Obrigatória.")
    private String senha;

    @NotBlank(message = "CPF é obrigatório.")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos.")
    private String cpf;

    @Email(message = "Formato de e-mail invádlio.")
    private String email;

    @NotNull(message = "A data de nascimento é obrigatória.")
    private LocalDate dataNascimento;

    private String telefone;

    @NotNull(message = "O ID da empresa é obrigatório.")
    private Long empresaId; // <-- A CHAVE DO FLUXO

    // Getters e Setters
    
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public LocalDate getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public Long getEmpresaId() {
		return empresaId;
	}
	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}
}