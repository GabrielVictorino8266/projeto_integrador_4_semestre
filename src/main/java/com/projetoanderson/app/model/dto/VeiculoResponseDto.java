package com.projetoanderson.app.model.dto;

import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;

public record VeiculoResponseDto(
    Long id,
    String numeroVeiculo,
    String placa,
    TipoVeiculo tipoVeiculo,
    Integer anoFabricacao,
    String marca,
    Integer kmAtual,
    Integer limiteAvisoKm
) {
    public VeiculoResponseDto(Veiculo veiculo) {
        this(
            veiculo.getId(),
            veiculo.getNumeroVeiculo(),
            veiculo.getPlaca(),
            veiculo.getTipoVeiculo(),
            veiculo.getAnoFabricacao(),
            veiculo.getMarca(),
            veiculo.getKmAtual(),
            veiculo.getLimiteAvisoKm()
        );
    }
}