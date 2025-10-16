package com.projetoanderson.app.dto;

import com.projetoanderson.app.model.entity.enums.TipoCNH;

public class PerfilMotoristaResponseDTO {

	private Long id;
    private TipoCNH tipoCnh;
    private String numeroCnh;
    private Integer desempenho;
    private String nomeMotorista;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCNH getTipoCnh() {
        return tipoCnh;
    }

    public void setTipoCnh(TipoCNH tipoCnh) {
        this.tipoCnh = tipoCnh;
    }

    public String getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(String numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public Integer getDesempenho() {
        return desempenho;
    }

    public void setDesempenho(Integer desempenho) {
        this.desempenho = desempenho;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    public void setNomeMotorista(String nomeMotorista) {
        this.nomeMotorista = nomeMotorista;
    }
	
}
