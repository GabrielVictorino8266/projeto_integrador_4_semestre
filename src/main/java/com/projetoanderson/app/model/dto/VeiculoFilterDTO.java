package com.projetoanderson.app.model.dto;

import java.util.List;

import com.projetoanderson.app.model.entity.enums.StatusVeiculo;

public class VeiculoFilterDTO {
    private List<StatusVeiculo> status = List.of(StatusVeiculo.ATIVO);

    private String licensePlate;

    // Getters

    public List<StatusVeiculo> getStatus() {
        return status;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    // Setters
    
    public void setStatus(List<StatusVeiculo> status) {
        this.status = status;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

}