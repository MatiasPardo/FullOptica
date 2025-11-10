package org.openxava.negocio.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VentasPorMes {
	
	@Id
	public String mesyanio;
	
	public BigDecimal subTotal;
	
	public BigDecimal total;
	
	public BigDecimal totalGastos;
	
	public BigDecimal totalMenosGastos;

	public BigDecimal getTotalGastos() {
		return totalGastos;
	}

	public void setTotalGastos(BigDecimal totalGastos) {
		this.totalGastos = totalGastos;
	}

	public BigDecimal getTotalMenosGastos() {
		return totalMenosGastos;
	}

	public void setTotalMenosGastos(BigDecimal totalMenosGastos) {
		this.totalMenosGastos = totalMenosGastos;
	}

	public String getMesyanio() {
		return mesyanio;
	}

	public void setMesyanio(String mesyanio) {
		this.mesyanio = mesyanio;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	

}
