package com.projetoanderson.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.model.entity.PerfilMotorista;

@Repository
public interface PerfilMotoristaRepository extends JpaRepository<PerfilMotorista, Long> {
    Optional<PerfilMotorista> findByNumeroCnh(String numeroCnh);
    boolean existsByNumeroCnh(String numeroCnh);
    
    @Query("SELECT COUNT(p) FROM PerfilMotorista p WHERE p.usuario.empresa.id = :empresaId")
    long countByEmpresaId(@Param("empresaId") Long empresaId);
    
    @Query("SELECT p FROM PerfilMotorista p WHERE p.usuario.empresa.id = :empresaId")
    List<PerfilMotorista> findAllByUsuarioEmpresaId(@Param("empresaId") Long empresaId);
}
