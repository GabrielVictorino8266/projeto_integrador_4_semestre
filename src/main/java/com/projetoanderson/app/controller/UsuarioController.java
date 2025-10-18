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

import com.projetoanderson.app.dto.UsuarioPatchDTO;
import com.projetoanderson.app.dto.UsuarioRequestDTO;
import com.projetoanderson.app.dto.UsuarioResponseDTO;
import com.projetoanderson.app.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> buscarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cpf,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable){

        Page<UsuarioResponseDTO> pagina = usuarioService.buscarTodosComFiltro(
            nome, email, cpf, pageable);

        if (pagina.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorID(@PathVariable Long id){
        Optional<UsuarioResponseDTO> dtoOptional = usuarioService.buscarUsuarioPorIDOptional(id);

        return dtoOptional
                .map(ResponseEntity::ok) 
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorCPF(@RequestParam String cpf){
         Optional<UsuarioResponseDTO> dtoOptional = usuarioService.buscarUsuarioPorCpfOptional(cpf);
         return dtoOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(
            @Valid @RequestBody UsuarioRequestDTO usuarioDTO) {
        UsuarioResponseDTO usuarioCriado = usuarioService.criarUsuarioParaPropriaEmpresa(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuarioParcialmente(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioPatchDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizarUsuarioParcialmente(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id){
        try {
            usuarioService.deletePorId(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
             if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                 return ResponseEntity.noContent().build();
             }
             throw e;
        }
    }
}