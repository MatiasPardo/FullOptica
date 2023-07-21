package org.openxava.negocio.model;

import javax.persistence.Entity;

import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.BasicBusiness;

@Views({
	@View(name="Simple",members="pais"),
	@View(members="fechaCreacion, usuario;"
			+ "codigo, nombre; pais")
})

@Entity
public class Pais extends BasicBusiness{

	private String pais;

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}
	
}