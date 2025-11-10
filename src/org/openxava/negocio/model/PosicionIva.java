package org.openxava.negocio.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import org.openxava.negocio.base.BasicBusiness;

@Entity
@View(members="codigo, nombre, descripcion")
@Tab(properties="codigo, nombre, descripcion")
public class PosicionIva extends BasicBusiness {

    @Column(length=10)
    private String codigo;
    
    @Column(length=100)
    private String descripcion;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public static final String CONSUMIDOR_FINAL = "CF";
    public static final String RESPONSABLE_INSCRIPTO = "RI";
    public static final String MONOTRIBUTISTA = "M";
    public static final String EXENTO = "E";
    
    public boolean esResponsableInscripto() {
        return this.codigo != null && this.codigo.equals(RESPONSABLE_INSCRIPTO);
    }
    
    public boolean esMonotributista() {
        return this.codigo != null && this.codigo.equals(MONOTRIBUTISTA);
    }
    
    public static PosicionIva consumidorFinal() {
        PosicionIva cf = new PosicionIva();
        cf.setCodigo(CONSUMIDOR_FINAL);
        cf.setNombre("Consumidor Final");
        cf.setDescripcion("Consumidor Final");
        return cf;
    }
}