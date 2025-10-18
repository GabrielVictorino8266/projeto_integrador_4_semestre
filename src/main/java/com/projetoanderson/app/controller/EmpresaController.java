package com.projetoanderson.app.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.projetoanderson.app.dto.EmpresaPatchDTO;
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
	public ResponseEntity<Page<EmpresaResponseDTO>> buscarTodas(@RequestParam(required = false) String razaoSocial,
			@RequestParam(required = false) String nomeFantasia, @RequestParam(required = false) String cnpj,
			@PageableDefault(size = 20, sort = "nomeFantasia") Pageable pageable) {
		Page<EmpresaResponseDTO> pagina = empresaService.buscarTodasComFiltro(razaoSocial, nomeFantasia, cnpj,
				pageable);

		if (pagina.isEmpty()) {
			return ResponseEntity.noContent().build(); // 204 No Content
		}
		return ResponseEntity.ok(pagina);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EmpresaResponseDTO> buscarEmpresaPorID(@PathVariable Long id) {
		// Service retorna Optional.empty() se não encontrado ou acesso negado
		Optional<EmpresaResponseDTO> dtoOptional = empresaService.buscarEmpresaPorIDOptional(id);

		return dtoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
	}

	@PostMapping
	public ResponseEntity<EmpresaResponseDTO> criarEmpresa(@RequestBody @Valid EmpresaRequestDTO dto) {
		EmpresaResponseDTO novaEmpresa = empresaService.criarEmpresa(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(novaEmpresa);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<EmpresaResponseDTO> atualizarEmpresaParcialmente(@PathVariable Long id,
			@RequestBody @Valid EmpresaPatchDTO dto) { // Usa PatchDTO
		return ResponseEntity.ok(empresaService.atualizarEmpresaParcialmente(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletarEmpresa(@PathVariable Long id) {
		try {
			empresaService.deletePorId(id);
			return ResponseEntity.noContent().build();
		} catch (ResponseStatusException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return ResponseEntity.noContent().build();
			}
			throw e;
		}
	}
}