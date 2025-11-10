package org.openxava.negocio.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.Required;
import org.openxava.annotations.SearchKey;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import org.openxava.calculators.FalseCalculator;
import org.openxava.negocio.base.BasicBusiness;


@Entity
@View(members="codigo, nombre; empresa, sucursal; tipo, electronico")
@Tab(properties="codigo, nombre, empresa.nombre, sucursal.nombre, tipo, electronico")
public class PuntoVenta extends BasicBusiness {

    @Column(length=10)
    @SearchKey
    @Required
    private String codigo;
    
    @Column(length=100)
    @Required
    private String nombre;
    
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @DescriptionsList(descriptionProperties="nombre")
    @NoCreate @NoModify
    private Empresa empresa;
    
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @DescriptionsList(descriptionProperties="nombre")
    @NoCreate @NoModify
    private Sucursal sucursal;
    
    @DefaultValueCalculator(FalseCalculator.class)
    private Boolean electronico = false;
    
    private TipoPuntoVenta tipo = TipoPuntoVenta.Manual;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

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

    public Boolean getElectronico() {
        return electronico == null ? false : electronico;
    }

    public void setElectronico(Boolean electronico) {
        this.electronico = electronico;
    }
    
    public TipoPuntoVenta getTipo() {
        return tipo == null ? TipoPuntoVenta.Manual : tipo;
    }

    public void setTipo(TipoPuntoVenta tipo) {
        this.tipo = tipo;
    }


}