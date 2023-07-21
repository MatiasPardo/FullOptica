package org.openxava.negocio.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.openxava.annotations.View;

@View(members="OjoDerecho[esferico, cilindrico, eje, add;];"
		+ "OjoIzquierdo[esferico2, cilindrico2, eje2, add2;];")

@Embeddable
public class Graduacion{

	@Column(length=8)
	public String esferico;
	
	@Column(length=8)
	public String cilindrico;
	
	@Column(length=8)
	public String eje;
	
	@Column(length=8)
	public String esferico2;
	
	@Column(length=8)
	public String cilindrico2;
	
	@Column(length=8)
	public String eje2;
	
	@Column(length=6)
	public String add;
	
	@Column(length=6)
	public String add2;

	public String getAdd() {
		return add;
	}

	public void setAdd(String add) {
		this.add = add;
	}

	public String getAdd2() {
		return add2;
	}

	public void setAdd2(String add2) {
		this.add2 = add2;
	}

	public String getEsferico() {
		return esferico;
	}

	public void setEsferico(String esferico) {
		this.esferico = esferico;
	}

	public String getCilindrico() {
		return cilindrico;
	}

	public void setCilindrico(String cilindrico) {
		this.cilindrico = cilindrico;
	}

	public String getEje() {
		return eje;
	}

	public void setEje(String eje) {
		this.eje = eje;
	}

	public String getEsferico2() {
		return esferico2;
	}

	public void setEsferico2(String esferico2) {
		this.esferico2 = esferico2;
	}

	public String getCilindrico2() {
		return cilindrico2;
	}

	public void setCilindrico2(String cilindrico2) {
		this.cilindrico2 = cilindrico2;
	}

	public String getEje2() {
		return eje2;
	}

	public void setEje2(String eje2) {
		this.eje2 = eje2;
	}
	
	
}
