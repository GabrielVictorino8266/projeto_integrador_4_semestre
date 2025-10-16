package com.projetoanderson.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projetoanderson.app.dto.EmpresaRequestDTO;
import com.projetoanderson.app.dto.EmpresaResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.repository.EmpresaRepository;

@Service
public class EmpresaService {
	
	private final EmpresaRepository empresaRepository;

	public EmpresaService(EmpresaRepository empresaRepository) {
		this.empresaRepository = empresaRepository;
	}
	
	public void deletePorId(Long id){
		if(!empresaRepository.existsById(id))
			throw new RuntimeException("Veículo com id " + id + " não encontrado.");
		empresaRepository.deleteById(id);
	}
	
	public EmpresaResponseDTO buscarEmpresaPorID(Long id) {
		return empresaRepository.findById(id)
				.map(this::converterResponseParaDTO)
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada com id " + id));	
	}
	
	public List<EmpresaResponseDTO> buscarTodas() {
		return empresaRepository.findAll()
				.stream()
				.map(this::converterResponseParaDTO)
				.collect(Collectors.toList());	
	}
	
	public EmpresaResponseDTO criarEmpresa(EmpresaRequestDTO requestDTO) {
		if(empresaRepository.existsByCnpj(requestDTO.getCnpj())) {
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
	
	public EmpresaResponseDTO atualizarEmpresa(Long id, EmpresaRequestDTO empresa) {
		Empresa empresaAAtualizar = empresaRepository.findById(id)
				.orElseThrow(()->new RuntimeException("Empresa não existe com id " + id + " não é possível atualizar."));
		
		empresaAAtualizar.setNomeFantasia(empresa.getNomeFantasia());
		empresaAAtualizar.setCnpj(empresa.getCnpj());
		empresaAAtualizar.setRazaoSocial(empresa.getRazaoSocial());
		
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
