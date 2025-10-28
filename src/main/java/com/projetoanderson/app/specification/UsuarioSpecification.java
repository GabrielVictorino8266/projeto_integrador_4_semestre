package com.projetoanderson.app.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.projetoanderson.app.model.entity.Usuario;

import jakarta.persistence.criteria.Predicate;

public class UsuarioSpecification {

    public static Specification<Usuario> comFiltros(Long idEmpresaEspecifica, String nome, String email, String cpf, String telefone) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Empresa filter - always AND
            if (idEmpresaEspecifica != null) {
                predicates.add(cb.equal(root.get("empresa").get("id"), idEmpresaEspecifica));
            }

            // Other filters - OR between them
            List<Predicate> orPredicates = new ArrayList<>();

            if (StringUtils.hasText(nome)) {
                orPredicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(email)) {
                orPredicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(cpf)) {
                String cpfNumerico = cpf.replaceAll("[^0-9]", "");
                if (StringUtils.hasText(cpfNumerico)) {
                    orPredicates.add(cb.like(root.get("cpf"), "%" + cpfNumerico + "%"));
                }
            }

            if (StringUtils.hasText(telefone)) {
                String telefoneNumerico = telefone.replaceAll("[^0-9]", "");
                if (StringUtils.hasText(telefoneNumerico)) {
                    orPredicates.add(cb.like(root.get("telefone"), "%" + telefoneNumerico + "%"));
                }
            }

            // Combine: empresa AND (nome OR email OR cpf)
            if (!orPredicates.isEmpty()) {
                Predicate orCombined = cb.or(orPredicates.toArray(new Predicate[0]));
                predicates.add(orCombined);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Usuario> pertenceAEmpresa(Long empresaId) {
        return (root, query, cb) -> cb.equal(root.get("empresa").get("id"), empresaId);
    }
}