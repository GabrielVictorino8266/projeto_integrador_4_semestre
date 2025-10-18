package com.projetoanderson.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.projetoanderson.app.dto.UsuarioPatchDTO;
import com.projetoanderson.app.dto.UsuarioRequestDTO;
import com.projetoanderson.app.dto.UsuarioResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.UsuarioRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
			EmpresaRepository empresaRepository) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	private Empresa getEmpresaDoUsuarioLogado() {
		UsuarioAuthenticated usuarioAuth = (UsuarioAuthenticated) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Empresa empresa = usuarioAuth.getUsuario().getEmpresa();
		if (empresa == null) {
			throw new IllegalStateException("Usuário autenticado não está associado a nenhuma empresa.");
		}
		return empresa;
	}

	private void validarAcessoUsuario(Usuario usuarioAlvo) {
		Empresa empresaLogada = getEmpresaDoUsuarioLogado();
		if (!usuarioAlvo.getEmpresa().getId().equals(empresaLogada.getId())) {
			throw new AccessDeniedException("Você não tem permissão para acessar usuários de outra empresa.");
		}
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponseDTO> buscarTodos() {
		Long empresaIdLogada = getEmpresaDoUsuarioLogado().getId();
		List<Usuario> usuarios = usuarioRepository.findAllByEmpresaId(empresaIdLogada);
		return usuarios.stream().map(this::converterParaResponseDTO).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarUsuarioId(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado."));
		validarAcessoUsuario(usuario);
		return converterParaResponseDTO(usuario);
	}

	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarUsuarioCpf(String cpf) {
		Usuario usuario = usuarioRepository.findByCpf(cpf) //
				.orElseThrow(() -> new RuntimeException("Usuário com CPF:  " + cpf + " não encontrado."));
		validarAcessoUsuario(usuario);
		return converterParaResponseDTO(usuario);
	}

	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarUsuarioEmail(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email) //
				.orElseThrow(() -> new RuntimeException("Usuário com Email: " + email + " não encontrado."));
		validarAcessoUsuario(usuario);
		return converterParaResponseDTO(usuario);
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponseDTO> buscarUsuarioNome(String nome) {
		Long empresaIdLogada = getEmpresaDoUsuarioLogado().getId();
		List<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCaseAndEmpresaId(nome, empresaIdLogada);
		return usuarios.stream().map(this::converterParaResponseDTO).collect(Collectors.toList());
	}

	@Transactional
	public UsuarioResponseDTO criar(UsuarioRequestDTO usuarioDTO) { //
		Empresa empresaDoAdminLogado = getEmpresaDoUsuarioLogado();
		Long idEmpresaDoAdmin = empresaDoAdminLogado.getId();

		if (usuarioRepository.existsByCpfAndEmpresaId(usuarioDTO.getCpf(), idEmpresaDoAdmin)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Conflito: CPF " + usuarioDTO.getCpf() + " já cadastrado nesta empresa.");
		}
		if (usuarioRepository.existsByEmailAndEmpresaId(usuarioDTO.getEmail(), idEmpresaDoAdmin)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Conflito: Email " + usuarioDTO.getEmail() + " já cadastrado nesta empresa.");
		}

		Usuario novoUsuario = new Usuario(); //
		novoUsuario.setNome(usuarioDTO.getNome());
		novoUsuario.setCpf(usuarioDTO.getCpf());
		novoUsuario.setEmail(usuarioDTO.getEmail());
		novoUsuario.setDataNascimento(usuarioDTO.getDataNascimento());
		novoUsuario.setTelefone(usuarioDTO.getTelefone());
		novoUsuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

		novoUsuario.setEmpresa(empresaDoAdminLogado);
		novoUsuario.setAtivo(true);

		Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

		return converterParaResponseDTO(usuarioSalvo);
	}

	@Transactional
	public void deletarPorId(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário com ID: " + id + " não encontrado."));
		validarAcessoUsuario(usuario);
		usuarioRepository.deleteById(id);
	}

	@Transactional
	public UsuarioResponseDTO atualizarUsuarioParcialmente(Long id, UsuarioPatchDTO dto) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário com ID: " + id + " não encontrado."));
		validarAcessoUsuario(usuario);

		Usuario usuarioAAtualizar = usuarioRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com id " + id + " não encontrado."));

		boolean modificado = false;

		if (dto.getNome() != null && !dto.getNome().equals(usuarioAAtualizar.getNome())) {
			usuarioAAtualizar.setNome(dto.getNome());
			modificado = true;
		}

		if (dto.getEmail() != null && !dto.getEmail().equals(usuarioAAtualizar.getEmail())) {
			usuarioAAtualizar.setEmail(dto.getEmail());
			modificado = true;
		}

		if (StringUtils.hasText(dto.getSenha())) {
			usuarioAAtualizar.setSenha(passwordEncoder.encode(dto.getSenha()));
			modificado = true;
		}

		if (dto.getDataNascimento() != null && !dto.getDataNascimento().equals(usuarioAAtualizar.getDataNascimento())) {
			usuarioAAtualizar.setDataNascimento(dto.getDataNascimento());
			modificado = true;
		}

		if (dto.getTelefone() != null && !dto.getTelefone().equals(usuarioAAtualizar.getTelefone())) {
			usuarioAAtualizar.setTelefone(dto.getTelefone().isEmpty() ? null : dto.getTelefone());
			modificado = true;
		}

		if (modificado) {
			usuarioAAtualizar = usuarioRepository.save(usuarioAAtualizar);
		}

		return converterParaResponseDTO(usuarioAAtualizar);
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