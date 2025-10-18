package com.projetoanderson.app.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;

public class VeiculoSpecification {

    public static Specification<Veiculo> comEmpresa(Long empresaId) {
        return (root, query, builder) -> builder.equal(root.get("empresa").get("id"), empresaId);
    }

    public static Specification<Veiculo> comPlaca(String placa) {
        // Retorna nulo se o filtro não for fornecido, o que é ignorado pelo .and()
        if (!StringUtils.hasText(placa)) {
            return null;
        }
        return (root, query, builder) -> 
            builder.like(builder.lower(root.get("placa")), "%" + placa.toLowerCase() + "%");
    }

    public static Specification<Veiculo> comMarca(String marca) {
        if (!StringUtils.hasText(marca)) {
            return null;
        }
        return (root, query, builder) -> 
            builder.like(builder.lower(root.get("marca")), "%" + marca.toLowerCase() + "%");
    }

    public static Specification<Veiculo> comTipo(String tipoVeiculo) {
        if (!StringUtils.hasText(tipoVeiculo)) {
            return null;
        }
        try {
            TipoVeiculo tipo = TipoVeiculo.fromString(tipoVeiculo);
            return (root, query, builder) -> builder.equal(root.get("tipoVeiculo"), tipo);
        } catch (IllegalArgumentException e) {
            // Se o tipo for inválido, retorna uma condição que nunca será verdadeira.
            return (root, query, builder) -> builder.disjunction();
        }
    }

    public static Specification<Veiculo> comStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        try {
            StatusVeiculo statusEnum = StatusVeiculo.fromString(status);
            return (root, query, builder) -> builder.equal(root.get("status"), statusEnum);
        } catch (IllegalArgumentException e) {
            return (root, query, builder) -> builder.disjunction();
        }
    }
}