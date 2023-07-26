package org.openxava.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.Required;
import org.openxava.annotations.SearchKey;
import org.openxava.negocio.calculators.DefaultValueCalculatorEmpresa;
import org.openxava.negocio.model.Empresa;

@Entity
public class ConfiguradorEntidad {

	@Id
	@Column(length=50) 
	@SearchKey
	private String entidad;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
	@NoCreate @NoModify
	@Required
	@DefaultValueCalculator(DefaultValueCalculatorEmpresa.class)
	private Empresa empresa;
	
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

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
