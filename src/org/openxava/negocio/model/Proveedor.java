package org.openxava.negocio.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.negocio.calculators.DefaultValueCalculatorDomicilio;


@Views({
	@View(members="nombre, razonSocial, cuit;"
			+ "correoElectronico, codigo;"
			+ "domicilio;"
			+ "Auditoria[fechaCreacion, fechaModificacion]"),
	@View(name="simple", members="nombre, razonSocial, cuit;")
})


@Entity
public class Proveedor extends BasicBusiness{
	
	public String razonSocial;
	
	public Date fechaModificacion;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DefaultValueCalculator(DefaultValueCalculatorDomicilio.class)
	public Domicilio domicilio;
	
	@Stereotype("EMAIL")
	public String correoElectronico;
		
	public String cuit;
	
	@PreUpdate
	public void onUpdate(){
		this.setFechaModificacion(new Date());
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
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

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	

	
}
