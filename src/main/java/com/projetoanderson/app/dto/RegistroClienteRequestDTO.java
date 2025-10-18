// CRIE ESTE NOVO ARQUIVO:
// package com.projetoanderson.app.dto;
package com.projetoanderson.app.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class RegistroClienteRequestDTO {

    // Dados da Empresa
    @NotBlank(message = "A Razão Social é obrigatória.")
    @Size(max = 255)
    private String razaoSocial;

    @NotBlank(message = "O Nome Fantasia é obrigatório.")
    @Size(max = 255)
    private String nomeFantasia;

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ deve conter 14 dígitos.")
    private String cnpj;

    // Dados do Usuário Admin
    @NotBlank(message = "O Nome do admin é Obrigatório.")
    @Size(min = 3, max = 100)
    private String adminNome;

    @NotBlank(message = "A Senha do admin é Obrigatória.")
    private String adminSenha;

    @NotBlank(message = "CPF do admin é obrigatório.")
    @Pattern(regexp = "^\\d{11}$", message = "CPF do admin deve conter 11 dígitos.")
    private String adminCpf;

    @Email(message = "Formato de e-mail do admin inválido.")
    @NotBlank(message = "Email do admin é obrigatório.")
    private String adminEmail;

    @NotNull(message = "A data de nascimento do admin é obrigatória.")
    @Past(message = "A data de nascimento do admin deve ser no passado.")
    private LocalDate adminDataNascimento;

    private String adminTelefone; // Opcional

    // Getters e Setters para todos os campos...

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getAdminNome() { return adminNome; }
    public void setAdminNome(String adminNome) { this.adminNome = adminNome; }
    public String getAdminSenha() { return adminSenha; }
    public void setAdminSenha(String adminSenha) { this.adminSenha = adminSenha; }
    public String getAdminCpf() { return adminCpf; }
    public void setAdminCpf(String adminCpf) { this.adminCpf = adminCpf; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public LocalDate getAdminDataNascimento() { return adminDataNascimento; }
    public void setAdminDataNascimento(LocalDate adminDataNascimento) { this.adminDataNascimento = adminDataNascimento; }
    public String getAdminTelefone() { return adminTelefone; }
    public void setAdminTelefone(String adminTelefone) { this.adminTelefone = adminTelefone; }
}