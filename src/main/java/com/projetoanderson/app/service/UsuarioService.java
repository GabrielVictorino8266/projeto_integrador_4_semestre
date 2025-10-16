package com.projetoanderson.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projetoanderson.app.dto.UsuarioRequestDTO;
import com.projetoanderson.app.dto.UsuarioResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.UsuarioRepository;

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmpresaRepository empresaRepository;
	
	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmpresaRepository empresaRepository) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.empresaRepository = empresaRepository;
	}
	
	
	public List<UsuarioResponseDTO> buscarTodos(){
		List<Usuario> usuarios = usuarioRepository.findAll();
		return usuarios.stream().map(this::converterParaResponseDTO).collect(Collectors.toList());
	}
	
	public UsuarioResponseDTO buscarUsuarioId(Long id) {
		return usuarioRepository.findById(id)
				.map(this::converterParaResponseDTO)
				.orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado."));
	}
	
	public UsuarioResponseDTO buscarUsuarioCpf(String cpf) {
		return usuarioRepository.findByCpf(cpf)
				.map(this::converterParaResponseDTO)
				.orElseThrow(() -> new RuntimeException("Usuário com CPF:  " + cpf + " não encontrado."));
	}
	
	public UsuarioResponseDTO buscarUsuarioEmail(String email) {
		return usuarioRepository.findByEmail(email)
				.map(this::converterParaResponseDTO)
				.orElseThrow(() -> new RuntimeException("Usuário com Email: " + email + " não encontrado."));
	}
	
	public List<UsuarioResponseDTO> buscarUsuarioNome(String nome) {
		return usuarioRepository.findByNomeContainingIgnoreCase(nome)
				.stream()
				.map(this::converterParaResponseDTO)
				.collect(Collectors.toList());
	}	
	

	public UsuarioResponseDTO criar(UsuarioRequestDTO usuarioDTO) {
		if(usuarioRepository.existsByCpf(usuarioDTO.getCpf())) {
			throw new RuntimeException("CPF já cadastrado.");
		}
		if(usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
			throw new RuntimeException("Email já cadastrado.");
		}
		Usuario usuarioCriado = new Usuario();
		usuarioCriado.setNome(usuarioDTO.getNome());
		usuarioCriado.setCpf(usuarioDTO.getCpf());
		usuarioCriado.setEmail(usuarioDTO.getEmail());
		usuarioCriado.setDataNascimento(usuarioDTO.getDataNascimento());
		usuarioCriado.setTelefone(usuarioDTO.getTelefone());
        
		usuarioCriado.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        
        Empresa empresa = empresaRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada."));
        usuarioCriado.setEmpresa(empresa);
        usuarioCriado.setAtivo(true);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuarioCriado);
        
		
		return converterParaResponseDTO(usuarioSalvo);
	}
	
	public void deletarPorId(Long id) {
		if(!usuarioRepository.existsById(id)) {
			throw new RuntimeException("Usuário com ID: " + id + " não encontrado.");
		}
		usuarioRepository.deleteById(id);
	}
	
	public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO usuarioDTO) {
		Usuario usuarioJaExiste = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado."));
		usuarioJaExiste.setNome(usuarioDTO.getNome());
		usuarioJaExiste.setEmail(usuarioDTO.getEmail());
		usuarioJaExiste.setDataNascimento(usuarioDTO.getDataNascimento());
		usuarioJaExiste.setTelefone(usuarioDTO.getTelefone());
		
		Long empresaId = usuarioDTO.getEmpresa();
		Empresa empresaUsuario = empresaRepository.findById(empresaId).orElseThrow(() -> new RuntimeException("Empresa com id " + empresaId + " não encontrada."));
		
		usuarioJaExiste.setEmpresa(empresaUsuario);
		
		if(usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty()) {
			usuarioJaExiste.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
		}
		
		Usuario usuarioAtualizado = usuarioRepository.save(usuarioJaExiste);
		
		return converterParaResponseDTO(usuarioAtualizado);
	}
	
	private UsuarioResponseDTO converterParaResponseDTO(Usuario usuario) {
		UsuarioResponseDTO dto = new UsuarioResponseDTO();
		dto.setId(usuario.getId());
		dto.setCpf(usuario.getCpf());
		dto.setDataNascimento(usuario.getDataNascimento());
		dto.setEmail(usuario.getEmail());
		dto.setNome(usuario.getNome());
		dto.setTelefone(usuario.getTelefone());
		dto.setEmpresa(usuario.getEmpresa().getId());
		return dto;
	}
}
