package org.openxava.negocio.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.negocio.calculators.DefaultValueCalculatorDomicilio;
import org.openxava.validators.ValidationException;

@Views({
	@View(members="nombre, apellido, numeroDocumento;"
			+ "edad, correoElectronico, codigo;"
			+ "domicilio;"
			+ "Auditoria[fechaCreacion, fechaModificacion; inactivo]"),
	@View(name="simple", members="nombre, apellido, edad, numeroDocumento;")
})


@Entity
public class Cliente extends BasicBusiness{
	
	public String apellido;
	
	public Date fechaModificacion;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DefaultValueCalculator(DefaultValueCalculatorDomicilio.class)
	public Domicilio domicilio;
	
	@Stereotype("EMAIL")
	public String correoElectronico;
	
	public Integer edad;
	
	public String numeroDocumento;
	
	private Boolean inactivo;

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}
	
	@PreUpdate
	public void onUpdate(){
		this.setFechaModificacion(new Date());
	}

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
