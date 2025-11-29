package org.openxava.negocio.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.negocio.calculators.DefaultValueCalculatorDomicilio;
import org.openxava.negocio.calculators.DefaultValueCalculatorPosicionIva;
import org.openxava.validators.ValidationException;

@Views({
	@View(members="nombre, apellido, numeroDocumento;"
			+ "edad, correoElectronico, codigo;"
			+ "cuit;"
			+ "posicionIva;"
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
	
	public String cuit;
	
	private Boolean inactivo;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="codigo, descripcion")
	@DefaultValueCalculator(DefaultValueCalculatorPosicionIva.class)
	@NoCreate @NoModify
	private PosicionIva posicionIva;

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
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

	public PosicionIva getPosicionIva() {
		return posicionIva;
	}

	public void setPosicionIva(PosicionIva posicionIva) {
		this.posicionIva = posicionIva;
	}
	
	@Hidden
	@Transient
	public PosicionIva getPosicionIvaEfectiva() {
		if (this.posicionIva != null) {
			return this.posicionIva;
		}
		// Por defecto consumidor final
		return PosicionIva.consumidorFinal();
	}

	public String getNombreCompleto() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDireccion() {
		// TODO Auto-generated method stub
		return null;
	}
}
