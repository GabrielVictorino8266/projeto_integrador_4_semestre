package com.projetoanderson.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.projetoanderson.app.model.entity.Manutencao;
import com.projetoanderson.app.model.entity.enums.TipoManutencao;

public record ManutencaoResponseDTO(
    Long id,
    LocalDate dataManutencao,
    String descricao,
    BigDecimal custo,
    TipoManutencao tipoManutencao,
    Long veiculoId,
    String placaVeiculo
) {
    public ManutencaoResponseDTO(Manutencao manutencao) {
        this(
            manutencao.getId(),
            manutencao.getDataManutencao(),
            manutencao.getDescricao(),
            manutencao.getCusto(),
            manutencao.getTipoManutencao(),
            manutencao.getVeiculo().getId(),
            manutencao.getVeiculo().getPlaca()
        );
    }
}