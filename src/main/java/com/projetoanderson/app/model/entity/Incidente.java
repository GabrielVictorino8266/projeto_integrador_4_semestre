package com.projetoanderson.app.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.projetoanderson.app.model.entity.enums.Severidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="incidentes")
@EntityListeners(AuditingEntityListener.class)
public class Incidente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message="Descrição obrigatória.")
	@Column(name="descricao", nullable=false, columnDefinition = "TEXT")
	private String descricao;
	
	@Enumerated(EnumType.STRING)
	@Column(name="severidade", nullable = false)
	private Severidade severidade;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "perfil_motorista_id", nullable = false)
	private PerfilMotorista perfilMotorista;
	
	@NotNull
	@Column(name = "data_ocorrencia", nullable = false)
	private LocalDateTime dataOcorrencia;
	
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
    
    @LastModifiedDate
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
   
    @CreatedDate
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @CreatedBy
    @Column(name = "criado_por")
    private String criadoPor;
    
    @LastModifiedBy
    @Column(name = "atualizado_por")
    private String atualizadoPor;
    
    @Column(name = "excluido_por")
    private String excluidoPor;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Severidade getSeveridade() {
		return severidade;
	}

	public void setSeveridade(Severidade severidade) {
		this.severidade = severidade;
	}

	public PerfilMotorista getPerfilMotorista() {
		return perfilMotorista;
	}

	public void setPerfilMotorista(PerfilMotorista perfilMotorista) {
		this.perfilMotorista = perfilMotorista;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public LocalDateTime getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(LocalDateTime atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
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

	public LocalDateTime getDataOcorrencia() {
		return dataOcorrencia;
	}

	public void setDataOcorrencia(LocalDateTime dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}
	
}
