package com.projetoanderson.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.projetoanderson.app.model.entity.enums.TipoManutencao;
import com.projetoanderson.app.validation.ValidEnum;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class ManutencaoRequestDTO {

    @NotNull(message = "A data da manutenção é obrigatória.")
    @PastOrPresent(message = "A data da manutenção não pode ser no futuro.")
    private LocalDate dataManutencao;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(min = 3, max = 255, message = "Descrição deve ter entre 3 e 255 caracteres.")
    private String descricao;

    @NotNull(message = "O custo é obrigatório.")
    @DecimalMin(value = "0.01", message = "O custo deve ser maior que zero.")
    private BigDecimal custo;

    @NotBlank(message = "O tipo de manutenção é obrigatório.")
    @ValidEnum(enumClass = TipoManutencao.class, message = "Tipo de manutenção inválido. Valores aceitos: {enumValues}", ignoreCase = true)
    private String tipoManutencao;

    @NotNull(message = "O ID do veículo é obrigatório.")
    private Long veiculoId;

    public LocalDate getDataManutencao() { return dataManutencao; }
    public void setDataManutencao(LocalDate dataManutencao) { this.dataManutencao = dataManutencao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getCusto() { return custo; }
    public void setCusto(BigDecimal custo) { this.custo = custo; }
    public String getTipoManutencao() { return tipoManutencao; }
    public void setTipoManutencao(String tipoManutencao) { this.tipoManutencao = tipoManutencao; }
    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }
}