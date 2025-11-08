package com.projetoanderson.app.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projetoanderson.app.dto.ManutencaoCustoMesDTO;
import com.projetoanderson.app.model.entity.Manutencao;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long>, JpaSpecificationExecutor<Manutencao> {
	@Query(value = "SELECT new com.projetoanderson.app.dto.ManutencaoCustoMesDTO(" +
	           "   to_char(m.dataManutencao, 'YYYY-MM'), SUM(m.custo)) " +
	           "FROM Manutencao m " +
	           "JOIN m.veiculo v " +
	           "WHERE v.empresa.id = :empresaId AND m.dataManutencao >= :dataInicio " +
	           "GROUP BY to_char(m.dataManutencao, 'YYYY-MM') " +
	           "ORDER BY to_char(m.dataManutencao, 'YYYY-MM') ASC")
	    List<ManutencaoCustoMesDTO> findCustosUltimosMeses(
	            @Param("empresaId") Long empresaId, 
	            @Param("dataInicio") LocalDate dataInicio);
}