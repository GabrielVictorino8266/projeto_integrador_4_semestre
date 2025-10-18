package com.projetoanderson.app.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projetoanderson.app.dto.EmpresaRequestDTO;
import com.projetoanderson.app.dto.EmpresaResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;

@Service
public class EmpresaService {

	private final EmpresaRepository empresaRepository;

	public EmpresaService(EmpresaRepository empresaRepository) {
		this.empresaRepository = empresaRepository;
	}

	// --- MÉTODO AUXILIAR DE SEGURANÇA ---
	private Empresa getEmpresaDoUsuarioLogado() {
		UsuarioAuthenticated usuarioAuth = (UsuarioAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Empresa empresa = usuarioAuth.getUsuario().getEmpresa();
		if (empresa == null) {
			throw new IllegalStateException("Usuário autenticado não está associado a nenhuma empresa.");
		}
		return empresa;
	}

	// --- MÉTODOS CRUD ATUALIZADOS ---

	@Transactional
	public void deletePorId(Long id) {
		Empresa empresaLogada = getEmpresaDoUsuarioLogado();
		if (!empresaLogada.getId().equals(id)) {
			throw new AccessDeniedException("Você não pode deletar a empresa de outro usuário.");
		}
		if (!empresaRepository.existsById(id)) {
			throw new RuntimeException("Empresa com id " + id + " não encontrada.");
		}
		empresaRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public EmpresaResponseDTO buscarEmpresaPorID(Long id) {
		Empresa empresaLogada = getEmpresaDoUsuarioLogado();
		if (!empresaLogada.getId().equals(id)) {
			throw new AccessDeniedException("Você só pode visualizar a sua própria empresa.");
		}
		return empresaRepository.findById(id)
				.map(this::converterResponseParaDTO)
				.orElseThrow(() -> new RuntimeException("Sua empresa (ID: " + id + ") não foi encontrada."));
	}

	@Transactional(readOnly = true)
	public List<EmpresaResponseDTO> buscarTodas() {
		Empresa empresaLogada = getEmpresaDoUsuarioLogado();
		return Collections.singletonList(converterResponseParaDTO(empresaLogada));
	}

	@Transactional
	public EmpresaResponseDTO criarEmpresa(EmpresaRequestDTO requestDTO) {
		// ESTE MÉTODO AGORA É SEGURO E BLOQUEIA A AÇÃO
		// Usuários normais (Admin de uma empresa) não podem criar outras empresas.
		throw new AccessDeniedException("Usuários não têm permissão para criar novas empresas.");
	}

	// NOVO MÉTODO PARA O SUPER ADMIN
	@Transactional
	public EmpresaResponseDTO criarEmpresaComoSuperAdmin(EmpresaRequestDTO requestDTO) {
		// Esta é a lógica de criação original, agora em um método separado e seguro.
		if (empresaRepository.existsByCnpj(requestDTO.getCnpj())) {
			throw new RuntimeException("Empresa já cadastrada com esse CNPJ.");
		}

		Empresa novaEmpresa = new Empresa();
		novaEmpresa.setRazaoSocial(requestDTO.getRazaoSocial());
		novaEmpresa.setNomeFantasia(requestDTO.getNomeFantasia());
		novaEmpresa.setCnpj(requestDTO.getCnpj());
		novaEmpresa.setAtivo(true);
		Empresa empresaCriada = empresaRepository.save(novaEmpresa);

		return converterResponseParaDTO(empresaCriada);
	}

	@Transactional
	public EmpresaResponseDTO atualizarEmpresa(Long id, EmpresaRequestDTO dto) {
		Empresa empresaLogada = getEmpresaDoUsuarioLogado();
		if (!empresaLogada.getId().equals(id)) {
			throw new AccessDeniedException("Você só pode atualizar os dados da sua própria empresa.");
		}

		Empresa empresaAAtualizar = empresaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sua empresa (ID: " + id + ") não foi encontrada."));

		if (!empresaAAtualizar.getCnpj().equals(dto.getCnpj()) && empresaRepository.existsByCnpj(dto.getCnpj())) {
			throw new RuntimeException("O novo CNPJ informado já está cadastrado em outra empresa.");
		}

		empresaAAtualizar.setNomeFantasia(dto.getNomeFantasia());
		empresaAAtualizar.setCnpj(dto.getCnpj());
		empresaAAtualizar.setRazaoSocial(dto.getRazaoSocial());

		Empresa empresaAtualizada = empresaRepository.save(empresaAAtualizar);
		return converterResponseParaDTO(empresaAtualizada);
	}

	private EmpresaResponseDTO converterResponseParaDTO(Empresa empresa) {
		EmpresaResponseDTO dto = new EmpresaResponseDTO();
		dto.setId(empresa.getId());
		dto.setCnpj(empresa.getCnpj());
		dto.setNomeFantasia(empresa.getNomeFantasia());
		dto.setRazaoSocial(empresa.getRazaoSocial());
		return dto;
	}
}