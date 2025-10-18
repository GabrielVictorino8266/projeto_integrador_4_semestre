package com.projetoanderson.app.service; // Ajuste o pacote se necessário

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.projetoanderson.app.dto.UsuarioPatchDTO; // Importar DTO Patch
import com.projetoanderson.app.dto.UsuarioRequestDTO;
import com.projetoanderson.app.dto.UsuarioResponseDTO; // Assumindo que existe
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.repository.EmpresaRepository; // Importar
import com.projetoanderson.app.repository.FuncaoRepository; // Importar
import com.projetoanderson.app.repository.UsuarioRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;
import com.projetoanderson.app.specification.UsuarioSpecification;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final FuncaoRepository funcaoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          EmpresaRepository empresaRepository,
                          FuncaoRepository funcaoRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.funcaoRepository = funcaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UsuarioAuthenticated getUsuarioAutenticado() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioAuthenticated)) {
             throw new IllegalStateException("Usuário não autenticado ou tipo de Principal inesperado.");
         }
         return (UsuarioAuthenticated) authentication.getPrincipal();
    }

    private boolean temRole(UsuarioAuthenticated usuarioAuth, String roleName) {
        return usuarioAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(roleName));
    }

    private Long getIdEmpresaUsuarioLogadoOuNull() {
        UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();
        if (temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
            return null;
        }
        Empresa empresaDoUsuario = usuarioAuth.getUsuario().getEmpresa();
        if (empresaDoUsuario == null) {
             throw new IllegalStateException("Usuário autenticado ("+ usuarioAuth.getUsername() + ") não está associado a nenhuma empresa.");
        }
        return empresaDoUsuario.getId();
    }

    private void validarAcessoUsuario(Long idUsuarioAlvo) {
        UsuarioAuthenticated usuarioLogado = getUsuarioAutenticado();

        if (temRole(usuarioLogado, Funcao.ROLE_SUPER_ADMIN)) {
             if (!usuarioRepository.existsById(idUsuarioAlvo)) {
                  throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com id " + idUsuarioAlvo + " não encontrado.");
             }
             return; // Acesso permitido
        }

        Long empresaIdUsuarioLogado = getIdEmpresaUsuarioLogadoOuNull(); // Garante que o usuário logado tem empresa

        Usuario usuarioAlvo = usuarioRepository.findById(idUsuarioAlvo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com id " + idUsuarioAlvo + " não encontrado."));

        if (!usuarioAlvo.getEmpresa().getId().equals(empresaIdUsuarioLogado)) {
             throw new AccessDeniedException("Acesso negado. Você não pode gerenciar usuários de outra empresa.");
        }
    }


    @Transactional
    public void deletePorId(Long id) {
        validarAcessoUsuario(id);
        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarUsuarioPorIDOptional(Long id) {
         try {
            validarAcessoUsuario(id);
        } catch (AccessDeniedException | ResponseStatusException e) {
            return Optional.empty();
        }
		return usuarioRepository.findById(id)
				.map(this::converterParaResponseDTO);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarUsuarioPorCpfOptional(String cpf) {
        Long idEmpresa = getIdEmpresaUsuarioLogadoOuNull(); // Null se Super Admin

        Optional<Usuario> usuarioOpt;
        if (idEmpresa == null) {
             throw new UnsupportedOperationException("Busca por CPF não suportada para Super Admin. Use filtros ou ID.");
        } else {
            usuarioOpt = usuarioRepository.findByCpf(cpf) // Busca primeiro
                 .filter(u -> u.getEmpresa().getId().equals(idEmpresa)); // Filtra pela empresa
        }

        return usuarioOpt.map(this::converterParaResponseDTO);
    }


    @Transactional(readOnly = true)
	public Page<UsuarioResponseDTO> buscarTodosComFiltro(
            String nome, String email, String cpf, Pageable pageable) {

        Long idEmpresaEspecifica = getIdEmpresaUsuarioLogadoOuNull(); // Null se for Super Admin

        Specification<Usuario> spec = UsuarioSpecification.comFiltros(
            idEmpresaEspecifica, nome, email, cpf
        );

        Page<Usuario> paginaUsuarios = usuarioRepository.findAll(spec, pageable);
        return paginaUsuarios.map(this::converterParaResponseDTO);
	}

    @Transactional
	public UsuarioResponseDTO criarUsuarioParaPropriaEmpresa(UsuarioRequestDTO usuarioDTO) {
		Empresa empresaDoAdminLogado = empresaRepository.findById(getIdEmpresaUsuarioLogadoOuNull())
              .orElseThrow(() -> new IllegalStateException("Empresa do admin logado não encontrada."));
        Long idEmpresaDoAdmin = empresaDoAdminLogado.getId();

		if (usuarioRepository.existsByCpfAndEmpresaId(usuarioDTO.getCpf(), idEmpresaDoAdmin)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado nesta empresa.");
		}
		if (usuarioRepository.existsByEmailAndEmpresaId(usuarioDTO.getEmail(), idEmpresaDoAdmin)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado nesta empresa.");
		}

		Usuario novoUsuario = new Usuario();
		novoUsuario.setNome(usuarioDTO.getNome());
		novoUsuario.setCpf(usuarioDTO.getCpf());
		novoUsuario.setEmail(usuarioDTO.getEmail());
		novoUsuario.setDataNascimento(usuarioDTO.getDataNascimento());
		novoUsuario.setTelefone(usuarioDTO.getTelefone());
		novoUsuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
		novoUsuario.setEmpresa(empresaDoAdminLogado);
		novoUsuario.setAtivo(true);

        Funcao rolePadrao = funcaoRepository.findByNome(Funcao.ROLE_MOTORISTA)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Função padrão não encontrada."));
        Set<Funcao> funcoes = new HashSet<>();
        funcoes.add(rolePadrao);
        novoUsuario.setFuncoes(funcoes);

		Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
		return converterParaResponseDTO(usuarioSalvo);
	}


    @Transactional
    public UsuarioResponseDTO atualizarUsuarioParcialmente(Long id, UsuarioPatchDTO dto) { // Usa PatchDTO
        validarAcessoUsuario(id);

        Usuario usuarioAAtualizar = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com id " + id + " não encontrado (inesperado)."));

        boolean modificado = false;
        Long empresaId = usuarioAAtualizar.getEmpresa().getId();

        if (StringUtils.hasText(dto.getNome()) && !dto.getNome().equals(usuarioAAtualizar.getNome())) {
            usuarioAAtualizar.setNome(dto.getNome());
            modificado = true;
        }

        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equals(usuarioAAtualizar.getEmail())) {
             if (usuarioRepository.existsByEmailAndEmpresaIdAndIdNot(dto.getEmail(), empresaId, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "O email " + dto.getEmail() + " já está em uso nesta empresa.");
             }
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

        if (dto.getTelefone() != null) {
             String telefoneLimpo = StringUtils.hasText(dto.getTelefone()) ? dto.getTelefone() : null;
             if(telefoneLimpo == null && usuarioAAtualizar.getTelefone() != null ||
                telefoneLimpo != null && !telefoneLimpo.equals(usuarioAAtualizar.getTelefone()) ) {
                 usuarioAAtualizar.setTelefone(telefoneLimpo);
                 modificado = true;
             }
        }

        if (modificado) {
            usuarioAAtualizar = usuarioRepository.save(usuarioAAtualizar);
        }
        return converterParaResponseDTO(usuarioAAtualizar);
    }

    private UsuarioResponseDTO converterParaResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setCpf(usuario.getCpf());
        dto.setDataNascimento(usuario.getDataNascimento());
        dto.setTelefone(usuario.getTelefone());
        if (usuario.getEmpresa() != null) {
            dto.setEmpresa(usuario.getEmpresa().getId());
        }
        return dto;
    }
}