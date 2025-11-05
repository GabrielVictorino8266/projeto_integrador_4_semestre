package com.projetoanderson.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PagamentoPixRequestDTO {

    @NotNull(message = "O valor da transação é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor da transação deve ser positivo.")
    private BigDecimal transactionAmount;

    @NotBlank(message = "A descrição do produto é obrigatória.")
    private String description;

    @NotBlank(message = "O e-mail do pagador é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;

    @NotBlank(message = "O tipo de documento é obrigatório.")
    private String identificationType; // Ex: "CPF"

    @NotBlank(message = "O número do documento é obrigatório.")
    @Pattern(regexp = "^\\d+$", message = "Número do documento deve conter apenas dígitos.")
    private String identificationNumber;

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getIdentificationType() { return identificationType; }
    public void setIdentificationType(String identificationType) { this.identificationType = identificationType; }
    public String getIdentificationNumber() { return identificationNumber; }
    public void setIdentificationNumber(String identificationNumber) { this.identificationNumber = identificationNumber; }
}