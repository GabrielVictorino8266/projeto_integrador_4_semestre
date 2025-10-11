package com.projetoanderson.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.model.entity.PerfilMotorista;

@Repository
public interface PerfilMotoristaRepository extends JpaRepository<PerfilMotorista, Long> {
    Optional<PerfilMotorista> findByNumeroCnh(String numeroCnh);
    boolean existsByNumeroCnh(String numeroCnh);
}
