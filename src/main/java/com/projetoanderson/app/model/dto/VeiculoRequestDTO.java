package com.projetoanderson.app.model.dto;

import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonAlias;

import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.validation.ValidEnum;

public class VeiculoRequestDTO {
    @NotEmpty
    @Size(max = 10)
    @Pattern(regexp = "^\\w+$")
    @JsonAlias({"vehicleNumber", "numeroVeiculo"})     
    private String numeroVeiculo;

    @NotEmpty
    @Size(max = 8)
    @Pattern(regexp = "^[A-Z]{3}[0-9][0-9A-Z][0-9]{2}$")
    @JsonAlias({"licensePlate", "placa"})
    private String placa;

    @NotEmpty
    @ValidEnum(enumClass = TipoVeiculo.class, ignoreCase = true)
    @JsonAlias({"vehicleType", "tipoVeiculo"})
    private String tipoVeiculo;

    @NotNull
    @Min(1900)
    @JsonAlias({"manufacturingYear", "anoFabricacao"})
    private Integer anoFabricacao;

    @NotEmpty
    @Size(max = 20)
    @Pattern(regexp = "^\\w+$")
    @JsonAlias({"brand", "marca"})
    private String marca;

    @NotNull
    @Min(0)
    @JsonAlias({"currentKm", "kmAtual"})
    private Integer kmAtual;

    @NotNull
    @Min(0)
    @JsonAlias({"warningKmLimit", "limiteAvisoKm"})
    private Integer limiteAvisoKm;
    
    @AssertTrue(message = "O ano de fabricação não pode ser maior que o ano atual")
    private boolean isAnoFabricacaoValid() {
        if (anoFabricacao == null) {
            return true; // deixa @NotNull cuidar disso
        }
        return anoFabricacao <= java.time.Year.now().getValue();
    }
    
    // Getters

    public String getNumeroVeiculo() {
        return numeroVeiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }

    public String getMarca() {
        return marca;
    }

    public Integer getKmAtual() {
        return kmAtual;
    }

    public Integer getLimiteAvisoKm() {
        return limiteAvisoKm;
    }

    // Setters
    
    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public void setNumeroVeiculo(String numeroVeiculo) {
        this.numeroVeiculo = numeroVeiculo;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setAnoFabricacao(Integer anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setKmAtual(Integer kmAtual) {
        this.kmAtual = kmAtual;
    }

    public void setLimiteAvisoKm(Integer limiteAvisoKm) {
        this.limiteAvisoKm = limiteAvisoKm;
    }

}