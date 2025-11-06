package com.projetoanderson.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // IMPORTAR
import org.springframework.web.server.ResponseStatusException;

import com.projetoanderson.app.dto.VeiculoPatchDTO; // IMPORTAR
import com.projetoanderson.app.dto.VeiculoRequestDTO;
import com.projetoanderson.app.dto.VeiculoResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoPlano;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.VeiculoRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;
import com.projetoanderson.app.specification.VeiculoSpecification;

@Service
public class VeiculoService {
    
    private final VeiculoRepository veiculoRepository;
    private final EmpresaRepository empresaRepository;

    public VeiculoService(VeiculoRepository veiculoRepository, EmpresaRepository empresaRepository) {
        this.veiculoRepository = veiculoRepository;
        this.empresaRepository = empresaRepository;
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

    private Empresa getEmpresaDoUsuarioLogado() {
        UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();
        if (temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
            return null;
        }
        Empresa empresaDoUsuario = usuarioAuth.getUsuario().getEmpresa();
        if (empresaDoUsuario == null) {
             throw new IllegalStateException("Usuário autenticado ("+ usuarioAuth.getUsername() + ") não está associado a nenhuma empresa.");
        }
        return empresaRepository.findById(empresaDoUsuario.getId())
             .orElseThrow(() -> new IllegalStateException("Empresa do usuário logado não encontrada no banco."));
    }

    private void validarAcessoEmpresa(Long empresaIdAlvo) {
        UsuarioAuthenticated usuarioLogado = getUsuarioAutenticado();
        
        if (temRole(usuarioLogado, Funcao.ROLE_SUPER_ADMIN)) {
            if (!empresaRepository.existsById(empresaIdAlvo)) {
                 throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa com ID " + empresaIdAlvo + " não encontrada.");
            }
            return;
        }
        
        Empresa empresaDoUsuario = getEmpresaDoUsuarioLogado();
        if (empresaDoUsuario == null || !empresaDoUsuario.getId().equals(empresaIdAlvo)) {
            throw new AccessDeniedException("Acesso negado à empresa com ID: " + empresaIdAlvo);
        }
    }

    private void validarLimiteDeVeiculos(Empresa empresa) {
        if (empresa.getTipoPlano() == TipoPlano.GRATUITO) {
            long contagemAtual = veiculoRepository.countByEmpresaId(empresa.getId());
            if (contagemAtual >= 5) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Limite de 5 veículos atingido para o plano gratuito.");
            }
        }
    }


    @Transactional(readOnly = true)
    public Page<VeiculoResponseDTO> buscarTodos(Long empresaId, String placa, String tipoVeiculo, String status, String marca, int page, int size) {
        
        Long idEmpresaFiltro;
        UsuarioAuthenticated usuarioLogado = getUsuarioAutenticado();

        if (temRole(usuarioLogado, Funcao.ROLE_SUPER_ADMIN)) {
            idEmpresaFiltro = empresaId;
        } else {
            Empresa empresaDoUsuario = getEmpresaDoUsuarioLogado();
            if (empresaId != null && !empresaDoUsuario.getId().equals(empresaId)) {
                 throw new AccessDeniedException("Você só pode visualizar veículos da sua própria empresa.");
            }
            idEmpresaFiltro = empresaDoUsuario.getId();
        }

        Specification<Veiculo> spec = Specification.where(VeiculoSpecification.comEmpresa(idEmpresaFiltro))
                .and(VeiculoSpecification.comPlaca(placa))
                .and(VeiculoSpecification.comTipo(tipoVeiculo))
                .and(VeiculoSpecification.comStatus(status))
                .and(VeiculoSpecification.comMarca(marca));
        
        Pageable pageable = PageRequest.of(page, size);
        return veiculoRepository.findAll(spec, pageable).map(VeiculoResponseDTO::new);
    }
    
    @Transactional(readOnly = true)
    public VeiculoResponseDTO buscarPorId(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo com id " + id + " não encontrado."));
        
        validarAcessoEmpresa(veiculo.getEmpresa().getId());
        
        return new VeiculoResponseDTO(veiculo);
    }
    
