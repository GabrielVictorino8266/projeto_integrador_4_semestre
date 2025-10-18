// CRIE ESTE NOVO ARQUIVO:
// package com.projetoanderson.app.controller;
package com.projetoanderson.app.controller;

import com.projetoanderson.app.dto.RegistroClienteRequestDTO;
import com.projetoanderson.app.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/register") // Endpoint público
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @PostMapping
    public ResponseEntity<String> registrarNovoCliente(
            @Valid @RequestBody RegistroClienteRequestDTO dto) {

        registroService.registrarNovoCliente(dto);

        // Se chegou aqui sem exceção, o registro foi bem-sucedido
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registro realizado com sucesso! Você já pode fazer login.");
    }
}