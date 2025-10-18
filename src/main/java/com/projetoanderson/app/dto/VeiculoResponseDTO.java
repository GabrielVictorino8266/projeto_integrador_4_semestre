package com.projetoanderson.app.dto;

import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;

public record VeiculoResponseDTO(
    Long id,
    String numeroVeiculo,
    String placa,
    TipoVeiculo tipoVeiculo,
    Integer anoFabricacao,
    String marca,
    Integer kmAtual,
    Integer limiteAvisoKm,
    StatusVeiculo status,
    Long empresaId
) {
    public VeiculoResponseDTO(Veiculo veiculo) {
        this(
            veiculo.getId(),
            veiculo.getNumeroVeiculo(),
            veiculo.getPlaca(),
            veiculo.getTipoVeiculo(),
            veiculo.getAnoFabricacao(),
            veiculo.getMarca(),
            veiculo.getKmAtual(),
            veiculo.getLimiteAvisoKm(),
            veiculo.getStatus(),
            veiculo.getEmpresa().getId()
        );
    }
}