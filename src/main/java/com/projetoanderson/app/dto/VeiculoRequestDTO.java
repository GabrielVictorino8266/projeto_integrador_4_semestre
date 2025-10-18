package com.projetoanderson.app.dto;

import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.validation.ValidEnum;
import jakarta.validation.constraints.*;

public class VeiculoRequestDTO {

	@NotEmpty(message = "O número do veículo é obrigatório.")
	@Size(max = 10)
	private String numeroVeiculo;

	@NotEmpty(message = "A placa é obrigatória.")
	@Size(max = 8)
	@Pattern(regexp = "^[A-Z]{3}[0-9][0-9A-Z][0-9]{2}$", message = "Formato de placa inválido.")
	private String placa;

	@NotEmpty(message = "O tipo do veículo é obrigatório.")
	@ValidEnum(enumClass = TipoVeiculo.class, ignoreCase = true)
	private String tipoVeiculo;

	@NotNull(message = "O ano de fabricação é obrigatório.")
	@Min(1900)
	private Integer anoFabricacao;

	@NotEmpty(message = "A marca é obrigatória.")
	@Size(max = 20)
	private String marca;

	@NotNull(message = "A quilometragem atual é obrigatória.")
	@Min(0)
	private Integer kmAtual;

	@NotNull(message = "O limite de KM para aviso é obrigatório.")
	@Min(0)
	private Integer limiteAvisoKm;

	@ValidEnum(enumClass = StatusVeiculo.class, ignoreCase = true)
	private String status = StatusVeiculo.ATIVO.getValor();

	@NotNull(message = "O ID da empresa é obrigatório.")
	private Long empresaId;

	@AssertTrue(message = "O ano de fabricação não pode ser maior que o ano atual.")
	private boolean isAnoFabricacaoValid() {
		if (anoFabricacao == null) {
			return true; // @NotNull cuidará da validação de nulo
		}
		return anoFabricacao <= java.time.Year.now().getValue();
	}

	// Getters
	public String getNumeroVeiculo() {
		return numeroVeiculo;
	}

	public String getPlaca() {
		return placa;
	}

	public String getTipoVeiculo() {
		return tipoVeiculo;
	}

	public Integer getAnoFabricacao() {
		return anoFabricacao;
	}

	public String getMarca() {
		return marca;
	}

	public Integer getKmAtual() {
		return kmAtual;
	}

	public Integer getLimiteAvisoKm() {
		return limiteAvisoKm;
	}

	public String getStatus() {
		return status;
	}

	public Long getEmpresaId() {
		return empresaId;
	}

	// Setters (importantes para o framework preencher o objeto)
	public void setNumeroVeiculo(String numeroVeiculo) {
		this.numeroVeiculo = numeroVeiculo;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public void setTipoVeiculo(String tipoVeiculo) {
		this.tipoVeiculo = tipoVeiculo;
	}

	public void setAnoFabricacao(Integer anoFabricacao) {
		this.anoFabricacao = anoFabricacao;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public void setKmAtual(Integer kmAtual) {
		this.kmAtual = kmAtual;
	}

	public void setLimiteAvisoKm(Integer limiteAvisoKm) {
		this.limiteAvisoKm = limiteAvisoKm;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}
}