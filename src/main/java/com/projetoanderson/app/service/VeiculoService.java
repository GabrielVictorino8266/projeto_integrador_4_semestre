package com.projetoanderson.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projetoanderson.app.dto.VeiculoRequestDTO;
import com.projetoanderson.app.dto.VeiculoResponseDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.VeiculoRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;
import com.projetoanderson.app.specification.VeiculoSpecification;

// Importe suas exceções customizadas se as tiver criado
// import com.projetoanderson.app.exception.LimiteDePlanoAtingidoException;
// import com.projetoanderson.app.exception.VeiculoNaoEncontradoException;

@Service
public class VeiculoService {
    
    private final VeiculoRepository veiculoRepository;
    private final EmpresaRepository empresaRepository;
    // 1. ADICIONANDO A INJEÇÃO DO USUARIOSERVICE (SE O MÉTODO getEmpresaDoUsuarioLogado ESTIVER NELE)
    // Se o método estiver aqui mesmo, essa injeção não é necessária.

    public VeiculoService(VeiculoRepository veiculoRepository, EmpresaRepository empresaRepository) {
        this.veiculoRepository = veiculoRepository;
        this.empresaRepository = empresaRepository;
    }

    // --- MÉTODOS DE LÓGICA INTERNA E SEGURANÇA ---

    private Empresa getEmpresaDoUsuarioLogado() {
        UsuarioAuthenticated usuarioAuth = (UsuarioAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Cuidado: A relação Usuario -> Empresa é LAZY. Acessar getEmpresa() aqui pode causar LazyInitializationException.
        // Uma abordagem mais segura é ter o ID da empresa no objeto UsuarioAuthenticated ou recarregar o usuário.
        // Por simplicidade, vamos manter assim, mas fique ciente do risco.
        return usuarioAuth.getUsuario().getEmpresa();
    }

    private void validarAcessoEmpresa(Long empresaIdAlvo) {
        Empresa empresaDoUsuario = getEmpresaDoUsuarioLogado(); 
        if (!empresaDoUsuario.getId().equals(empresaIdAlvo)) {
            throw new AccessDeniedException("Acesso negado à empresa com ID: " + empresaIdAlvo);
        }
    }

    private void validarLimiteDeVeiculos(Empresa empresa) {
        long contagemAtual = veiculoRepository.countByEmpresaId(empresa.getId());
        if (contagemAtual >= 5) { // Limite de exemplo
            throw new RuntimeException("409 Conflict: Limite de veículos atingido para o plano atual");
        }
    }

    // --- MÉTODOS CRUD PÚBLICOS ---

    @Transactional(readOnly = true)
    public Page<VeiculoResponseDTO> buscarTodos(Long empresaId, String placa, String tipoVeiculo, String status, String marca, int page, int size) {
        Long idEmpresaFiltro;
        if (empresaId == null) {
            idEmpresaFiltro = getEmpresaDoUsuarioLogado().getId();
        } else {
            validarAcessoEmpresa(empresaId);
            idEmpresaFiltro = empresaId;
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
        Long empresaId = getEmpresaDoUsuarioLogado().getId();
        Veiculo veiculo = veiculoRepository.findByIdAndEmpresaId(id, empresaId)
            .orElseThrow(() -> new RuntimeException("Veículo com id " + id + " não encontrado ou não pertence à sua empresa."));
        
        return new VeiculoResponseDTO(veiculo);
    }
    
    @Transactional
    public VeiculoResponseDTO criar(VeiculoRequestDTO dto) {
        validarAcessoEmpresa(dto.getEmpresaId());
        
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
            .orElseThrow(() -> new RuntimeException("Empresa com ID " + dto.getEmpresaId() + " não encontrada."));
            
        validarLimiteDeVeiculos(empresa);

        if (veiculoRepository.existsByPlacaAndEmpresaId(dto.getPlaca(), dto.getEmpresaId())) {
            throw new RuntimeException("Veículo com placa " + dto.getPlaca() + " já cadastrado nesta empresa.");
        }

        Veiculo veiculo = new Veiculo();
        veiculo.setEmpresa(empresa);
        // Populando a entidade a partir do DTO
        popularVeiculoComDTO(veiculo, dto);
        veiculo.setAtivo(true);
        
        return new VeiculoResponseDTO(veiculoRepository.save(veiculo));
    }

    @Transactional
    public VeiculoResponseDTO atualizar(Long id, VeiculoRequestDTO dto) {
        validarAcessoEmpresa(dto.getEmpresaId());

        Long empresaIdLogada = getEmpresaDoUsuarioLogado().getId();
        Veiculo veiculo = veiculoRepository.findByIdAndEmpresaId(id, empresaIdLogada)
            .orElseThrow(() -> new RuntimeException("Veículo com id " + id + " não encontrado ou não pertence à sua empresa."));

        if (!veiculo.getPlaca().equals(dto.getPlaca()) && veiculoRepository.existsByPlacaAndEmpresaId(dto.getPlaca(), empresaIdLogada)) {
            throw new RuntimeException("A nova placa " + dto.getPlaca() + " já pertence a outro veículo da sua empresa.");
        }

        // Atualizando os campos da entidade com os dados do DTO
        popularVeiculoComDTO(veiculo, dto);
        
        return new VeiculoResponseDTO(veiculoRepository.save(veiculo));
    }

    @Transactional
    public void deletarPorId(Long id) {
        Long empresaId = getEmpresaDoUsuarioLogado().getId();
        if (!veiculoRepository.existsByIdAndEmpresaId(id, empresaId)) { // Método novo no Repository
            throw new RuntimeException("Veículo com id " + id + " não encontrado ou não pertence à sua empresa.");
        }
        
        veiculoRepository.deleteById(id);
    }
    
    /**
     * Método auxiliar para popular ou atualizar uma entidade Veiculo com dados de um DTO.
     * Centraliza a lógica de conversão e evita repetição de código.
     */
    private void popularVeiculoComDTO(Veiculo veiculo, VeiculoRequestDTO dto) {
        veiculo.setNumeroVeiculo(dto.getNumeroVeiculo());
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setAnoFabricacao(dto.getAnoFabricacao());
        veiculo.setMarca(dto.getMarca());
        veiculo.setKmAtual(dto.getKmAtual());
        veiculo.setLimiteAvisoKm(dto.getLimiteAvisoKm());
        // 2. CONVERSÃO DE STRING PARA ENUM
        veiculo.setTipoVeiculo(TipoVeiculo.fromString(dto.getTipoVeiculo()));
        veiculo.setStatus(StatusVeiculo.fromString(dto.getStatus()));
    }
}