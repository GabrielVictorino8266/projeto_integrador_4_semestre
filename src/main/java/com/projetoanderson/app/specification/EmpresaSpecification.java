package com.projetoanderson.app.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.projetoanderson.app.model.entity.Empresa;

import jakarta.persistence.criteria.Predicate;

public class EmpresaSpecification {

	public static Specification<Empresa> comFiltros(Long idEmpresaEspecifica, String razaoSocial, String nomeFantasia,
			String cnpj) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (idEmpresaEspecifica != null) {
				predicates.add(cb.equal(root.get("id"), idEmpresaEspecifica));
			}

			if (StringUtils.hasText(razaoSocial)) {
				predicates.add(cb.like(cb.lower(root.get("razaoSocial")), "%" + razaoSocial.toLowerCase() + "%"));
			}
			if (StringUtils.hasText(nomeFantasia)) {
				predicates.add(cb.like(cb.lower(root.get("nomeFantasia")), "%" + nomeFantasia.toLowerCase() + "%"));
			}
			if (StringUtils.hasText(cnpj)) {
				String cnpjNumerico = cnpj.replaceAll("[^0-9]", "");
				if (StringUtils.hasText(cnpjNumerico)) {
					predicates.add(cb.like(root.get("cnpj"), "%" + cnpjNumerico + "%"));
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public static Specification<Empresa> pertenceAEmpresa(Long empresaId) {
		return (root, query, cb) -> cb.equal(root.get("id"), empresaId);
	}

	public static Specification<Empresa> razaoSocialContem(String razaoSocial) {
		return (root, query, cb) -> {
			if (!StringUtils.hasText(razaoSocial))
				return cb.conjunction();
			return cb.like(cb.lower(root.get("razaoSocial")), "%" + razaoSocial.toLowerCase() + "%");
		};
	}

	public static Specification<Empresa> nomeFantasiaContem(String nomeFantasia) {
		return (root, query, cb) -> {
			if (!StringUtils.hasText(nomeFantasia))
				return cb.conjunction();
			return cb.like(cb.lower(root.get("nomeFantasia")), "%" + nomeFantasia.toLowerCase() + "%");
		};
	}

	public static Specification<Empresa> cnpjContem(String cnpj) {
		String cnpjNumerico = cnpj != null ? cnpj.replaceAll("[^0-9]", "") : null;
		return (root, query, cb) -> {
			if (!StringUtils.hasText(cnpjNumerico))
				return cb.conjunction();
			return cb.like(root.get("cnpj"), "%" + cnpjNumerico + "%");
		};
	}
}