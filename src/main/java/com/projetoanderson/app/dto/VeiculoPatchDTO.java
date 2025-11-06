package com.projetoanderson.app.dto;

import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.validation.ValidEnum;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class VeiculoPatchDTO {

	@Size(max = 10, message = "Número do veículo não pode exceder 10 caracteres.")
	private String numeroVeiculo;

	@Size(max = 8, message = "Placa não pode exceder 8 caracteres.")
	@Pattern(regexp = "^[A-Z]{3}[0-9][0-9A-Z][0-9]{2}$", message = "Formato de placa inválido (padrão antigo ou Mercosul).")
	private String placa;

	@ValidEnum(enumClass = TipoVeiculo.class, message = "Tipo de veículo inválido. Valores aceitos: {enumValues}", ignoreCase = true)
	private String tipoVeiculo;

	@Min(value = 1900, message = "Ano de fabricação deve ser no mínimo 1900.")
	private Integer anoFabricacao;

	@Size(max = 20, message = "Marca não pode exceder 20 caracteres.")
	private String marca;

	@Min(value = 0, message = "KM Atual deve ser no mínimo 0.")
	private Integer kmAtual;

	@Min(value = 0, message = "Limite de KM para aviso deve ser no mínimo 0.")
	private Integer limiteAvisoKm;

	@ValidEnum(enumClass = StatusVeiculo.class, message = "Status inválido. Valores aceitos: {enumValues}", ignoreCase = true)
	private String status;

	@AssertTrue(message = "O ano de fabricação não pode ser maior que o ano atual.")
	private boolean isAnoFabricacaoValid() {
		if (anoFabricacao == null) {
			return true;
		}
		return anoFabricacao <= java.time.Year.now().getValue();
	}

	public String getNumeroVeiculo() { return numeroVeiculo; }
	public void setNumeroVeiculo(String numeroVeiculo) { this.numeroVeiculo = numeroVeiculo; }
	public String getPlaca() { return placa; }
	public void setPlaca(String placa) { this.placa = placa; }
	public String getTipoVeiculo() { return tipoVeiculo; }
	public void setTipoVeiculo(String tipoVeiculo) { this.tipoVeiculo = tipoVeiculo; }
	public Integer getAnoFabricacao() { return anoFabricacao; }
	public void setAnoFabricacao(Integer anoFabricacao) { this.anoFabricacao = anoFabricacao; }
	public String getMarca() { return marca; }
	public void setMarca(String marca) { this.marca = marca; }
	public Integer getKmAtual() { return kmAtual; }
	public void setKmAtual(Integer kmAtual) { this.kmAtual = kmAtual; }
	public Integer getLimiteAvisoKm() { return limiteAvisoKm; }
	public void setLimiteAvisoKm(Integer limiteAvisoKm) { this.limiteAvisoKm = limiteAvisoKm; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}