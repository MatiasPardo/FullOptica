package org.openxava.negocio.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;

import org.openxava.actions.DefaultValueCalculatorDomicilio;
import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.BasicBusiness;

@Views({
	@View(members="codigo, nombre, apellido;"
			+ "edad, correoElectronico;"
			+ "domicilio;"
			+ "Auditoria[fechaCreacion, fechaModificacion]"),
	@View(name="simple", members="nombre, apellido, edad;")
})


@Entity
public class Cliente extends BasicBusiness{
	
	public String apellido;
	
	public Date fechaModificacion;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DefaultValueCalculator(DefaultValueCalculatorDomicilio.class)
	public Domicilio domicilio;
	
	public String correoElectronico;
	
	public Integer edad;

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
	

	
}
