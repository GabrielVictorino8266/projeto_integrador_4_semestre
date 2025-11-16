package com.projetoanderson.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.projetoanderson.app.dto.PaginacaoResponseDTO;
import com.projetoanderson.app.dto.VeiculoPatchDTO;
import com.projetoanderson.app.dto.VeiculoRequestDTO;
import com.projetoanderson.app.dto.VeiculoResponseDTO;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo; // IMPORTADO
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
    public ResponseEntity<Page<VeiculoResponseDTO>> buscarTodos(
            @RequestParam(required = false) Long empresa_id,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String tipoVeiculo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String chassi,
            @PageableDefault(sort = "placa") Pageable pageable) {
            
        Page<VeiculoResponseDTO> pagina = veiculoService.buscarTodos(
            empresa_id, placa, tipoVeiculo, status, marca, modelo, chassi, 
            pageable);
        
        if (pagina.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> findById(@PathVariable Long id) {
        // O Service já valida se o usuário tem acesso a este veículo
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }
    
    

    @PostMapping 
    public ResponseEntity<?> create(
            @Valid @RequestBody VeiculoRequestDTO data) {
        try {
            VeiculoResponseDTO novoVeiculo = veiculoService.criar(data);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoVeiculo);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getReason()));
            }
            throw e; 
        }
    }
    

    @PatchMapping("/{id}") 
    public ResponseEntity<VeiculoResponseDTO> updatePartial(
            @PathVariable Long id,
            @Valid @RequestBody VeiculoPatchDTO data) {
        return ResponseEntity.ok(veiculoService.atualizarParcialmente(id, data));
    }


    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        veiculoService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lookups")
    public ResponseEntity<Map<String, List<Map<String, String>>>> listEnums() {
        List<Map<String, String>> statusList = Arrays.stream(StatusVeiculo.values())
                .map(status -> Map.of(
                        "label", status.getDescricao(),
                        "value", status.getValor()
                ))
                .collect(Collectors.toList());
        
        List<Map<String, String>> tipoList = Arrays.stream(TipoVeiculo.values())
                .map(tipo -> Map.of(
                        "label", tipo.getDescricao(),
                        "value", tipo.getValor() 
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(Map.of("status", statusList, "tipos", tipoList));
    }
}