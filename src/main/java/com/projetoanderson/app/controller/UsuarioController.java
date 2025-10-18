package com.projetoanderson.app.controller;

import java.util.List;

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

import com.projetoanderson.app.dto.UsuarioPatchDTO;
import com.projetoanderson.app.dto.UsuarioRequestDTO;
import com.projetoanderson.app.dto.UsuarioResponseDTO;
import com.projetoanderson.app.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	public UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	
	@GetMapping
	public ResponseEntity<List<UsuarioResponseDTO>> buscarTodos(){
		List<UsuarioResponseDTO> usuarios = usuarioService.buscarTodos();
		
		return ResponseEntity.ok(usuarios);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id){
		UsuarioResponseDTO usuarioEncontrado = usuarioService.buscarUsuarioId(id);
		
		return ResponseEntity.ok(usuarioEncontrado);
	}
	
	@GetMapping("/buscar")
	public ResponseEntity<?> buscarPorNomeEmailCpf(
			@RequestParam(required=false) String nome, 
			@RequestParam(required=false) String email,
			@RequestParam(required=false) String cpf){
		
		if(email != null && !email.isEmpty()) {
			UsuarioResponseDTO usuarioEcontrado = usuarioService.buscarUsuarioEmail(email);
			return ResponseEntity.ok(usuarioEcontrado);
		}
		
		if(nome != null && !nome.isEmpty()) {
			List<UsuarioResponseDTO> usuariosEcontrados = usuarioService.buscarUsuarioNome(nome);
			return ResponseEntity.ok(usuariosEcontrados);
		}
		
		if(cpf != null && !cpf.isEmpty()) {
			UsuarioResponseDTO usuarioEcontrado = usuarioService.buscarUsuarioCpf(cpf);
			return ResponseEntity.ok(usuarioEcontrado);
		}
		
		return ResponseEntity.badRequest().body("Forneça o nome, email ou cpf para encontrar um usuário.");
	}
	
	@PostMapping
	public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody @Valid UsuarioRequestDTO usuarioDTO){
		UsuarioResponseDTO usuario = usuarioService.criar(usuarioDTO);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletarPorId(@PathVariable Long id){
		usuarioService.deletarPorId(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioPatchDTO usuarioDTO){
		UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizarUsuarioParcialmente(id, usuarioDTO);
		
		return ResponseEntity.ok(usuarioAtualizado);
	}
	

}
