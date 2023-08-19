package org.openxava.negocio.model;

import javax.persistence.Entity;
import javax.persistence.PreRemove;

import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.validators.ValidationException;
@Views({
	@View(name = "simple", members = "codigo, nombre;")
})
@Entity
public class Producto extends BasicBusiness{
	
	private Boolean inactivo;

	public Boolean getInactivo() {
		return inactivo;
	}

	public void setInactivo(Boolean inactivo) {
		this.inactivo = inactivo;
	}
	
	@PreRemove
	public void preEliminar() {
		throw new ValidationException("No se puede eliminar, en cambio se debe marcar como inactivo");
	}
	
	

}
