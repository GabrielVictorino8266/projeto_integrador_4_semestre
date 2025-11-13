package com.projetoanderson.app.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditoria {
	
	@Version
	@Column(name = "version")
	private Long version;

	@UuidGenerator(style = UuidGenerator.Style.AUTO)
	@Column(name="uuid", nullable=false, updatable=false, unique=true, columnDefinition = "UUID")
	private UUID uuid;
	
	@CreatedDate
	@Column(name="criado_em", nullable=false, updatable=false)
	private LocalDateTime criadoEm;
	
	@LastModifiedDate
	@Column(name="atualizado_em", nullable=false, updatable=false)
	private LocalDateTime atualizadoEm;
	
	@Column(name="is_ativo", nullable=false)
	private boolean isAtivo;

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(LocalDateTime criadoEm) {
		this.criadoEm = criadoEm;
	}

	public LocalDateTime getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(LocalDateTime atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

	public boolean isAtivo() {
		return isAtivo;
	}

	public void setAtivo(boolean isAtivo) {
		this.isAtivo = isAtivo;
	}
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
