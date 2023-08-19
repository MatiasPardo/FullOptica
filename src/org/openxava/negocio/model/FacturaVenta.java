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
import org.openxava.annotations.CloudHiddenTabReference;
import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import org.openxava.model.Estado;
import org.openxava.negocio.base.MovementTransactional;
import org.openxava.negocio.base.actions.SucursalUsuarioFilter;
import org.openxava.negocio.calculators.DefaultValuCalculatorMedioDePago;
import org.openxava.negocio.calculators.DefaultValueCalculatorSucusal;
import org.openxava.validators.ValidationException;

@View(members="fecha, empresa, sucursal, retirado;"
		+ "estado, numero;"
		+ "cliente, receta;"
		+ "medioDePago;"
		+ "items;"
		+ "Totales["
		+ "totalSinDescuento, total;"
		+ "senia, saldo];"
		+ "observaciones")

@Tab(
	    filter=SucursalUsuarioFilter.class,
	    properties="cliente.nombre, fecha, empresa.nombre, numero, estado, total, senia, saldo, totalSinDescuento, usuario, medioDePago.nombre, sucursal.nombre",
	    baseCondition=SucursalUsuarioFilter.BASECONDITION_USUARIO,
	    defaultOrder="${fechaCreacion} desc"
	)
@Entity
public class FacturaVenta extends MovementTransactional{

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="apellido, nombre, numeroDocumento")
	@CloudHiddenTabReference
	private Cliente cliente;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="numeroDeSobre, tipoDeLente.nombre")
	private RecetaMedica receta;
	
	@OneToMany(mappedBy="venta", cascade=CascadeType.ALL)
	@ListProperties("producto.codigo, producto.nombre, cantidad, descuento, precio, subTotal")	
	private Collection<ItemFacturaVenta> items;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DefaultValueCalculator(DefaultValueCalculatorSucusal.class)
	@DescriptionsList(descriptionProperties="nombre, codigo")	
	private Sucursal sucursal;
	
	@ReadOnly
	private BigDecimal saldo = BigDecimal.ZERO;
	
	@ReadOnly
	private BigDecimal totalSinDescuento = BigDecimal.ZERO;
	
	@ReadOnly
	private BigDecimal total;
	
	private BigDecimal senia;
	
	@ReadOnly
	private Boolean retirado;
	
	@Stereotype("MEMO")
	@Length(max=255)
	private String observaciones;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
	@NoCreate @NoModify
	@DefaultValueCalculator(DefaultValuCalculatorMedioDePago.class)
	private MedioDePago medioDePago;

	
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
        if(this.getReceta() != null) {
        	this.getReceta().setCliente(this.getCliente());
        	this.getReceta().setEmpresa(this.getEmpresa());
        }
        
    }
	
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
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

	public MedioDePago getMedioDePago() {
		return medioDePago;
	}

	public void setMedioDePago(MedioDePago medioDePago) {
		this.medioDePago = medioDePago;
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

	public Boolean getRetirado() {
		return retirado;
	}

	public void setRetirado(Boolean retiroCliente) {
		this.retirado = retiroCliente;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	@Override
	public void accionesPreConfirmar() {
		if(this.getItems().isEmpty()){
			throw new ValidationException("Se debe asignar productos a la venta");
		}
		this.setSenia(BigDecimal.ZERO);
		this.setRetirado(Boolean.TRUE);
    	this.getReceta().setEstado(Estado.Abierta);
		this.calcularCampoCalculado();
		
	}

	@Override
	public void accionesPreAnular() {
		// TODO Auto-generated method stub
		
	}

}
