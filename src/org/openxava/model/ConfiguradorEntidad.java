package org.openxava.model;

import javax.persistence.Column;
import javax.persistence.Id;

import org.openxava.annotations.SearchKey;

public class ConfiguradorEntidad {

	@Id
	@Column(length=50)
	@SearchKey
	private String entidad;
	
	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public static ConfiguradorEntidad buscarConfigurador(String modelName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getOcultarImagenes() {
		// TODO Auto-generated method stub
		return false;
	}

}