    @Transactional
    public VeiculoResponseDTO criar(VeiculoRequestDTO dto) {
        validarAcessoEmpresa(dto.getEmpresaId());
        
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa com ID " + dto.getEmpresaId() + " não encontrada."));
            
        validarLimiteDeVeiculos(empresa);

        if (veiculoRepository.existsByPlaca(dto.getPlaca().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Veículo com placa " + dto.getPlaca() + " já cadastrado no sistema.");
        }

        Veiculo veiculo = new Veiculo();
        veiculo.setEmpresa(empresa);
        popularVeiculoComDTO(veiculo, dto); // Chama o método auxiliar
        veiculo.setAtivo(true);
        
        return new VeiculoResponseDTO(veiculoRepository.save(veiculo));
    }

    @Transactional
    public VeiculoResponseDTO atualizarParcialmente(Long id, VeiculoPatchDTO dto) {
        // 1. Busca o veículo e valida se o usuário pode acessá-lo
        Veiculo veiculo = veiculoRepository.findById(id)
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo com id " + id + " não encontrado."));
        
        validarAcessoEmpresa(veiculo.getEmpresa().getId());
        
        boolean modificado = false;

        
        if (StringUtils.hasText(dto.getPlaca())) {
            String novaPlaca = dto.getPlaca().toUpperCase();
            if (!novaPlaca.equals(veiculo.getPlaca())) {
                if (veiculoRepository.existsByPlacaAndIdNot(novaPlaca, id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "A nova placa " + novaPlaca + " já pertence a outro veículo.");
                }
                veiculo.setPlaca(novaPlaca);
                modificado = true;
            }
        }

        if (StringUtils.hasText(dto.getNumeroVeiculo()) && !dto.getNumeroVeiculo().equals(veiculo.getNumeroVeiculo())) {
            veiculo.setNumeroVeiculo(dto.getNumeroVeiculo());
            modificado = true;
        }
        
        if (StringUtils.hasText(dto.getMarca()) && !dto.getMarca().equals(veiculo.getMarca())) {
            veiculo.setMarca(dto.getMarca());
            modificado = true;
        }

        if (dto.getAnoFabricacao() != null && !dto.getAnoFabricacao().equals(veiculo.getAnoFabricacao())) {
            veiculo.setAnoFabricacao(dto.getAnoFabricacao());
            modificado = true;
        }

        if (dto.getKmAtual() != null && !dto.getKmAtual().equals(veiculo.getKmAtual())) {
            veiculo.setKmAtual(dto.getKmAtual());
            modificado = true;
        }

        if (dto.getLimiteAvisoKm() != null && !dto.getLimiteAvisoKm().equals(veiculo.getLimiteAvisoKm())) {
            veiculo.setLimiteAvisoKm(dto.getLimiteAvisoKm());
            modificado = true;
        }
        
        if (StringUtils.hasText(dto.getTipoVeiculo())) {
            TipoVeiculo novoTipo = TipoVeiculo.fromString(dto.getTipoVeiculo());
            if (novoTipo != veiculo.getTipoVeiculo()) {
                veiculo.setTipoVeiculo(novoTipo);
                modificado = true;
            }
        }
        
        if (StringUtils.hasText(dto.getStatus())) {
            StatusVeiculo novoStatus = StatusVeiculo.fromString(dto.getStatus());
            if (novoStatus != veiculo.getStatus()) {
                veiculo.setStatus(novoStatus);
                modificado = true;
            }
        }

        if (modificado) {
            veiculo = veiculoRepository.save(veiculo);
        }
        
        return new VeiculoResponseDTO(veiculo);
    }


    @Transactional
    public void deletarPorId(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo com id " + id + " não encontrado."));
        
        validarAcessoEmpresa(veiculo.getEmpresa().getId());
        
        veiculoRepository.deleteById(id);
    }
    
    private void popularVeiculoComDTO(Veiculo veiculo, VeiculoRequestDTO dto) {
        veiculo.setNumeroVeiculo(dto.getNumeroVeiculo());
        veiculo.setPlaca(dto.getPlaca().toUpperCase());
        veiculo.setAnoFabricacao(dto.getAnoFabricacao());
        veiculo.setMarca(dto.getMarca());
        veiculo.setKmAtual(dto.getKmAtual());
        veiculo.setLimiteAvisoKm(dto.getLimiteAvisoKm());
        veiculo.setTipoVeiculo(TipoVeiculo.fromString(dto.getTipoVeiculo()));
        veiculo.setStatus(StatusVeiculo.fromString(dto.getStatus()));
    }
}