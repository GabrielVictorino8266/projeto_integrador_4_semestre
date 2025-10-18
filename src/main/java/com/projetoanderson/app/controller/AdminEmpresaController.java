package com.projetoanderson.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetoanderson.app.dto.EmpresaRequestDTO;
import com.projetoanderson.app.dto.EmpresaResponseDTO;
import com.projetoanderson.app.service.EmpresaService;

import jakarta.validation.Valid;

/**
 * Controlador REST para operações administrativas do sistema (Super Admin).
 * Requer a role 'SUPER_ADMIN' para acesso.
 */
@RestController
@RequestMapping("/api/admin/empresas")
public class AdminEmpresaController {
    
    private final EmpresaService empresaService;

	public AdminEmpresaController(EmpresaService empresaService) {
		this.empresaService = empresaService;
	}

    /**
     * Cria uma nova empresa no sistema.
     * Endpoint restrito apenas para usuários com ROLE_SUPER_ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaResponseDTO> criarEmpresaComoSuperAdmin(
            @RequestBody @Valid EmpresaRequestDTO dto) {
        
        // Chama o método de serviço dedicado à criação por super admin
        EmpresaResponseDTO novaEmpresa = empresaService.criarEmpresaComoSuperAdmin(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(novaEmpresa);
    }
}