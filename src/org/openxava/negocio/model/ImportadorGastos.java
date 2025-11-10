package org.openxava.negocio.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.View;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.negocio.calculators.DefaultValueCalculatorDateNow;

@Entity
@View(members="fechaGasto, montoTotal; codigoItem, codigoProveedor; descripcionItem")
public class ImportadorGastos extends BasicBusiness {

    @DefaultValueCalculator(DefaultValueCalculatorDateNow.class)
    private Date fechaGasto;
    
    private BigDecimal montoTotal;
    
    private String codigoItem;
    
    private String codigoProveedor;
    
    @Stereotype("MEMO")
    private String descripcionItem;

    public Date getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(Date fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getCodigoItem() {
        return codigoItem;
    }

    public void setCodigoItem(String codigoItem) {
        this.codigoItem = codigoItem;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }
}