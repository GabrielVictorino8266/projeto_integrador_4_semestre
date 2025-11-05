package com.projetoanderson.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetoanderson.app.dto.PagamentoPixRequestDTO;
import com.projetoanderson.app.dto.PagamentoPixResponseDTO;
import com.projetoanderson.app.service.PagamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/pix")
    public ResponseEntity<PagamentoPixResponseDTO> criarPagamentoPix(
            @Valid @RequestBody PagamentoPixRequestDTO requestDTO) {
        
        PagamentoPixResponseDTO response = pagamentoService.criarPagamentoPix(requestDTO);
        return ResponseEntity.ok(response);
    }
}