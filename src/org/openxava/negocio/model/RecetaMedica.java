package org.openxava.negocio.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.MovementTransactional;

@Views({
		@View(members=""
				+ "cliente; "
				+ "empresa, numeroDeSobre;"
				+ "tipoDeLente, material;"
				+ "distanciaInterPupilar;"
				+ "graduacion;")
})

@Entity
public class RecetaMedica extends MovementTransactional {

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@ReferenceView("simple")
	private Cliente cliente;

	@Embedded
	private Graduacion graduacion;
	
	private String numeroDeSobre;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
	private TipoLente tipoDeLente;
	
	private String distanciaInterPupilar;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
	private MaterialLente material;

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

	public MaterialLente getMaterial() {
		return material;
	}

	public void setMaterial(MaterialLente material) {
		this.material = material;
	}
	
}

