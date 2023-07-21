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

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.View;
import org.openxava.model.Estado;
import org.openxava.negocio.base.MovementTransactional;

@View(members="fecha, empresa;"
		+ "estado, numero;"
		+ "cliente, receta;"
		+ "items;"
		+ "Totales["
		+ "totalSinDescuento, total;"
		+ "senia, saldo]")
@Entity
public class FacturaVenta extends MovementTransactional{

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="apellido, nombre, codigo ")
	private Cliente cliente;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="numeroDeSobre, tipoDeLente.nombre, material.nombre")
	private RecetaMedica receta;
	
	@OneToMany(mappedBy="venta", cascade=CascadeType.ALL)
	@ListProperties("producto.codigo, producto.nombre, cantidad, descuento, precio")	
	private Collection<ItemFacturaVenta> items;
	
	@ReadOnly
	private BigDecimal saldo = BigDecimal.ZERO;
	
	private BigDecimal totalSinDescuento = BigDecimal.ZERO;
	
	private BigDecimal total;
	
	private BigDecimal senia;
	
	@ManyToOne
	@DescriptionsList(descriptionProperties="nombre")
	private MetodoPago metodoDePago;

	
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
            for(ItemFacturaVenta item :this.getItems()){
            	BigDecimal subTotalItem = item.getCantidad().multiply(item.getPrecio());
            	totalSinDescuento = totalSinDescuento.add(subTotalItem);
    			totalItem = totalItem.add(subTotalItem);
            	if(item.getDescuento().compareTo(BigDecimal.ZERO) == 1) 
            		totalItem = totalItem.subtract(subTotalItem.multiply(item.getDescuento().divide(BigDecimal.valueOf(100L),4,RoundingMode.HALF_EVEN)));
            }
        }

        this.setTotal(totalItem);
        this.setTotalSinDescuento(totalSinDescuento);
        this.saldo = this.getTotal().subtract(this.getSenia());

    }

    
	public BigDecimal getTotal() {
		return total == null ? BigDecimal.ZERO : total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getSenia() {
		return senia == null ? BigDecimal.ZERO : senia;
	}

	public void setSenia(BigDecimal seña) {
		this.senia = seña;
	}

	public MetodoPago getMetodoDePago() {
		return metodoDePago;
	}

	public void setMetodoDePago(MetodoPago metodoDePago) {
		this.metodoDePago = metodoDePago;
	}

	public Collection<ItemFacturaVenta> getItems() {
		return items;
	}

	public void setItems(Collection<ItemFacturaVenta> items) {
		this.items = items;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public RecetaMedica getReceta() {
		return receta;
	}

	public void setReceta(RecetaMedica receta) {
		this.receta = receta;
	}
	
	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public BigDecimal getTotalSinDescuento() {
		return totalSinDescuento;
	}

	public void setTotalSinDescuento(BigDecimal totalSinDescuento) {
		this.totalSinDescuento = totalSinDescuento;
	}

}
