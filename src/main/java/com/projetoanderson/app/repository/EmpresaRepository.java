package com.projetoanderson.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.model.entity.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
}
