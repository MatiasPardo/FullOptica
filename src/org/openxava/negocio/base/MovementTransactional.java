package org.openxava.negocio.base;

import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openxava.actions.DefaultValueCalculatorDateNow;
import org.openxava.actions.DefaultValueCalculatorEmpresa;
import org.openxava.actions.DefaultValueCalculatorState;
import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.model.Estado;
import org.openxava.negocio.model.Empresa;

@MappedSuperclass
public abstract class MovementTransactional extends BasicBusiness {
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
    @DefaultValueCalculator(DefaultValueCalculatorEmpresa.class)
	private Empresa empresa;
	
    @DefaultValueCalculator(DefaultValueCalculatorState.class)
	public Estado estado;
	
	public String numero;
	
    @DefaultValueCalculator(DefaultValueCalculatorDateNow.class)
	public Date fecha;
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void preConfirmar(){
		
	}
	
	public void preGrabar(){
		
	}
	
	public Long asignarNumeracion(String numeracion, Long numero){
		Long prox = super.asignarNumeracion(numeracion, numero);
		this.setNumero(numeracion);
		return prox;
	}
	
	public void numerar(String entidad){
//		Numerador numerador = BuscarNumerador;
		
		
	}
	
	
}
