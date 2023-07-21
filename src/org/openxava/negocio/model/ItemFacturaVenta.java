package org.openxava.negocio.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.View;
import org.openxava.negocio.base.ObjectPersistent;

@View(members="venta; producto; cantidad, precio, descuento")

@Entity
public class ItemFacturaVenta extends ObjectPersistent{
	
	@ReadOnly
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private FacturaVenta venta;

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@ReferenceView("simple")
	private Producto producto;
	
	private BigDecimal cantidad = BigDecimal.ONE;

	private BigDecimal descuento;
	
    @Required
	private BigDecimal precio;

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public BigDecimal getCantidad() {
		return cantidad == null? BigDecimal.ONE: cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getDescuento() {
		return descuento == null ? BigDecimal.ZERO:descuento;
	}

	public void setDescuento(BigDecimal descuento) {
		this.descuento = descuento;
	}

	public FacturaVenta getVenta() {
		return venta;
	}

	public void setVenta(FacturaVenta venta) {
		this.venta = venta;
	}

	public BigDecimal getPrecio() {
		return precio == null ? BigDecimal.ZERO : precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}
	
}
