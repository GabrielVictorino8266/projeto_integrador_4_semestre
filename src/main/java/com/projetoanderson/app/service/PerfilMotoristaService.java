package com.projetoanderson.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus; // IMPORTAR
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException; // IMPORTAR

import com.projetoanderson.app.dto.PerfilMotoristaRequestDTO;
import com.projetoanderson.app.dto.PerfilMotoristaResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.model.entity.PerfilMotorista;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.model.entity.enums.TipoPlano;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.PerfilMotoristaRepository;
import com.projetoanderson.app.repository.UsuarioRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;

@Service
public class PerfilMotoristaService {
	
	private final PerfilMotoristaRepository perfilMotoristaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    public PerfilMotoristaService(PerfilMotoristaRepository perfilMotoristaRepository, 
                                  UsuarioRepository usuarioRepository,
                                  EmpresaRepository empresaRepository) {
        this.perfilMotoristaRepository = perfilMotoristaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
    }

    private void validarLimiteDeMotoristas(Empresa empresa) {
        if (empresa.getTipoPlano() == TipoPlano.GRATUITO) {
            long contagemAtual = perfilMotoristaRepository.countByEmpresaId(empresa.getId());
            if (contagemAtual >= 5) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Limite de 5 perfis de motorista atingido para o plano gratuito.");
            }
        }
    }

    private Empresa getEmpresaDoUsuarioLogado() {
        UsuarioAuthenticated usuarioAuth = (UsuarioAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return empresaRepository.findById(usuarioAuth.getUsuario().getEmpresa().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa do usuário não encontrada."));
    }
    
    private void validarAcesso(Long usuarioDoPerfilId) {
         UsuarioAuthenticated usuarioLogado = (UsuarioAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         if (usuarioLogado.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Funcao.ROLE_SUPER_ADMIN))) {
             return;
         }

         Long empresaIdLogada = usuarioLogado.getUsuario().getEmpresa().getId();

         Usuario usuarioDoPerfil = usuarioRepository.findById(usuarioDoPerfilId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com ID " + usuarioDoPerfilId + " não encontrado."));

        if (usuarioDoPerfil.getEmpresa() == null || !usuarioDoPerfil.getEmpresa().getId().equals(empresaIdLogada)) {
            throw new AccessDeniedException("Acesso negado. O perfil de motorista não pertence à sua empresa.");
        }
    }


    private PerfilMotoristaResponseDTO toResponseDTO(PerfilMotorista perfil) {
        PerfilMotoristaResponseDTO dto = new PerfilMotoristaResponseDTO();
        dto.setId(perfil.getId());
        dto.setTipoCnh(perfil.getTipoCnh());
        dto.setNumeroCnh(perfil.getNumeroCnh());
        dto.setDesempenho(perfil.getDesempenho());
        if (perfil.getUsuario() != null) {
           dto.setNomeMotorista(perfil.getUsuario().getNome());
        }
        return dto;
    }

    @Transactional
    public PerfilMotoristaResponseDTO criar(PerfilMotoristaRequestDTO dto) {
        
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário com o ID " + dto.getUsuarioId() + " não encontrado."));

        validarAcesso(dto.getUsuarioId());

        if (perfilMotoristaRepository.existsById(dto.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este usuário já possui um perfil de motorista.");
        }
        if (perfilMotoristaRepository.existsByNumeroCnh(dto.getNumeroCnh())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este número de CNH já está cadastrado.");
        }

        Empresa empresa = empresaRepository.findById(usuario.getEmpresa().getId())
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empresa do usuário não encontrada."));
        
        validarLimiteDeMotoristas(empresa);

        PerfilMotorista novoPerfil = new PerfilMotorista();
        novoPerfil.setTipoCnh(dto.getTipoCnh());
        novoPerfil.setNumeroCnh(dto.getNumeroCnh());
        novoPerfil.setDesempenho(dto.getDesempenho() != null ? dto.getDesempenho() : 10);

        novoPerfil.setUsuario(usuario);
        novoPerfil.setId(usuario.getId());
        novoPerfil.setAtivo(true);

        PerfilMotorista perfilSalvo = perfilMotoristaRepository.save(novoPerfil);
        return toResponseDTO(perfilSalvo);
    }

    @Transactional(readOnly = true)
    public List<PerfilMotoristaResponseDTO> buscarTodos() {
        Long empresaIdLogada = getEmpresaDoUsuarioLogado().getId();
        return perfilMotoristaRepository.findAllByUsuarioEmpresaId(empresaIdLogada)
            .stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PerfilMotoristaResponseDTO buscarPorId(Long id) {
        validarAcesso(id); 
        return perfilMotoristaRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de motorista com ID " + id + " não encontrado."));
    }

    @Transactional
    public PerfilMotoristaResponseDTO atualizar(Long id, PerfilMotoristaRequestDTO dto) {
        validarAcesso(id); 
        
        PerfilMotorista perfilExistente = perfilMotoristaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de motorista com ID " + id + " não encontrado."));

        if (!perfilExistente.getNumeroCnh().equals(dto.getNumeroCnh()) && perfilMotoristaRepository.existsByNumeroCnh(dto.getNumeroCnh())) {
             throw new ResponseStatusException(HttpStatus.CONFLICT, "Este número de CNH já está cadastrado.");
        }

        perfilExistente.setTipoCnh(dto.getTipoCnh());
        perfilExistente.setNumeroCnh(dto.getNumeroCnh());
        perfilExistente.setDesempenho(dto.getDesempenho());

        PerfilMotorista perfilAtualizado = perfilMotoristaRepository.save(perfilExistente);
        return toResponseDTO(perfilAtualizado);
    }

    @Transactional
    public void deletarPorId(Long id) {
        validarAcesso(id); 

        if (!perfilMotoristaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de motorista com ID " + id + " não encontrado.");
        }
        perfilMotoristaRepository.deleteById(id);
    }
}