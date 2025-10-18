package com.projetoanderson.app.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.projetoanderson.app.dto.EmpresaPatchDTO;
import com.projetoanderson.app.dto.EmpresaRequestDTO;
import com.projetoanderson.app.dto.EmpresaResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;
import com.projetoanderson.app.specification.EmpresaSpecification;

@Service
public class EmpresaService {

	private final EmpresaRepository empresaRepository;

	public EmpresaService(EmpresaRepository empresaRepository) {
		this.empresaRepository = empresaRepository;
	}

	private UsuarioAuthenticated getUsuarioAutenticado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioAuthenticated)) {
			throw new IllegalStateException("Usuário não autenticado ou tipo de Principal inesperado.");
		}
		return (UsuarioAuthenticated) authentication.getPrincipal();
	}

	private boolean temRole(UsuarioAuthenticated usuarioAuth, String roleName) {
		return usuarioAuth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.anyMatch(role -> role.equals(roleName));
	}

	private Long getIdEmpresaUsuarioLogadoOuNull() {
		UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();
		if (temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
			return null;
		}
		Empresa empresaDoUsuario = usuarioAuth.getUsuario().getEmpresa();
		if (empresaDoUsuario == null) {
			throw new IllegalStateException(
					"Usuário autenticado (" + usuarioAuth.getUsername() + ") não está associado a nenhuma empresa.");
		}
		return empresaDoUsuario.getId();
	}

	public void validarAcessoEmpresa(Long idEmpresaRequerida) {
		UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();
		if (temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
			if (!empresaRepository.existsById(idEmpresaRequerida)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Empresa com id " + idEmpresaRequerida + " não encontrada.");
			}
			return; // Super Admin pode acessar
		}
		Long idEmpresaDoUsuario = getIdEmpresaUsuarioLogadoOuNull();
		if (!idEmpresaDoUsuario.equals(idEmpresaRequerida)) {
			throw new AccessDeniedException("Acesso negado. Você não pertence a esta empresa.");
		}
	}

	@Transactional
	public void deletePorId(Long id) {
		validarAcessoEmpresa(id);
		empresaRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public Optional<EmpresaResponseDTO> buscarEmpresaPorIDOptional(Long id) {
		try {
			validarAcessoEmpresa(id);
		} catch (AccessDeniedException | ResponseStatusException e) {
			return Optional.empty();
		}
		return empresaRepository.findById(id).map(this::converterResponseParaDTO);
	}

	@Transactional(readOnly = true)
	public Page<EmpresaResponseDTO> buscarTodasComFiltro(String razaoSocial, String nomeFantasia, String cnpj,
			Pageable pageable) {

		Long idEmpresaEspecifica = getIdEmpresaUsuarioLogadoOuNull();

		Specification<Empresa> spec = EmpresaSpecification.comFiltros(idEmpresaEspecifica, razaoSocial, nomeFantasia,
				cnpj);

		Page<Empresa> paginaEmpresas = empresaRepository.findAll(spec, pageable);
		return paginaEmpresas.map(this::converterResponseParaDTO);
	}

	@Transactional
	public EmpresaResponseDTO criarEmpresa(EmpresaRequestDTO requestDTO) {
		UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();
		if (!temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
			throw new AccessDeniedException("Apenas o Super Administrador pode criar novas empresas.");
		}

		String cnpj = requestDTO.getCnpj();
		if (empresaRepository.existsByCnpj(cnpj)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Conflito: Já existe uma empresa cadastrada com o CNPJ " + formatarCnpj(cnpj) + ".");
		}

		Empresa novaEmpresa = new Empresa();
		novaEmpresa.setRazaoSocial(requestDTO.getRazaoSocial());
		novaEmpresa.setNomeFantasia(requestDTO.getNomeFantasia());
		novaEmpresa.setCnpj(cnpj);
		novaEmpresa.setAtivo(true);
		Empresa empresaCriada = empresaRepository.save(novaEmpresa);
		return converterResponseParaDTO(empresaCriada);
	}

	@Transactional
	public EmpresaResponseDTO atualizarEmpresaParcialmente(Long id, EmpresaPatchDTO dto) {
		validarAcessoEmpresa(id); // Valida acesso e existência

		Empresa empresaAAtualizar = empresaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Empresa com id " + id + " não encontrada (inesperado após validação).")); // Não deve acontecer

		boolean modificado = false;

		if (dto.getRazaoSocial() != null && !dto.getRazaoSocial().equals(empresaAAtualizar.getRazaoSocial())) {
			empresaAAtualizar.setRazaoSocial(dto.getRazaoSocial());
			modificado = true;
		}
		if (dto.getNomeFantasia() != null && !dto.getNomeFantasia().equals(empresaAAtualizar.getNomeFantasia())) {
			empresaAAtualizar.setNomeFantasia(dto.getNomeFantasia());
			modificado = true;
		}
		String novoCnpj = dto.getCnpj();
		if (novoCnpj != null && !novoCnpj.equals(empresaAAtualizar.getCnpj())) {
			if (empresaRepository.existsByCnpj(novoCnpj)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflito: O novo CNPJ informado ("
						+ formatarCnpj(novoCnpj) + ") já está cadastrado em outra empresa.");
			}
			empresaAAtualizar.setCnpj(novoCnpj);
			modificado = true;
		}

		if (modificado) {
			empresaAAtualizar = empresaRepository.save(empresaAAtualizar);
		}
		return converterResponseParaDTO(empresaAAtualizar);
	}

	private EmpresaResponseDTO converterResponseParaDTO(Empresa empresa) {
		EmpresaResponseDTO dto = new EmpresaResponseDTO();
		dto.setId(empresa.getId());
		dto.setCnpj(empresa.getCnpj());
		dto.setNomeFantasia(empresa.getNomeFantasia());
		dto.setRazaoSocial(empresa.getRazaoSocial());
		return dto;
	}

	private String formatarCnpj(String cnpj) {
		if (cnpj == null || cnpj.length() != 14)
			return cnpj;
		return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/"
				+ cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
	}
}