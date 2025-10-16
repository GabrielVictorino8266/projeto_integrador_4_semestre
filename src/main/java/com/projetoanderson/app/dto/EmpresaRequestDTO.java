package com.projetoanderson.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EmpresaRequestDTO {
	
    @NotBlank(message = "A Razão Social é obrigatória.")
    @Size(max = 255)
    private String razaoSocial;

    @NotBlank(message = "O Nome Fantasia é obrigatório.")
    @Size(max = 255)
    private String nomeFantasia;

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ deve conter 14 dígitos.")
    private String cnpj;
	
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public String getNomeFantasia() {
		return nomeFantasia;
	}
	public String getCnpj() {
		return cnpj;
	}

}
