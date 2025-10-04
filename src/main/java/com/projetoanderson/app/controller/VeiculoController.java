package com.projetoanderson.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.projetoanderson.app.model.dto.VeiculoCreateDTO;
import com.projetoanderson.app.model.dto.VeiculoResponseDTO;
import com.projetoanderson.app.model.dto.VeiculoUpdateDTO;
import com.projetoanderson.app.service.VeiculoService;

@RestController
@RequestMapping("/vehicles")
public class VeiculoController {
    
    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
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
}