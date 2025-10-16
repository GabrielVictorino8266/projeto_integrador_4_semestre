package com.projetoanderson.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projetoanderson.app.dto.PerfilMotoristaRequestDTO;
import com.projetoanderson.app.dto.PerfilMotoristaResponseDTO;
import com.projetoanderson.app.model.entity.PerfilMotorista;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.repository.PerfilMotoristaRepository;
import com.projetoanderson.app.repository.UsuarioRepository;

@Service
public class PerfilMotoristaService {
	
	private final PerfilMotoristaRepository perfilMotoristaRepository;
    private final UsuarioRepository usuarioRepository;

    public PerfilMotoristaService(PerfilMotoristaRepository perfilMotoristaRepository, UsuarioRepository usuarioRepository) {
        this.perfilMotoristaRepository = perfilMotoristaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private PerfilMotoristaResponseDTO toResponseDTO(PerfilMotorista perfil) {
        PerfilMotoristaResponseDTO dto = new PerfilMotoristaResponseDTO();
        dto.setId(perfil.getId());
        dto.setTipoCnh(perfil.getTipoCnh());
        dto.setNumeroCnh(perfil.getNumeroCnh());
        dto.setDesempenho(perfil.getDesempenho());
        dto.setNomeMotorista(perfil.getUsuario().getNome());
        return dto;
    }

    public PerfilMotoristaResponseDTO criar(PerfilMotoristaRequestDTO dto) {
        if (perfilMotoristaRepository.existsById(dto.getUsuarioId())) {
            throw new RuntimeException("Este usuário já possui um perfil de motorista.");
        }
        if (perfilMotoristaRepository.existsByNumeroCnh(dto.getNumeroCnh())) {
            throw new RuntimeException("Este número de CNH já está cadastrado.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário com o ID " + dto.getUsuarioId() + " não encontrado."));

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

    public List<PerfilMotoristaResponseDTO> buscarTodos() {
        return perfilMotoristaRepository.findAll()
            .stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    public PerfilMotoristaResponseDTO buscarPorId(Long id) {
        return perfilMotoristaRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Perfil de motorista com ID " + id + " não encontrado."));
    }

    public PerfilMotoristaResponseDTO atualizar(Long id, PerfilMotoristaRequestDTO dto) {
        PerfilMotorista perfilExistente = perfilMotoristaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil de motorista com ID " + id + " não encontrado."));

        perfilExistente.setTipoCnh(dto.getTipoCnh());
        perfilExistente.setNumeroCnh(dto.getNumeroCnh());
        perfilExistente.setDesempenho(dto.getDesempenho());

        PerfilMotorista perfilAtualizado = perfilMotoristaRepository.save(perfilExistente);
        return toResponseDTO(perfilAtualizado);
    }

    public void deletarPorId(Long id) {
        if (!perfilMotoristaRepository.existsById(id)) {
            throw new RuntimeException("Perfil de motorista com ID " + id + " não encontrado.");
        }
        perfilMotoristaRepository.deleteById(id);
    }

}
