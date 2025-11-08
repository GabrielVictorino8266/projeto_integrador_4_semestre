package com.projetoanderson.app.repository;

import java.util.List; // IMPORTAR
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query; // IMPORTAR
import org.springframework.data.repository.query.Param; // IMPORTAR
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.dto.DashboardResponseDTO; // IMPORTAR
import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo; // IMPORTAR

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long>, JpaSpecificationExecutor<Veiculo> {
    Optional<Veiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    boolean existsByPlacaAndIdNot(String placa, Long id);
    Optional<Veiculo> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByPlacaAndEmpresaId(String placa, Long empresaId);
    long countByEmpresaId(Long empresaId);
    boolean existsByIdAndEmpresaId(Long id, Long empresaId);

    long countByEmpresaIdAndStatus(Long empresaId, StatusVeiculo status);

    @Query("SELECT v.status, COUNT(v) FROM Veiculo v WHERE v.empresa.id = :empresaId GROUP BY v.status")
    List<Object[]> countGroupByStatus(@Param("empresaId") Long empresaId);
    @Query("SELECT new com.projetoanderson.app.dto.DashboardResponseDTO$AlertaKmDTO(v.id, v.placa, v.kmAtual, v.limiteAvisoKm, v.marca) " +
           "FROM Veiculo v " +
           "WHERE v.empresa.id = :empresaId AND v.kmAtual >= (v.limiteAvisoKm * 0.9) " + // 90% do limite
           "ORDER BY (v.kmAtual * 1.0 / v.limiteAvisoKm) DESC")
    List<DashboardResponseDTO.AlertaKmDTO> findAlertaManutencaoKm(@Param("empresaId") Long empresaId);
    
}