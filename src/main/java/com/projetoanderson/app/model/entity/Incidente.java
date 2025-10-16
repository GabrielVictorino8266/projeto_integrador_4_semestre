package com.projetoanderson.app.model.entity;

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

@Entity
@Table(name = "incidentes")
@EntityListeners(AuditingEntityListener.class)
public class Incidente extends Auditoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message="Descrição obrigatória.")
	@Column(name="descricao", nullable=false, columnDefinition = "TEXT")
	private String descricao;
	
	@Enumerated(EnumType.STRING)
	@Column(name="severidade", nullable = false)
	private Severidade severidade;
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_motorista_id", nullable = false)
    private PerfilMotorista perfilMotorista;

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
	
}
