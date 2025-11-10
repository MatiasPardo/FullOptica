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

import javax.persistence.Column;

import org.hibernate.validator.constraints.Length;
import org.openxava.annotations.AddAction;
import org.openxava.annotations.CloudHiddenTabReference;
import org.openxava.annotations.OnChange;
import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.RemoveAction;
import org.openxava.annotations.RemoveSelectedAction;
import org.openxava.annotations.SaveAction;
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
		+ "estado, numero, puntoVenta, tipoComprobante;"
		+ "cliente, receta;"
		+ "medioDePago;"
		+ "items;"
		+ "Totales["
		+ "totalSinDescuento, total;"
		+ "senia, saldo, seniaInicial];"
		+ "Facturacion[cae, fechaVencimientoCae, fechaConfirmacion];"
		+ "observaciones")

@Tab(
	    filter=SucursalUsuarioFilter.class,
	    properties="cliente.nombre, fecha, empresa.nombre, numero, puntoVenta.codigo, tipoComprobante, estado, total, senia, saldo, totalSinDescuento, cae, fechaVencimientoCae, fechaConfirmacion, usuario, medioDePago.nombre, sucursal.nombre",
	    baseCondition=SucursalUsuarioFilter.BASECONDITION_FACTURASUCURSAL,
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
	
	@OneToMany(mappedBy="venta", cascade=CascadeType.REMOVE, orphanRemoval=true)
	@ListProperties("producto.codigo, producto.nombre, cantidad, descuento, precio, subTotal")	
	@RemoveAction(value="ItemTransaccion.remove")
	@RemoveSelectedAction(value="ItemTransaccion.removeSelected")
	@AddAction("BasicBusiness.add")
	@SaveAction("ItemTransaccion.save")
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
	
	@ReadOnly
	private BigDecimal seniaInicial;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="codigo, nombre")
	@NoCreate @NoModify
	@OnChange(org.openxava.negocio.base.actions.ValidarPuntoVentaAction.class)
	@DefaultValueCalculator(org.openxava.negocio.calculators.DefaultValueCalculatorPuntoVenta.class)
	private PuntoVenta puntoVenta;
	
	@Column(length=20)
	@ReadOnly
	private String cae;
	
	@ReadOnly
	@Stereotype("DATE")
	private Date fechaVencimientoCae;
	
	@Column(length=10)
	@ReadOnly
	private String tipoComprobante = "C";
	
	@ReadOnly
	@Stereotype("DATETIME")
	private Date fechaConfirmacion;

	
    public BigDecimal getSeniaInicial() {
		return seniaInicial;
	}

	public void setSeniaInicial(BigDecimal seniaInicial) {
		this.seniaInicial = seniaInicial;
	}

	// M�todo para inicializar datos al entrar al m�dulo
    @PostConstruct
    public void init() {
        this.setFecha(new Date());
        this.setEstado(Estado.Borrador);
    }
    
    // M�todo para calcular el valor del campo calculado
    @PostLoad
    public void calcularCampoCalculado() {
        BigDecimal totalItem = BigDecimal.ZERO;
        BigDecimal totalSinDescuento = BigDecimal.ZERO;
        
        if(this.getItems() != null && !this.getItems().isEmpty()){
            for(ItemFacturaVenta item :this.getItems()){
            	item.calcularTotales();
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
        
        // Calcular tipo de comprobante
        this.calcularTipoComprobante();
    }
    
    private void calcularTipoComprobante() {
    	try {
    		// Monotributistas usan tipo C, responsables inscriptos tipo B
    		String tipo = "C"; // Por defecto C para monotributistas
    		
    		if (this.getEmpresa() != null && !this.getEmpresa().esMonotributista()) {
    			tipo = "B"; // Solo si NO es monotributista usar B
    		}
    		
    		this.setTipoComprobante(tipo);
    	} catch (Exception e) {
    		// Si hay error, usar tipo C por defecto (monotributista)
    		this.setTipoComprobante("C");
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

	public void setSenia(BigDecimal sena) {
		this.senia = sena;
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

	public PuntoVenta getPuntoVenta() {
		return puntoVenta;
	}

	public void setPuntoVenta(PuntoVenta puntoVenta) {
		this.puntoVenta = puntoVenta;
	}

	public String getCae() {
		return cae;
	}

	public void setCae(String cae) {
		this.cae = cae;
	}

	public Date getFechaVencimientoCae() {
		return fechaVencimientoCae;
	}

	public void setFechaVencimientoCae(Date fechaVencimientoCae) {
		this.fechaVencimientoCae = fechaVencimientoCae;
	}

	@Override
	public void accionesPreConfirmar() {
		if(this.getItems().isEmpty()){
			throw new ValidationException("Se debe asignar productos a la venta");
		}
		
		this.setSeniaInicial(this.getSenia() != null ? this.getSenia() : BigDecimal.ZERO);
		this.setSenia(BigDecimal.ZERO);
		this.setRetirado(Boolean.TRUE);
		
		if(this.getReceta() != null) {
			this.getReceta().setEstado(Estado.Abierta);
		}
		
		this.calcularCampoCalculado();
		this.calcularTipoComprobante();
	}
	
	@Override
	public void confirmar() {
		System.out.println("[FacturaVenta.confirmar] Iniciando confirmacion de factura");
		System.out.println("[FacturaVenta.confirmar] Numero actual: " + this.getNumero());
		System.out.println("[FacturaVenta.confirmar] Tipo comprobante: " + this.getTipoComprobante());
		System.out.println("[FacturaVenta.confirmar] Empresa ID: " + (this.getEmpresa() != null ? this.getEmpresa().getId() : "null"));
		System.out.println("[FacturaVenta.confirmar] Sucursal ID: " + (this.getSucursal() != null ? this.getSucursal().getId() : "null"));
		System.out.println("[FacturaVenta.confirmar] PuntoVenta ID: " + (this.getPuntoVenta() != null ? this.getPuntoVenta().getId() : "null"));
		
		// Asignar numeracion al confirmar
		if(this.getNumero() == null || this.getNumero().isEmpty()) {
			System.out.println("[FacturaVenta.confirmar] Asignando numeracion...");
			String numeroAsignado = org.openxava.negocio.base.actions.AsignarNumeracionAction.asignarNumero(
				"FacturaVenta", 
				this.getEmpresa().getId(), 
				this.getSucursal() != null ? this.getSucursal().getId() : null,
				this.getPuntoVenta() != null ? this.getPuntoVenta().getId() : null,
				this.getTipoComprobante()
			);
			this.setNumero(numeroAsignado);
			System.out.println("[FacturaVenta.confirmar] Numero asignado: " + numeroAsignado);
		} else {
			System.out.println("[FacturaVenta.confirmar] Ya tiene numero asignado");
		}
		
		// Establecer fecha de confirmacion
		this.setFechaConfirmacion(new Date());
		System.out.println("[FacturaVenta.confirmar] Fecha confirmacion establecida");
		
		// Llamar al metodo padre para cambiar estado
		System.out.println("[FacturaVenta.confirmar] Llamando super.confirmar()...");
		super.confirmar();
		System.out.println("[FacturaVenta.confirmar] Confirmacion completada");
	}
	
	public String getTipoComprobante() {
		return tipoComprobante;
	}
	
	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}
	
	public Date getFechaConfirmacion() {
		return fechaConfirmacion;
	}
	
	public void setFechaConfirmacion(Date fechaConfirmacion) {
		this.fechaConfirmacion = fechaConfirmacion;
	}

	@Override
	public void accionesPreAnular() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void recalculateData() {
		this.calcularCampoCalculado();
	}
}
