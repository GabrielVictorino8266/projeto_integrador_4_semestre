package com.projetoanderson.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.model.entity.Manutencao;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {
}
