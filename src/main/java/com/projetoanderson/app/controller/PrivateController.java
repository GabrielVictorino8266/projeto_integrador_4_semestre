package com.projetoanderson.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("private")
public class PrivateController {
    
    @GetMapping
    public String getMessage() {
        return "Ola da autenticacao privada";
    }
    
}
