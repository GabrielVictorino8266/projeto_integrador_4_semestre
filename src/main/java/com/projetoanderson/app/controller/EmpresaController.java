package com.projetoanderson.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetoanderson.app.dto.EmpresaRequestDTO;
import com.projetoanderson.app.dto.EmpresaResponseDTO;
import com.projetoanderson.app.service.EmpresaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {
	
	private final EmpresaService empresaService;

	public EmpresaController(EmpresaService empresaService) {
		this.empresaService = empresaService;
	}
	
	@GetMapping
	public ResponseEntity<List<EmpresaResponseDTO>> buscarTodas(){
		return ResponseEntity.ok(empresaService.buscarTodas());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<EmpresaResponseDTO> buscarEmpresaPorID(@PathVariable Long id){
		return ResponseEntity.ok(empresaService.buscarEmpresaPorID(id));
	}
	
	@PostMapping
	public ResponseEntity<EmpresaResponseDTO> criarEmpresa(@RequestBody @Valid EmpresaRequestDTO dto){
		EmpresaResponseDTO novaEmpresa = empresaService.criarEmpresa(dto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(novaEmpresa);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<EmpresaResponseDTO> atualizarEmpresa(@PathVariable Long id, @RequestBody @Valid EmpresaRequestDTO dto){
		return ResponseEntity.ok(empresaService.atualizarEmpresa(id, dto));
	}

	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletarEmpresa(@PathVariable Long id){
		empresaService.deletePorId(id);
		return ResponseEntity.noContent().build();
		
	}
	

}
