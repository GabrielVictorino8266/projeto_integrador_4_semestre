package com.projetoanderson.app.dto;

public class UsuarioAtualContainerDTO {

	private UsuarioAtualResponseDTO user;

	public UsuarioAtualContainerDTO(UsuarioAtualResponseDTO user) {
		this.user = user;
	}

	public UsuarioAtualResponseDTO getUser() {
		return user;
	}

	public void setUser(UsuarioAtualResponseDTO user) {
		this.user = user;
	}
}