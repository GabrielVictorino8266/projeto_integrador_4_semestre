package com.projetoanderson.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import jakarta.validation.Valid;

import com.projetoanderson.app.model.dto.VeiculoCreateDTO;
import com.projetoanderson.app.model.dto.VeiculoFilterDTO;
import com.projetoanderson.app.model.dto.VeiculoResponseDTO;
import com.projetoanderson.app.model.dto.VeiculoUpdateDTO;
import com.projetoanderson.app.model.dto.VeiculoStatusDTO;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.service.VeiculoService;

@RestController
@RequestMapping("/vehicles")
public class VeiculoController {
    
    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public Page<VeiculoResponseDTO> findAll(
            @Valid @ModelAttribute VeiculoFilterDTO filters, 
            Pageable pageable) {
        return veiculoService.findAll(filters, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<VeiculoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.findById(id));
    }
    
    
    @PostMapping("/create")
    public ResponseEntity<VeiculoResponseDTO> create(
            @Valid @RequestBody VeiculoCreateDTO data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(veiculoService.create(data));
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<VeiculoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody VeiculoUpdateDTO data) {
        return ResponseEntity
                .ok(veiculoService.update(id, data));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<List<VeiculoStatusDTO>> listStatus() {
        List<VeiculoStatusDTO> statusList = Arrays.stream(StatusVeiculo.values())
                .map(status -> new VeiculoStatusDTO(status.getDescricao(), status.getValor()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(statusList);
    }
}