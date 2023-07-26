package org.openxava.negocio.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.View;
import org.openxava.negocio.base.ObjectPersistent;

@View(members="venta; producto; cantidad, precio, descuento, subTotal")

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
    
    @ReadOnly
    private BigDecimal subTotal;
    
    @PostLoad
    @PreUpdate
    @PrePersist
    public void calcularCampoCalculado() {
        calcularTotales();
        this.getVenta().calcularCampoCalculado();
    }

	public void calcularTotales() {
		BigDecimal totalSinDescuento = this.getCantidad().multiply(this.getPrecio());
        this.setCantidad(BigDecimal.ONE);
        
        if(this.getDescuento().compareTo(BigDecimal.ZERO) == 1){
        	this.setSubTotal(totalSinDescuento.subtract(totalSinDescuento.multiply(this.getDescuento().divide(BigDecimal.valueOf(100L),4,RoundingMode.HALF_EVEN))));
        }else {
        	this.setSubTotal(totalSinDescuento);
        }
	}
    

	public BigDecimal getSubTotal() {
		return subTotal != null ? subTotal : BigDecimal.ZERO ;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

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
		this.cantidad = (cantidad == null) ? BigDecimal.ONE : cantidad;
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
