package com.projetoanderson.app.model.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoVeiculo {

    CARRO("carro", "Carro"),
    MOTO("moto", "Moto"),
    CAMINHAO("caminhao", "Caminhão"),
    ONIBUS("onibus", "Ônibus"),
    VAN("van", "Van");

    private final String valor;
    private final String descricao;

    TipoVeiculo(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static TipoVeiculo fromString(String valor) {
        for (TipoVeiculo t : TipoVeiculo.values()) {
            if (t.valor.equalsIgnoreCase(valor)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de veículo inválido: " + valor);
    }
}