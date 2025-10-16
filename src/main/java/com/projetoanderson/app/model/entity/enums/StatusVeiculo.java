package com.projetoanderson.app.model.entity.enums;

public enum StatusVeiculo {

    ATIVO("ativo", "Ativo"),
    EXCLUIDO("excluido", "Excluído"),
    MANUTENCAO("manutencao", "Manutenção"),
    INDISPONIVEL("indisponivel", "Indisponível");

    private final String valor;
    private final String descricao;

    StatusVeiculo(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusVeiculo fromString(String valor) {
        for (StatusVeiculo s : StatusVeiculo.values()) {
            if (s.valor.equalsIgnoreCase(valor)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de veículo inválido: " + valor);
    }
}
