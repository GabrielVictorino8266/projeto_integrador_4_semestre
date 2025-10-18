package com.projetoanderson.app.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.projetoanderson.app.model.entity.Usuario;

import jakarta.persistence.criteria.Predicate;

public class UsuarioSpecification {

    public static Specification<Usuario> comFiltros(Long idEmpresaEspecifica, String nome, String email, String cpf) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idEmpresaEspecifica != null) {
                predicates.add(cb.equal(root.get("empresa").get("id"), idEmpresaEspecifica));
            }

            if (StringUtils.hasText(nome)) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(email)) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(cpf)) {
                String cpfNumerico = cpf.replaceAll("[^0-9]", "");
                if(StringUtils.hasText(cpfNumerico)){
                   predicates.add(cb.like(root.get("cpf"), "%" + cpfNumerico + "%"));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Usuario> pertenceAEmpresa(Long empresaId) {
         return (root, query, cb) -> cb.equal(root.get("empresa").get("id"), empresaId);
    }
}