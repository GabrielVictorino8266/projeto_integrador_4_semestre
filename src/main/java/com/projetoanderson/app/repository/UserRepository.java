package com.projetoanderson.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.model.entity.User;

/**
 * Repositório JPA para operações de persistência da entidade User.
 * 
 * <p>Esta interface estende JpaRepository e fornece métodos CRUD automáticos
 * além de queries personalizadas para busca de usuários.</p>
 * 
 * @author Gabriel Victorino
 * @since 2025-04-10
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
}
