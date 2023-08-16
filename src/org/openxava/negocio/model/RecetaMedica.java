package org.openxava.negocio.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.MovementTransactional;

@Views({
	@View(members="empresa;"
			+ "numeroDeSobre, numeroLaboratorio;"
			+ "tipoDeLente, distanciaInterPupilar;"
			+ "graduacion;"),
	@View(name="FacturaVenta",  members=""
			+ "numeroDeSobre, numeroLaboratorio;"
			+ "tipoDeLente, distanciaInterPupilar;"
			+ "graduacion;")
})

@Entity
public class RecetaMedica extends MovementTransactional {

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@ReferenceView("simple")
	private Cliente cliente;

	@Embedded
	private Graduacion graduacion;
	
	@Required
	private String numeroDeSobre;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
	@NoCreate @NoModify
	private TipoLente tipoDeLente;
	
	private String distanciaInterPupilar;
	
	private String numeroLaboratorio;

	public String viewName(org.openxava.view.View view) {
		view.getAllValues();
		return "FacturaVenta";
	}
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Graduacion getGraduacion() {
		return graduacion;
	}

	public void setGraduacion(Graduacion graduacion) {
		this.graduacion = graduacion;
	}

	public String getNumeroDeSobre() {
		return numeroDeSobre;
	}

	public void setNumeroDeSobre(String numeroDeSobre) {
		this.numeroDeSobre = numeroDeSobre;
	}

	public TipoLente getTipoDeLente() {
		return tipoDeLente;
	}

	public void setTipoDeLente(TipoLente tipoDeLente) {
		this.tipoDeLente = tipoDeLente;
	}

	public String getDistanciaInterPupilar() {
		return distanciaInterPupilar;
	}

	public void setDistanciaInterPupilar(String distanciaInterPupilar) {
		this.distanciaInterPupilar = distanciaInterPupilar;
	}
	public String getNumeroLaboratorio() {
		return numeroLaboratorio;
	}
	public void setNumeroLaboratorio(String numeroLaboratorio) {
		this.numeroLaboratorio = numeroLaboratorio;
	}
	

	@Override
	public void accionesPreConfirmar() {
		
	}

	
}

