package org.openxava.negocio.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Required;
import org.openxava.annotations.SearchKey;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import org.openxava.calculators.IntegerCalculator;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.util.Users;

@Entity
@View(members="nombre; empresa, sucursal, puntoVenta; tipoComprobante; proximoNumero; cantidadDigitos, prefijo")
@Tab(properties="nombre, empresa.nombre, sucursal.nombre, puntoVenta.codigo, tipoComprobante, tipoComprobanteDescripcion, proximoNumero, cantidadDigitos, prefijo")
public class Numerador extends BasicBusiness {

    @Column(length=50)
    @SearchKey
    private String nombre;
    
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @DescriptionsList(descriptionProperties="nombre")
    @NoCreate @NoModify
    private Empresa empresa;
    
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @DescriptionsList(descriptionProperties="nombre")
    @NoCreate @NoModify
    private Sucursal sucursal;
    
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @DescriptionsList(descriptionProperties="codigo, nombre")
    @NoCreate @NoModify
    private PuntoVenta puntoVenta;
    
    @Column(length=10)
    private String tipoComprobante = "C";
    
    // Método para obtener el código AFIP
    public int getCodigoAfip() {
        return TipoComprobanteAfip.porCodigo(this.getTipoComprobante()).getCodigoAfip();
    }
    
    @DefaultValueCalculator(IntegerCalculator.class)
    @Required
    private Long proximoNumero;
    
    @Column(length=50)
    @ReadOnly
    private String modificadoPor;
    
    @ReadOnly
    @Stereotype("DATETIME")
    private Date fechaModificacion;
    
    @DefaultValueCalculator(IntegerCalculator.class)
    private Integer cantidadDigitos = 8;
    
    private String prefijo = "";
    
    @Version
    @Hidden
    private int version;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Long getProximoNumero() {
        return proximoNumero;
    }

    public void setProximoNumero(Long proximoNumero) {
        this.proximoNumero = proximoNumero;
        this.setModificadoPor(Users.getCurrent());
        this.setFechaModificacion(new Date());
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getCantidadDigitos() {
        return cantidadDigitos == null ? 8 : cantidadDigitos;
    }

    public void setCantidadDigitos(Integer cantidadDigitos) {
        this.cantidadDigitos = cantidadDigitos;
    }

    public String getPrefijo() {
        return prefijo == null ? "" : prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getTipoComprobante() {
        return tipoComprobante == null ? "C" : tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getTipoComprobanteDescripcion() {
        return TipoComprobanteAfip.porCodigo(this.getTipoComprobante()).getDescripcion();
    }

    public String obtenerSiguienteNumero() {
        Long numero = this.getProximoNumero();
        this.setProximoNumero(numero + 1);
        return formatearNumero(numero);
    }

    public String formatearNumero(Long numero) {
        String numeroStr = numero.toString();
        String numeroFormateado = "";
        
        // Si tiene punto de venta, usar formato: puntoVenta-numero con 4 dígitos
        if (this.getPuntoVenta() != null) {
            String codigoPV = this.getPuntoVenta().getCodigo();
            numeroFormateado = String.format("%04d", Integer.parseInt(codigoPV)) + "-";
        }
        
        // Agregar prefijo si existe
        numeroFormateado += this.getPrefijo();
        
        // Completar con ceros (descontando la longitud del prefijo)
        int longitudPrefijo = this.getPrefijo().length();
        int ceros = this.getCantidadDigitos() - numeroStr.length() - longitudPrefijo;
        for (int i = 0; i < ceros; i++) {
            numeroFormateado += "0";
        }
        numeroFormateado += numeroStr;
        return numeroFormateado;
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.proximoNumero == null) {
            this.proximoNumero = 1L;
        }
        this.setModificadoPor(Users.getCurrent());
        this.setFechaModificacion(new Date());
    }
}