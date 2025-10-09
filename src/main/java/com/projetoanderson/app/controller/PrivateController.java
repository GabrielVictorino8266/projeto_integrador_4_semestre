package com.projetoanderson.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para endpoints privados (protegidos por autenticação).
 * 
 * <p>Este controlador contém recursos que requerem autenticação JWT válida.
 * Todos os endpoints aqui são acessíveis apenas para usuários autenticados.</p>
 * 
 * @author Gabriel Victorino
 * @since 2025-04-10
 */
@RestController
@RequestMapping("private")
public class PrivateController {
    
    /**
     * Endpoint de teste para verificar autenticação.
     * 
     * <p>Retorna uma mensagem simples confirmando que o usuário está autenticado
     * e pode acessar recursos privados.</p>
     * 
     * @return msg confirmação
     */
    @GetMapping
    public String getMessage() {
        return "Ola da autenticacao privada";
    }
    
}
