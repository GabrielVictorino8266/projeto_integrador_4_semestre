package com.projetoanderson.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.model.entity.Funcao;

@Repository
public interface FuncaoRepository extends JpaRepository<Funcao, Long> {
    Optional<Funcao> findByNome(String nome);
}
