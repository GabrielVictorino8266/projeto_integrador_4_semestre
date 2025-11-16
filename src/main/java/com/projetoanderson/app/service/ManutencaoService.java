package com.projetoanderson.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.projetoanderson.app.dto.ManutencaoPatchDTO;
import com.projetoanderson.app.dto.ManutencaoRequestDTO;
import com.projetoanderson.app.dto.ManutencaoResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.model.entity.Manutencao;
import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.TipoManutencao;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.ManutencaoRepository;
import com.projetoanderson.app.repository.VeiculoRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;
import com.projetoanderson.app.specification.ManutencaoSpecification;

@Service
public class ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;
    private final VeiculoRepository veiculoRepository;
    private final EmpresaRepository empresaRepository;

    public ManutencaoService(ManutencaoRepository manutencaoRepository,
                             VeiculoRepository veiculoRepository,
                             EmpresaRepository empresaRepository) {
        this.manutencaoRepository = manutencaoRepository;
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
             throw new IllegalStateException("Usuário autenticado não está associado a nenhuma empresa.");
        }
        return empresaRepository.findById(empresaDoUsuario.getId())
             .orElseThrow(() -> new IllegalStateException("Empresa do usuário logado não encontrada no banco."));
    }

    private Veiculo validarAcessoVeiculo(Long veiculoId) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo com ID " + veiculoId + " não encontrado."));

        UsuarioAuthenticated usuarioLogado = getUsuarioAutenticado();
        if (temRole(usuarioLogado, Funcao.ROLE_SUPER_ADMIN)) {
            return veiculo; // Super Admin pode acessar
        }

        Empresa empresaDoUsuario = getEmpresaDoUsuarioLogado();
        if (empresaDoUsuario == null || !veiculo.getEmpresa().getId().equals(empresaDoUsuario.getId())) {
            throw new AccessDeniedException("Acesso negado ao veículo com ID: " + veiculoId);
        }
        return veiculo;
    }

    private Manutencao validarAcessoManutencao(Long manutencaoId) {
        Manutencao manutencao = manutencaoRepository.findById(manutencaoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manutenção com ID " + manutencaoId + " não encontrada."));
        
        // Valida o acesso através do veículo vinculado
        validarAcessoVeiculo(manutencao.getVeiculo().getId());
        return manutencao;
    }

    // --- MÉTODOS CRUD ---

    @Transactional(readOnly = true)
    public Page<ManutencaoResponseDTO> buscarTodos(
            Long id,
            LocalDate dataManutencao,
            String descricao,
            Double custo,
            String tipoManutencao,
            Long veiculoId,
            String placaVeiculo,
            Pageable pageable) {
            
        // Create a list to hold all filter specifications
        List<Specification<Manutencao>> specs = new ArrayList<>();

        // Add filters to the list if parameters are provided
        if (id != null) {
            specs.add(ManutencaoSpecification.comId(id));
        }
        if (dataManutencao != null) {
            specs.add(ManutencaoSpecification.comDataManutencao(dataManutencao));
        }
        if (descricao != null && !descricao.isEmpty()) {
            specs.add(ManutencaoSpecification.comDescricaoContendo(descricao));
        }
        if (custo != null) {
            specs.add(ManutencaoSpecification.comCustoIgualA(custo));
        }
        if (tipoManutencao != null && !tipoManutencao.isEmpty()) {
            specs.add(ManutencaoSpecification.comTipoManutencao(tipoManutencao));
        }
        if (veiculoId != null) {
            specs.add(ManutencaoSpecification.comVeiculo(veiculoId));
        }
        if (placaVeiculo != null && !placaVeiculo.isEmpty()) {
            specs.add(ManutencaoSpecification.comPlacaVeiculoContendo(placaVeiculo));
        }

        // Combine all filters with OR
        Specification<Manutencao> combinedSpec = null;
        if (!specs.isEmpty()) {
            combinedSpec = specs.get(0);
            for (int i = 1; i < specs.size(); i++) {
                combinedSpec = combinedSpec.or(specs.get(i));
            }
        }

        // Start with the combined OR conditions (if any)
        Specification<Manutencao> spec = combinedSpec != null ? Specification.where(combinedSpec) : null;

        // Add company filter with AND (for security, always apply company filter)
        if (!temRole(getUsuarioAutenticado(), Funcao.ROLE_SUPER_ADMIN)) {
            Specification<Manutencao> companySpec = ManutencaoSpecification.comEmpresa(getEmpresaDoUsuarioLogado().getId());
            spec = spec != null ? spec.and(companySpec) : companySpec;
        }
        
        return manutencaoRepository.findAll(spec, pageable).map(ManutencaoResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public ManutencaoResponseDTO buscarPorId(Long id) {
        Manutencao manutencao = validarAcessoManutencao(id);
        return new ManutencaoResponseDTO(manutencao);
    }

    @Transactional
    public ManutencaoResponseDTO criar(ManutencaoRequestDTO dto) {
        Veiculo veiculo = validarAcessoVeiculo(dto.getVeiculoId());
        

        Manutencao manutencao = new Manutencao();
        manutencao.setDataManutencao(dto.getDataManutencao());
        manutencao.setDescricao(dto.getDescricao());
        manutencao.setCusto(dto.getCusto());
        manutencao.setTipoManutencao(TipoManutencao.valueOf(dto.getTipoManutencao().toUpperCase()));
        manutencao.setVeiculo(veiculo);
        manutencao.setAtivo(true);

        Manutencao manutencaoSalva = manutencaoRepository.save(manutencao);
        return new ManutencaoResponseDTO(manutencaoSalva);
    }

    @Transactional
    public ManutencaoResponseDTO atualizarParcialmente(Long id, ManutencaoPatchDTO dto) {
        Manutencao manutencao = validarAcessoManutencao(id);
        
        boolean modificado = false;

        if (dto.getDataManutencao() != null && !dto.getDataManutencao().equals(manutencao.getDataManutencao())) {
            manutencao.setDataManutencao(dto.getDataManutencao());
            modificado = true;
        }
        if (StringUtils.hasText(dto.getDescricao()) && !dto.getDescricao().equals(manutencao.getDescricao())) {
            manutencao.setDescricao(dto.getDescricao());
            modificado = true;
        }
        if (dto.getCusto() != null && dto.getCusto().compareTo(manutencao.getCusto()) != 0) {
            manutencao.setCusto(dto.getCusto());
            modificado = true;
        }
        if (StringUtils.hasText(dto.getTipoManutencao())) {
            TipoManutencao novoTipo = TipoManutencao.valueOf(dto.getTipoManutencao().toUpperCase());
            if (novoTipo != manutencao.getTipoManutencao()) {
                manutencao.setTipoManutencao(novoTipo);
                modificado = true;
            }
        }
        
        if (modificado) {
            manutencao = manutencaoRepository.save(manutencao);
        }
        
        return new ManutencaoResponseDTO(manutencao);
    }

    @Transactional
    public void deletarPorId(Long id) {
        validarAcessoManutencao(id);
        manutencaoRepository.deleteById(id);
    }
}