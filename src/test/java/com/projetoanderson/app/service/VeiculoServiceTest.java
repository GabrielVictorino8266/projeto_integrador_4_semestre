package com.projetoanderson.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.repository.VeiculoRepository;
import com.projetoanderson.app.model.dto.VeiculoResponseDTO;
import com.projetoanderson.app.model.dto.VeiculoUpdateDTO;
import com.projetoanderson.app.model.dto.VeiculoCreateDTO;
import com.projetoanderson.app.model.dto.VeiculoFilterDTO;
import com.projetoanderson.app.exception.VeiculoNaoEncontradoException;
import com.projetoanderson.app.exception.PlacaDuplicadaException;

class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setTipoVeiculo(TipoVeiculo.CARRO);
        veiculo.setStatus(StatusVeiculo.ATIVO);
        veiculo.setNumeroVeiculo("V001");
        veiculo.setAnoFabricacao(2020);
        veiculo.setMarca("Ford");
        veiculo.setKmAtual(10000);
        veiculo.setLimiteAvisoKm(500);
    }

    // ------------------ findById ------------------
    
    @Test
    @DisplayName("findById: deve retornar veículo quando existir")
    void findById_DeveRetornarVeiculo() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        
        VeiculoResponseDTO result = veiculoService.findById(1L);

        assertEquals("ABC1234", result.licensePlate());
        assertEquals(TipoVeiculo.CARRO, result.vehicleType());
        verify(veiculoRepository).findById(1L);
    }

    @Test
    @DisplayName("findById: deve lançar exceção quando não existir")
    void findById_DeveLancarExcecao() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(VeiculoNaoEncontradoException.class, () -> veiculoService.findById(1L));
        verify(veiculoRepository).findById(1L);
    }

    // ------------------ findAll ------------------
    
    @Test
    @DisplayName("findAll: deve retornar página de veículos")
    @SuppressWarnings("unchecked")
    void findAll_DeveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> page = new PageImpl<>(List.of(veiculo));
        when(veiculoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<VeiculoResponseDTO> result = veiculoService.findAll(new VeiculoFilterDTO(), pageable);
        
        assertEquals(1, result.getContent().size());
        assertEquals("ABC1234", result.getContent().get(0).licensePlate());
        verify(veiculoRepository).findAll(any(Specification.class), eq(pageable));
    }

    // ------------------ create ------------------
    
    @Test
    @DisplayName("create: deve criar veículo com sucesso")
    void create_DeveCriarVeiculo() {
        VeiculoCreateDTO dto = createVeiculoCreateDTO("XYZ9876");
        
        when(veiculoRepository.existsByPlaca("XYZ9876")).thenReturn(false);
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        VeiculoResponseDTO result = veiculoService.create(dto);
        
        assertNotNull(result);
        verify(veiculoRepository).existsByPlaca("XYZ9876");
        verify(veiculoRepository).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("create: deve lançar exceção quando placa duplicada")
    void create_DeveLancarExcecaoPlacaDuplicada() {
        VeiculoCreateDTO dto = createVeiculoCreateDTO("ABC1234");
        
        when(veiculoRepository.existsByPlaca("ABC1234")).thenReturn(true);
        
        assertThrows(PlacaDuplicadaException.class, () -> veiculoService.create(dto));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    // ------------------ update ------------------
    
    @Test
    @DisplayName("update: deve atualizar veículo com sucesso")
    void update_DeveAtualizarVeiculo() {
        VeiculoUpdateDTO dto = createVeiculoUpdateDTO("NEW123");
        
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.existsByPlacaAndIdNot("NEW123", 1L)).thenReturn(false);
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        VeiculoResponseDTO result = veiculoService.update(1L, dto);
        
        assertNotNull(result);
        verify(veiculoRepository).findById(1L);
        verify(veiculoRepository).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("update: deve lançar exceção quando veículo não existe")
    void update_DeveLancarExcecaoVeiculoNaoExiste() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(VeiculoNaoEncontradoException.class, 
            () -> veiculoService.update(1L, createVeiculoUpdateDTO("NEW123")));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("update: deve lançar exceção quando placa duplicada")
    void update_DeveLancarExcecaoPlacaDuplicada() {
        VeiculoUpdateDTO dto = createVeiculoUpdateDTO("NEW123");
        
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.existsByPlacaAndIdNot("NEW123", 1L)).thenReturn(true);
        
        assertThrows(PlacaDuplicadaException.class, () -> veiculoService.update(1L, dto));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("update: deve permitir atualização com mesma placa")
    void update_DevePermitirMesmaPlaca() {
        VeiculoUpdateDTO dto = createVeiculoUpdateDTO("ABC1234");
        
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.existsByPlacaAndIdNot("ABC1234", 1L)).thenReturn(false);
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        veiculoService.update(1L, dto);
        
        verify(veiculoRepository).save(any(Veiculo.class));
    }

    // ------------------ delete ------------------
    
    @Test
    @DisplayName("delete: deve marcar como excluído")
    void delete_DeveMarcarComoExcluido() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        
        veiculoService.delete(1L);

        assertEquals(StatusVeiculo.EXCLUIDO, veiculo.getStatus());
        assertNotNull(veiculo.getExcluidoEm());
    }

    @Test
    @DisplayName("delete: deve lançar exceção quando veículo não existe")
    void delete_DeveLancarExcecaoVeiculoNaoExiste() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(VeiculoNaoEncontradoException.class, () -> veiculoService.delete(1L));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    @DisplayName("delete: deve lançar exceção quando já excluído")
    void delete_DeveLancarExcecaoJaExcluido() {
        veiculo.setStatus(StatusVeiculo.EXCLUIDO);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        
        assertThrows(VeiculoNaoEncontradoException.class, () -> veiculoService.delete(1L));
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }


    // ------------------ helpers ------------------
     
    private VeiculoCreateDTO createVeiculoCreateDTO(String placa) {
        VeiculoCreateDTO dto = new VeiculoCreateDTO();
        dto.setPlaca(placa);
        dto.setTipoVeiculo("CARRO");
        dto.setStatus("ATIVO");
        dto.setNumeroVeiculo("V001");
        dto.setAnoFabricacao(2020);
        dto.setMarca("Ford");
        dto.setKmAtual(10000);
        dto.setLimiteAvisoKm(500);
        return dto;
    }

    private VeiculoUpdateDTO createVeiculoUpdateDTO(String placa) {
        VeiculoUpdateDTO dto = new VeiculoUpdateDTO();
        dto.setPlaca(placa);
        dto.setTipoVeiculo("CARRO");
        dto.setStatus("ATIVO");
        dto.setNumeroVeiculo("V001");
        dto.setAnoFabricacao(2020);
        dto.setMarca("Ford");
        dto.setKmAtual(10000);
        dto.setLimiteAvisoKm(500);
        return dto;
    }
}