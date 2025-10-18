package com.projetoanderson.app.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

	private Empresa getEmpresaDoUsuarioLogado() {
		UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();
		Empresa empresa = usuarioAuth.getUsuario().getEmpresa();
		if (empresa == null) {
			if (temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
				throw new IllegalStateException("Super Admin não está associado a uma empresa padrão.");
			}
			throw new IllegalStateException("Usuário autenticado não está associado a nenhuma empresa.");
		}
		return empresa;
	}

	private boolean temRole(UsuarioAuthenticated usuarioAuth, String roleName) {
		return usuarioAuth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.anyMatch(role -> role.equals(roleName));
	}

	public void validarAcessoEmpresa(Long idEmpresaRequerida) {
		UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();

		if (temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
			return;
		}

		Empresa empresaDoUsuario = usuarioAuth.getUsuario().getEmpresa();
		if (empresaDoUsuario == null || !empresaDoUsuario.getId().equals(idEmpresaRequerida)) {
			throw new AccessDeniedException("Acesso negado. Você não pertence a esta empresa.");
		}
	}

	@Transactional
	public void deletePorId(Long id) {
		validarAcessoEmpresa(id);

		if (!empresaRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa com id " + id + " não encontrada.");
		}
		empresaRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public EmpresaResponseDTO buscarEmpresaPorID(Long id) {
		validarAcessoEmpresa(id); 

		return empresaRepository.findById(id).map(this::converterResponseParaDTO).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa com id " + id + " não encontrada."));
	}

	@Transactional(readOnly = true)
	public List<EmpresaResponseDTO> buscarTodas() {
		UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();

		if (temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
			return empresaRepository.findAll().stream().map(this::converterResponseParaDTO)
					.collect(Collectors.toList());
		} else {
			Empresa empresaLogada = getEmpresaDoUsuarioLogado();
			return Collections.singletonList(converterResponseParaDTO(empresaLogada));
		}
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
		validarAcessoEmpresa(id);

		Empresa empresaAAtualizar = empresaRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa com id " + id + " não encontrada."));

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
		if (cnpj == null || cnpj.length() != 14) {
			return cnpj;
		}
		return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/"
				+ cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
	}
}