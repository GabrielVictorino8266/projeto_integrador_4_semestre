package com.projetoanderson.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.projetoanderson.app.exception.PlacaDuplicadaException;
import com.projetoanderson.app.exception.VeiculoNaoEncontradoException;
import com.projetoanderson.app.model.dto.VeiculoCreateDTO;
import com.projetoanderson.app.model.dto.VeiculoFilterDTO;
import com.projetoanderson.app.model.dto.VeiculoResponseDTO;
import com.projetoanderson.app.model.dto.VeiculoUpdateDTO;
import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.repository.VeiculoRepository;
import com.projetoanderson.app.specification.VeiculoSpecification;

@Service
public class VeiculoService {
    
    private final VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    /**
     * Busca um veículo por ID.
     * 
     * @param id ID do veículo a ser buscado.
     * @return Resposta com o veículo encontrado.
     * @throws VeiculoNaoEncontradoException Se o veículo não existir.
     */
    @Transactional(readOnly = true)
    public VeiculoResponseDTO findById(Long id) {
        return new VeiculoResponseDTO(
            veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException(
                    String.format("Veículo com id %d não encontrado.", id)
                )
            )
        );
    }
    
    /**
     * Busca todos os veículos. 
     * 
     * @param filter Filtros para busca.
     * @param pageable Paginação para busca.
     * @return Lista de veículos.
     */
    @Transactional(readOnly = true)
    public Page<VeiculoResponseDTO> findAll(VeiculoFilterDTO filter, Pageable pageable) {
        Specification<Veiculo> spec = VeiculoSpecification.withFilters(filter);
        
        return veiculoRepository.findAll(spec, pageable)
            .map(VeiculoResponseDTO::new);
    }

    /**
     * Cria um veículo.
     * 
     * @param dto Dados do veículo a ser criado.
     * @return Resposta com o veículo criado.
     * @throws PlacaDuplicadaException Se o veículo já existir.
     */
    @Transactional
    public VeiculoResponseDTO create(VeiculoCreateDTO dto) {
        if (veiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new PlacaDuplicadaException(
                String.format("Veículo com placa %s já cadastrado.", dto.getPlaca())
            );
        }
        Veiculo veiculo = new Veiculo();
        veiculo.setNumeroVeiculo(dto.getNumeroVeiculo());
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setTipoVeiculo(TipoVeiculo.fromString(dto.getTipoVeiculo()));
        veiculo.setAnoFabricacao(dto.getAnoFabricacao());
        veiculo.setMarca(dto.getMarca());
        veiculo.setKmAtual(dto.getKmAtual());
        veiculo.setLimiteAvisoKm(dto.getLimiteAvisoKm());
        veiculo.setStatus(StatusVeiculo.fromString(dto.getStatus()));
        return new VeiculoResponseDTO(veiculoRepository.save(veiculo));
    }

    /**
     * Atualiza um veículo.
     * 
     * @param id ID do veículo a ser atualizado.
     * @param dto Dados do veículo a ser atualizado.
     * @return Resposta com o veículo atualizado.
     * @throws VeiculoNaoEncontradoException Se o veículo não existir.
     * @throws PlacaDuplicadaException Se a placa do veículo já existir.
     */
    @Transactional
    public VeiculoResponseDTO update(Long id, VeiculoUpdateDTO dto) {
        Veiculo veiculo = veiculoRepository.findById(id)
            .orElseThrow(() -> new VeiculoNaoEncontradoException(
                String.format("Veículo com id %d não encontrado.", id)
            ));

        if (veiculoRepository.existsByPlacaAndIdNot(dto.getPlaca(), id)) {
            throw new PlacaDuplicadaException(
                String.format("Veículo com placa %s já cadastrado.", dto.getPlaca())
            );
        }
        veiculo.setNumeroVeiculo(dto.getNumeroVeiculo());
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setTipoVeiculo(TipoVeiculo.fromString(dto.getTipoVeiculo()));
        veiculo.setAnoFabricacao(dto.getAnoFabricacao());
        veiculo.setMarca(dto.getMarca());
        veiculo.setKmAtual(dto.getKmAtual());
        veiculo.setLimiteAvisoKm(dto.getLimiteAvisoKm());
        veiculo.setStatus(StatusVeiculo.fromString(dto.getStatus()));
        return new VeiculoResponseDTO(veiculoRepository.save(veiculo));
    }

    /**
     * Deleta um veículo.
     * 
     * @param id ID do veículo a ser deletado.
     * @throws VeiculoNaoEncontradoException Se o veículo não existir.
     */
    @Transactional
    public void delete(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
            .orElseThrow(() -> new VeiculoNaoEncontradoException(
                String.format("Veículo com id %d não encontrado.", id)
            ));
        
        if (veiculo.getStatus() == StatusVeiculo.EXCLUIDO) {
            throw new VeiculoNaoEncontradoException("Veículo já foi excluído.");
        }
        
        veiculo.setStatus(StatusVeiculo.EXCLUIDO);
        veiculo.setExcluidoEm(Instant.now());
        // save() removed - @Transactional handles it
    }
}