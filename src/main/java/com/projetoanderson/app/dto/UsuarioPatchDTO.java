package com.projetoanderson.app.dto; // Verifique e ajuste seu pacote

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class UsuarioPatchDTO {

    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres, se informado.")
    private String nome;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres, se informada.")
    private String senha;

    @Email(message = "Formato de e-mail inválido, se informado.")
    private String email;

    @Past(message = "A data de nascimento deve ser no passado, se informada.")
    private LocalDate dataNascimento;

    @Size(max = 20, message = "Telefone não pode exceder 20 caracteres, se informado.")
    private String telefone;

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
}