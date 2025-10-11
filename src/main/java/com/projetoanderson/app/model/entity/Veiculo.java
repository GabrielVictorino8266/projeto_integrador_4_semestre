package com.projetoanderson.app.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "veiculos")
public class Veiculo extends Auditoria {

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
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    
    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Manutencao> manutencoes = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		this.placa = placa;
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

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public List<Manutencao> getManutencoes() {
		return manutencoes;
	}

	public void setManutencoes(List<Manutencao> manutencoes) {
		this.manutencoes = manutencoes;
	}
    
}