package com.projetoanderson.app.model.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entidade JPA que representa um usuário do sistema.
 * 
 * <p>Esta classe mapeia a tabela "usuarios" no banco de dados e contém
 * as informações básicas de autenticação do usuário.</p>
 * 
 * @author Gabriel Victorino
 * @since 2025-04-10
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"empresa_id", "email"}),
		@UniqueConstraint(columnNames = {"empresa_id", "cpf"})
})
public class Usuario extends Auditoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "O Nome é Obrigatório.")
    @Size(min = 3, max = 100)
    @Column(name = "nome", nullable = false, unique = false, length = 100)
    private String nome;
    
    @Column(name = "senha", nullable = false)
    private String senha;
    
    @NotBlank(message = "CPF é obrigatório.")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos.")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;
    
    @Email(message = "Formato de e-mail invádlio.")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser no passado.")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
    
    @Column(name = "telefone", length = 20)
    private String telefone;
    
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
    	    name = "usuarios_funcoes",
    	    joinColumns = @JoinColumn(name = "usuario_id"),
    	    inverseJoinColumns = @JoinColumn(name = "funcao_id")
    	)
    private Set<Funcao> funcoes;
    
    @OneToOne
    @JoinColumn(name="id")
    private PerfilMotorista perfilMotorista;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="empresa_id", nullable=false)
    private Empresa empresa;

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

	public Set<Funcao> getFuncoes() {
		return funcoes;
	}

	public void setFuncoes(Set<Funcao> funcoes) {
		this.funcoes = funcoes;
	}

	public PerfilMotorista getPerfilMotorista() {
		return perfilMotorista;
	}

	public void setPerfilMotorista(PerfilMotorista perfilMotorista) {
		this.perfilMotorista = perfilMotorista;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

}
