package com.projetoanderson.app.specification;

import com.projetoanderson.app.model.dto.VeiculoFilterDTO;
import com.projetoanderson.app.model.entity.Veiculo;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

public class VeiculoSpecification {

    public static Specification<Veiculo> withFilters(VeiculoFilterDTO filter) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (filter.getLicensePlate() != null) {
                predicate = builder.and(predicate,
                    builder.equal(root.get("placa"), filter.getLicensePlate()));
            }

            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicate = builder.and(predicate,
                    root.get("status").in(filter.getStatus()));
            }

            return predicate;
        };
    }
}
