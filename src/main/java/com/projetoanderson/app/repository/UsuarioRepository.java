package com.projetoanderson.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.model.entity.Usuario;

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
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByEmail(String email);
	@Query("SELECT u FROM Usuario u JOIN FETCH u.empresa WHERE u.cpf = :cpf")
    Optional<Usuario> findByCpf(@Param("cpf") String cpf);
    
    boolean existsByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
    
 // Para o método buscarTodos filtrado
    List<Usuario> findAllByEmpresaId(Long empresaId);

    // Para o método buscarUsuarioNome filtrado
    List<Usuario> findByNomeContainingIgnoreCaseAndEmpresaId(String nome, Long empresaId);

    // Para as validações no método criar e atualizar
    boolean existsByCpfAndEmpresaId(String cpf, Long empresaId);
    boolean existsByEmailAndEmpresaId(String email, Long empresaId);
}
