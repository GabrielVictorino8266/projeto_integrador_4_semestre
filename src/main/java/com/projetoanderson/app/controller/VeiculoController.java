package com.projetoanderson.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projetoanderson.app.dto.PaginacaoResponseDTO;
import com.projetoanderson.app.dto.VeiculoRequestDTO;
import com.projetoanderson.app.dto.VeiculoResponseDTO;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.service.VeiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {
    
    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public ResponseEntity<PaginacaoResponseDTO<VeiculoResponseDTO>> buscarTodos(
            @RequestParam(required = false) Long empresa_id,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String tipoVeiculo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String marca,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<VeiculoResponseDTO> paginaDeVeiculos = veiculoService.buscarTodos(
            empresa_id, placa, tipoVeiculo, status, marca, page, size);
        
        PaginacaoResponseDTO<VeiculoResponseDTO> resposta = new PaginacaoResponseDTO<>(paginaDeVeiculos);
        
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }
    
    
    @PostMapping 
    public ResponseEntity<?> create(
            @Valid @RequestBody VeiculoRequestDTO data) {
        try {
            VeiculoResponseDTO novoVeiculo = veiculoService.criar(data);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoVeiculo);
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("409")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Limite de veículos atingido para o plano atual"));
            }
            throw e; 
        }
    }
    
    @PutMapping("/{id}") 
    public ResponseEntity<VeiculoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody VeiculoRequestDTO data) {
        return ResponseEntity.ok(veiculoService.atualizar(id, data));
    }

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        veiculoService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<List<Map<String, String>>> listStatus() {
        List<Map<String, String>> statusList = Arrays.stream(StatusVeiculo.values())
                .map(status -> Map.of("descricao", status.getDescricao(), "valor", status.getValor()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(statusList);
    }
}