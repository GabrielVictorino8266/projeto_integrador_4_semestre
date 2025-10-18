package com.projetoanderson.app.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EmpresaPatchDTO {

    @Size(max = 255, message = "Razão Social não pode exceder 255 caracteres.")
    private String razaoSocial;

    @Size(max = 255, message = "Nome Fantasia não pode exceder 255 caracteres.")
    private String nomeFantasia;

    @Pattern(regexp = "^\\d{14}$", message = "CNPJ deve conter 14 dígitos, se informado.")
    private String cnpj;

	// Getters e Setters
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getNomeFantasia() {
		return nomeFantasia;
	}
	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
}