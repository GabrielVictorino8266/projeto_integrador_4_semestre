package com.projetoanderson.app.service;

import org.springframework.stereotype.Service;

import com.projetoanderson.app.exception.VeiculoDuplicadoException;
import com.projetoanderson.app.model.dto.VeiculoCreateDTO;
import com.projetoanderson.app.model.dto.VeiculoResponseDto;
import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.repository.VeiculoRepository;

@Service
public class VeiculoService {
    
    private final VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }
    
    public VeiculoResponseDto create(VeiculoCreateDTO dto) {
        if (veiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new VeiculoDuplicadoException(
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
        return new VeiculoResponseDto(veiculoRepository.save(veiculo));
    }
}