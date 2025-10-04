package com.projetoanderson.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;

@Entity
@Table(name = "veiculos")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 10)
    @Column(name = "numero_veiculo", nullable = false, length = 10)
    private String numeroVeiculo;

    @NotBlank
    @Size(max = 8)
    @Column(name = "placa", nullable = false, length = 8, unique = true)
    private String placa;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_veiculo", nullable = false, length = 20)
    private TipoVeiculo tipoVeiculo;

    @NotNull
    @Column(name = "ano_fabricacao", nullable = false)
    private Integer anoFabricacao;

    @NotBlank
    @Size(max = 20)
    @Column(name = "marca", nullable = false, length = 20)
    private String marca;

    @NotNull
    @Column(name = "km_atual", nullable = false)
    private Integer kmAtual;

    @NotNull
    @Column(name = "limite_aviso_km", nullable = false)
    private Integer limiteAvisoKm;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusVeiculo status = StatusVeiculo.ATIVO;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "criado_por", length = 50, updatable = false)
    private String criadoPor;

    @Column(name = "atualizado_por", length = 50)
    private String atualizadoPor;

    @Column(name = "excluido_por", length = 50)
    private String excluidoPor;

    public Long getId() {
        return id;
    }

    public String getNumeroVeiculo() {
        return numeroVeiculo;
    }

    public void setNumeroVeiculo(String numeroVeiculo) {
        this.numeroVeiculo = numeroVeiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa != null ? placa.toUpperCase() : null;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(Integer anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getKmAtual() {
        return kmAtual;
    }

    public void setKmAtual(Integer kmAtual) {
        this.kmAtual = kmAtual;
    }

    public Integer getLimiteAvisoKm() {
        return limiteAvisoKm;
    }

    public void setLimiteAvisoKm(Integer limiteAvisoKm) {
        this.limiteAvisoKm = limiteAvisoKm;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
   
    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public String getExcluidoPor() {
        return excluidoPor;
    }

    public void setExcluidoPor(String excluidoPor) {
        this.excluidoPor = excluidoPor;
    }
    
}