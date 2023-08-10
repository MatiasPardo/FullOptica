package org.openxava.negocio.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;

import org.hibernate.validator.constraints.Length;
import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.View;
import org.openxava.model.Estado;
import org.openxava.negocio.base.MovementTransactional;
import org.openxava.negocio.calculators.DefaultValuCalculatorMedioDePago;
import org.openxava.negocio.calculators.DefaultValueCalculatorSucusal;

@View(members="fecha, estado, numero;"
		+ "empresa, sucursal;"
		+ "proveedor;"
		+ "items;"
		+ "medioDePago, total;"
		+ "observaciones")
@Entity
public class FacturaCompra extends MovementTransactional{

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="razonSocial, cuit, nombre")
	private Proveedor proveedor;
	
	@OneToMany(mappedBy="compra", cascade=CascadeType.ALL)
	@ListProperties("producto.codigo, producto.nombre, cantidad, descuento, precio, subTotal")	
	private Collection<ItemFacturaCompra> items;
			
	@ReadOnly
	private BigDecimal total;
	
	@Stereotype("MEMO")
	@Length(max=255)
	private String observaciones;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
	@NoCreate @NoModify
	@DefaultValueCalculator(DefaultValuCalculatorMedioDePago.class)
	private MedioDePago medioDePago;

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre, codigo")
	@DefaultValueCalculator(DefaultValueCalculatorSucusal.class)
	private Sucursal sucursal;
	
    // Método para inicializar datos al entrar al módulo
    @PostConstruct
    public void init() {
        this.setFecha(new Date());
        this.setEstado(Estado.Abierta);   
    }
    
    // Método para calcular el valor del campo calculado
    @PostLoad
    public void calcularCampoCalculado() {
        BigDecimal totalItem = BigDecimal.ZERO;
        BigDecimal totalSinDescuento = BigDecimal.ZERO;
        
        if(this.getItems() != null && !this.getItems().isEmpty()){
            for(ItemFacturaCompra item :this.getItems()){
            	BigDecimal subTotalItem = item.getCantidad().multiply(item.getPrecio());
            	totalSinDescuento = totalSinDescuento.add(subTotalItem);
    			totalItem = totalItem.add(subTotalItem);
            	if(item.getDescuento().compareTo(BigDecimal.ZERO) == 1) 
            		totalItem = totalItem.subtract(subTotalItem.multiply(item.getDescuento().divide(BigDecimal.valueOf(100L),4,RoundingMode.HALF_EVEN)));
            }
        }
        this.setTotal(totalItem);
    }
    
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Collection<ItemFacturaCompra> getItems() {
		return items;
	}

	public void setItems(Collection<ItemFacturaCompra> items) {
		this.items = items;
	}

	public BigDecimal getTotal() {
		return total == null ? BigDecimal.ZERO : total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public MedioDePago getMedioDePago() {
		return medioDePago;
	}

	public void setMedioDePago(MedioDePago medioDePago) {
		this.medioDePago = medioDePago;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

}
