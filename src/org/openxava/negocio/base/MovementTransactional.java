package org.openxava.negocio.base;

import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreRemove;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ReadOnly;
import org.openxava.model.Estado;
import org.openxava.negocio.calculators.DefaultValueCalculatorDateNow;
import org.openxava.negocio.calculators.DefaultValueCalculatorEmpresa;
import org.openxava.negocio.calculators.DefaultValueCalculatorState;
import org.openxava.negocio.model.Empresa;
import org.openxava.validators.ValidationException;

@MappedSuperclass
public abstract class MovementTransactional extends BasicBusiness {
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
    @DefaultValueCalculator(DefaultValueCalculatorEmpresa.class)
	@ReadOnly
	private Empresa empresa;
	
    @DefaultValueCalculator(DefaultValueCalculatorState.class)
    @ReadOnly
	public Estado estado;
	
    @ReadOnly
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
		return estado != null ? estado : Estado.Borrador;
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
	
	abstract public void accionesPreConfirmar();

	public void confirmar() {
		if(this.getEstado().equals(Estado.Borrador)) {
			this.setEstado(Estado.Confirmada);
		}
		
	}
	
	@PreRemove
	public void preEliminar() {
		throw new ValidationException("No se puede eliminar, en cambio se debe anular");
	}

	abstract public void accionesPreAnular();

	public void anular() {
		if(this.getEstado().equals(Estado.Confirmada)) {
			this.setEstado(Estado.Anulada);
		}else if(this.getEstado().equals(Estado.Abierta)) {
			this.setEstado(Estado.Cancelada);
		}
	} 
	
	public boolean readOnly() {
		if(this.getEstado().equals(Estado.Confirmada)){
			return true;
		}else return false;
	}
	
}
