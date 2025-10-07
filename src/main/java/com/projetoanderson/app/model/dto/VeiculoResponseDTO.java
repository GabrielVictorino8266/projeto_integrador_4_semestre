package com.projetoanderson.app.model.dto;

import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;

public record VeiculoResponseDTO(
    Long id,
    String vehicleNumber,
    String licensePlate,
    TipoVeiculo vehicleType,
    Integer manufacturingYear,
    String brand,
    Integer currentKm,
    Integer warningKmLimit,
    StatusVeiculo status
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
            veiculo.getStatus()
        );
    }
}