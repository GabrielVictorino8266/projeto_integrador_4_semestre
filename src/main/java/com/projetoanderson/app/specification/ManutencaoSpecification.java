package com.projetoanderson.app.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.projetoanderson.app.model.entity.Manutencao;
import com.projetoanderson.app.model.entity.enums.TipoManutencao;

public class ManutencaoSpecification {

    public static Specification<Manutencao> comEmpresa(Long empresaId) {
        if (empresaId == null) {
            return null;
        }
        return (root, query, builder) -> 
            builder.equal(root.get("veiculo").get("empresa").get("id"), empresaId);
    }

    public static Specification<Manutencao> comVeiculo(Long veiculoId) {
        if (veiculoId == null) {
            return null;
        }
        return (root, query, builder) -> 
            builder.equal(root.get("veiculo").get("id"), veiculoId);
    }
    public static Specification<Manutencao> comTipo(String tipo) {
        if (!StringUtils.hasText(tipo)) {
            return null;
        }
        try {
            TipoManutencao tipoEnum = TipoManutencao.valueOf(tipo.toUpperCase());
            return (root, query, builder) -> builder.equal(root.get("tipoManutencao"), tipoEnum);
        } catch (IllegalArgumentException e) {
            return (root, query, builder) -> builder.disjunction();
        }
    }
    public static Specification<Manutencao> entreDatas(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            return (root, query, builder) -> 
                builder.between(root.get("dataManutencao"), inicio, fim);
        }
        if (inicio != null) {
            return (root, query, builder) -> 
                builder.greaterThanOrEqualTo(root.get("dataManutencao"), inicio);
        }
        if (fim != null) {
            return (root, query, builder) -> 
                builder.lessThanOrEqualTo(root.get("dataManutencao"), fim);
        }
        return null;
    }
}