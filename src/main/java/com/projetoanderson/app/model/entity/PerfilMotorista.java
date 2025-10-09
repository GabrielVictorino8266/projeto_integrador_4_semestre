package com.projetoanderson.app.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.projetoanderson.app.model.entity.enums.TipoCNH;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="perfis_motorista")
public class PerfilMotorista {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_cnh", nullable=false, length=2)
	private TipoCNH tipoCnh;
	
	@NotBlank(message = "O número da CNH é obrigatório.")
	@Column(name="numero_cnh", nullable = false, unique = true, length=20)
	private String numeroCnh;
	
	@Min(value = 1, message = "Desempenho deve ser no mínimo 1.")
	@Max(value = 10, message = "Desempenho deve ser no máximo 10.")
	@Column(name="desempenho")
	private Integer desempenho = 10;

	@OneToMany(
		    mappedBy = "perfilMotorista",
		    cascade = CascadeType.ALL,
		    orphanRemoval = true,
		    fetch = FetchType.LAZY
		)
		private List<Incidente> incidentes = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="usuario_id", nullable = false, unique=true)
	private Usuario usuario;
	

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


	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Incidente> getIncidentes() {
		return incidentes;
	}

	public void setIncidentes(List<Incidente> incidentes) {
		this.incidentes = incidentes;
	}

}
