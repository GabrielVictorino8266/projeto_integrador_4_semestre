package com.projetoanderson.app.dto;

import com.projetoanderson.app.model.entity.enums.TipoCNH;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PerfilMotoristaRequestDTO {
	

    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long usuarioId;

    @NotNull(message = "O tipo da CNH é obrigatório.")
    private TipoCNH tipoCnh;

    @NotBlank(message = "O número da CNH é obrigatório.")
    @Pattern(regexp = "^\\d{11}$", message = "Número da CNH deve conter 11 dígitos.")
    private String numeroCnh;

    @Min(value = 1, message = "Desempenho deve ser no mínimo 1.")
    @Max(value = 10, message = "Desempenho deve ser no máximo 10.")
    private Integer desempenho;

    // Getters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public TipoCNH getTipoCnh() {
        return tipoCnh;
    }

    public String getNumeroCnh() {
        return numeroCnh;
    }

    public Integer getDesempenho() {
        return desempenho;
    }

}
