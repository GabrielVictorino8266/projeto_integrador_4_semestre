package com.projetoanderson.app.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "usuarios")
public class User {
    
    /**
     * Nome de usuário (chave primária).
     */
    @Id
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * Senha do usuário.
     */
    @Column(name = "password", nullable = false)
    private String password; // TODO: URGENTE - Implementar hash BCrypt

    /**
     * Obtém o nome de usuário.
     * 
     * @return nome de usuário
     */
    public String getUsername() {
        return username;
    }

    /**
     * Define o nome de usuário.
     * 
     * @param username nome de usuário
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtém a senha do usuário.
     * @return senha do usuário (hash BCrypt em produção)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define a senha do usuário.
     * @param password senha hasheada com BCrypt
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
