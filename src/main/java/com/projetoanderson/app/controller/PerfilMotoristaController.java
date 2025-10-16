package com.projetoanderson.app.controller;

import com.projetoanderson.app.dto.PerfilMotoristaRequestDTO;
import com.projetoanderson.app.dto.PerfilMotoristaResponseDTO;
import com.projetoanderson.app.service.PerfilMotoristaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfis-motorista")
public class PerfilMotoristaController {

    private final PerfilMotoristaService perfilMotoristaService;

    public PerfilMotoristaController(PerfilMotoristaService perfilMotoristaService) {
        this.perfilMotoristaService = perfilMotoristaService;
    }

    @PostMapping
    public ResponseEntity<PerfilMotoristaResponseDTO> criar(@RequestBody @Valid PerfilMotoristaRequestDTO dto) {
        PerfilMotoristaResponseDTO novoPerfil = perfilMotoristaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPerfil);
    }

    @GetMapping
    public ResponseEntity<List<PerfilMotoristaResponseDTO>> buscarTodos() {
        List<PerfilMotoristaResponseDTO> perfis = perfilMotoristaService.buscarTodos();
        return ResponseEntity.ok(perfis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilMotoristaResponseDTO> buscarPorId(@PathVariable Long id) {
        PerfilMotoristaResponseDTO perfil = perfilMotoristaService.buscarPorId(id);
        return ResponseEntity.ok(perfil);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilMotoristaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PerfilMotoristaRequestDTO dto) {
        PerfilMotoristaResponseDTO perfilAtualizado = perfilMotoristaService.atualizar(id, dto);
        return ResponseEntity.ok(perfilAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        perfilMotoristaService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}