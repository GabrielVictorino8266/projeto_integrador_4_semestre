package com.projetoanderson.app.service;

import java.util.List;
import java.util.stream.Collectors;

// Imports de Segurança e Transação
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import com.projetoanderson.app.dto.UsuarioRequestDTO;
import com.projetoanderson.app.dto.UsuarioResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.UsuarioRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated; // Import necessário

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository; //
	private final PasswordEncoder passwordEncoder;
	private final EmpresaRepository empresaRepository; //

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmpresaRepository empresaRepository) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.empresaRepository = empresaRepository;
	}

	// --- MÉTODO AUXILIAR DE SEGURANÇA ---
	/**
	 * Obtém a entidade Empresa do usuário atualmente autenticado.
	 */
	private Empresa getEmpresaDoUsuarioLogado() {
		UsuarioAuthenticated usuarioAuth = (UsuarioAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Empresa empresa = usuarioAuth.getUsuario().getEmpresa();
		if (empresa == null) {
			throw new IllegalStateException("Usuário autenticado não está associado a nenhuma empresa.");
		}
		// Para evitar LazyInitializationException, podemos buscar a empresa novamente se necessário
		// return empresaRepository.findById(empresa.getId()).orElseThrow();
		return empresa;
	}

	/**
	 * Valida se o usuário alvo pertence à mesma empresa do usuário logado.
	 */
	private void validarAcessoUsuario(Usuario usuarioAlvo) {
		Empresa empresaLogada = getEmpresaDoUsuarioLogado();
		if (!usuarioAlvo.getEmpresa().getId().equals(empresaLogada.getId())) {
			throw new AccessDeniedException("Você não tem permissão para acessar usuários de outra empresa.");
		}
	}

	// --- MÉTODOS CRUD ATUALIZADOS ---

	/**
	 * Lista APENAS os usuários da mesma empresa do usuário logado.
	 */
	@Transactional(readOnly = true)
	public List<UsuarioResponseDTO> buscarTodos() {
		Long empresaIdLogada = getEmpresaDoUsuarioLogado().getId();
		// Precisamos adicionar um método no repositório para buscar por empresaId
		List<Usuario> usuarios = usuarioRepository.findAllByEmpresaId(empresaIdLogada); // ** NOVO MÉTODO NECESSÁRIO NO REPOSITORY **
		return usuarios.stream().map(this::converterParaResponseDTO).collect(Collectors.toList());
	}

	/**
	 * Busca um usuário pelo ID, mas APENAS se ele for da mesma empresa do usuário logado.
	 */
	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarUsuarioId(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado."));
		validarAcessoUsuario(usuario); // Valida se pertence à empresa correta
		return converterParaResponseDTO(usuario);
	}

	/**
	 * Busca um usuário pelo CPF, mas APENAS se ele for da mesma empresa do usuário logado.
	 */
	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarUsuarioCpf(String cpf) {
		Usuario usuario = usuarioRepository.findByCpf(cpf) //
				.orElseThrow(() -> new RuntimeException("Usuário com CPF:  " + cpf + " não encontrado."));
		validarAcessoUsuario(usuario); // Valida se pertence à empresa correta
		return converterParaResponseDTO(usuario);
	}

	/**
	 * Busca um usuário pelo Email, mas APENAS se ele for da mesma empresa do usuário logado.
	 */
	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarUsuarioEmail(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email) //
				.orElseThrow(() -> new RuntimeException("Usuário com Email: " + email + " não encontrado."));
		validarAcessoUsuario(usuario); // Valida se pertence à empresa correta
		return converterParaResponseDTO(usuario);
	}

	/**
	 * Lista usuários por nome, mas APENAS aqueles da mesma empresa do usuário logado.
	 */
	@Transactional(readOnly = true)
	public List<UsuarioResponseDTO> buscarUsuarioNome(String nome) {
		Long empresaIdLogada = getEmpresaDoUsuarioLogado().getId();
		// Precisamos adicionar um método no repositório para buscar por nome E empresaId
		List<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCaseAndEmpresaId(nome, empresaIdLogada); // ** NOVO MÉTODO NECESSÁRIO NO REPOSITORY **
		return usuarios.stream().map(this::converterParaResponseDTO).collect(Collectors.toList());
	}

	/**
	 * Cria um novo usuário. Garante que o usuário seja criado na empresa correta
	 * e que o usuário logado tenha permissão para criar usuários nessa empresa.
	 */
	@Transactional
	public UsuarioResponseDTO criar(UsuarioRequestDTO usuarioDTO) { //
		Empresa empresaLogada = getEmpresaDoUsuarioLogado();
		Long idEmpresaAlvo = usuarioDTO.getEmpresa();

		// Validação de Segurança: O usuário logado só pode criar usuários na sua própria empresa.
		if (!empresaLogada.getId().equals(idEmpresaAlvo)) {
			throw new AccessDeniedException("Você só pode criar usuários para a sua própria empresa.");
		}

		// Validações de Negócio
		// Precisamos de métodos exists que considerem a empresa também
		if (usuarioRepository.existsByCpfAndEmpresaId(usuarioDTO.getCpf(), idEmpresaAlvo)) { // ** NOVO MÉTODO NECESSÁRIO NO REPOSITORY **
			throw new RuntimeException("CPF já cadastrado nesta empresa.");
		}
		if (usuarioRepository.existsByEmailAndEmpresaId(usuarioDTO.getEmail(), idEmpresaAlvo)) { // ** NOVO MÉTODO NECESSÁRIO NO REPOSITORY **
			throw new RuntimeException("Email já cadastrado nesta empresa.");
		}

		// Busca a entidade Empresa (já validamos o acesso)
		Empresa empresa = empresaRepository.findById(idEmpresaAlvo)
				.orElseThrow(() -> new RuntimeException("Empresa com ID " + idEmpresaAlvo + " não encontrada."));

		Usuario usuarioCriado = new Usuario(); //
		usuarioCriado.setNome(usuarioDTO.getNome());
		usuarioCriado.setCpf(usuarioDTO.getCpf());
		usuarioCriado.setEmail(usuarioDTO.getEmail());
		usuarioCriado.setDataNascimento(usuarioDTO.getDataNascimento());
		usuarioCriado.setTelefone(usuarioDTO.getTelefone());
		usuarioCriado.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
		usuarioCriado.setEmpresa(empresa); // Associa a empresa correta
		usuarioCriado.setAtivo(true);

		Usuario usuarioSalvo = usuarioRepository.save(usuarioCriado);
		return converterParaResponseDTO(usuarioSalvo);
	}

	/**
	 * Deleta um usuário. Só permite deletar usuários da própria empresa.
	 */
	@Transactional
	public void deletarPorId(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário com ID: " + id + " não encontrado."));
		validarAcessoUsuario(usuario); // Valida a permissão antes de deletar
		usuarioRepository.deleteById(id);
	}

	/**
	 * Atualiza um usuário. Só permite atualizar usuários da própria empresa.
	 */
	@Transactional
	public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO usuarioDTO) { //
		Empresa empresaLogada = getEmpresaDoUsuarioLogado();
		Long idEmpresaAlvo = usuarioDTO.getEmpresa();

		// Validação de Segurança: Só pode alterar para a própria empresa
		if (!empresaLogada.getId().equals(idEmpresaAlvo)) {
			throw new AccessDeniedException("Você não pode mover um usuário para outra empresa.");
		}

		// Busca o usuário a ser atualizado
		Usuario usuarioJaExiste = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado."));

		// Validação de Segurança: Verifica se o usuário a ser atualizado pertence à empresa logada
		validarAcessoUsuario(usuarioJaExiste);

		// Validações de Negócio (unicidade de email/cpf dentro da empresa, ignorando o próprio usuário)
		if (!usuarioJaExiste.getCpf().equals(usuarioDTO.getCpf()) && usuarioRepository.existsByCpfAndEmpresaId(usuarioDTO.getCpf(), idEmpresaAlvo)) {
			throw new RuntimeException("O novo CPF já está cadastrado para outro usuário nesta empresa.");
		}
		if (!usuarioJaExiste.getEmail().equals(usuarioDTO.getEmail()) && usuarioRepository.existsByEmailAndEmpresaId(usuarioDTO.getEmail(), idEmpresaAlvo)) {
			throw new RuntimeException("O novo Email já está cadastrado para outro usuário nesta empresa.");
		}


		// Atualiza os dados
		usuarioJaExiste.setNome(usuarioDTO.getNome());
		usuarioJaExiste.setEmail(usuarioDTO.getEmail());
		usuarioJaExiste.setDataNascimento(usuarioDTO.getDataNascimento());
		usuarioJaExiste.setTelefone(usuarioDTO.getTelefone());
		// A empresa não muda neste fluxo, já validamos.

		if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty()) {
			usuarioJaExiste.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
		}

		Usuario usuarioAtualizado = usuarioRepository.save(usuarioJaExiste);
		return converterParaResponseDTO(usuarioAtualizado);
	}

	/**
	 * Converte a entidade Usuario para o DTO de resposta.
	 */
	private UsuarioResponseDTO converterParaResponseDTO(Usuario usuario) { //
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