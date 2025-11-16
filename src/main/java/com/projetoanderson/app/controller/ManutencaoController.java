package com.projetoanderson.app.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.projetoanderson.app.dto.ManutencaoPatchDTO;
import com.projetoanderson.app.dto.ManutencaoRequestDTO;
import com.projetoanderson.app.dto.ManutencaoResponseDTO;
import com.projetoanderson.app.dto.PaginacaoResponseDTO;
import com.projetoanderson.app.model.entity.enums.TipoManutencao;
import com.projetoanderson.app.service.ManutencaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/manutencoes")
public class ManutencaoController {

    private final ManutencaoService manutencaoService;

    public ManutencaoController(ManutencaoService manutencaoService) {
        this.manutencaoService = manutencaoService;
    }

    @GetMapping
    public ResponseEntity<Page<ManutencaoResponseDTO>> buscarTodas(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataManutencao,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Double custo,
            @RequestParam(required = false) String tipoManutencao,
            @RequestParam(required = false) Long veiculoId,
            @RequestParam(required = false) String placaVeiculo,
            @PageableDefault(size = 20, sort = "dataManutencao", direction = Direction.DESC) Pageable pageable
    ) {
        Page<ManutencaoResponseDTO> pagina = manutencaoService.buscarTodos(
                id, dataManutencao, descricao, custo, tipoManutencao, veiculoId, placaVeiculo, pageable
        );
        
        if (pagina.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManutencaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(manutencaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ManutencaoResponseDTO> criar(
            @Valid @RequestBody ManutencaoRequestDTO dto) {
        ManutencaoResponseDTO novaManutencao = manutencaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaManutencao);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ManutencaoResponseDTO> atualizarParcialmente(
            @PathVariable Long id,
            @Valid @RequestBody ManutencaoPatchDTO dto) {
        return ResponseEntity.ok(manutencaoService.atualizarParcialmente(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        manutencaoService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lookups")
    public ResponseEntity<Map<String, List<Map<String, String>>>> listEnums() {
        List<Map<String, String>> tipoList = Arrays.stream(TipoManutencao.values())
                .map(tipo -> Map.of(
                        "label", tipo.name(),
                        "value", tipo.name() 
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(Map.of("tipos", tipoList));
    }
}