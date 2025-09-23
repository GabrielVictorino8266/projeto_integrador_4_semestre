package com.projetoanderson.app.model.entity.enums;

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

    // Para buscar enum pelo valor persistido
    public static TipoVeiculo fromValor(String valor) {
        for (TipoVeiculo t : TipoVeiculo.values()) {
            if (t.valor.equalsIgnoreCase(valor)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de veículo inválido: " + valor);
    }
}
