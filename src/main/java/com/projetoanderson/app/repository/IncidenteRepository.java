package com.projetoanderson.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.model.entity.Incidente;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {
}
