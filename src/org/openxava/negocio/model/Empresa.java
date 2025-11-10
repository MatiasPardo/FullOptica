package org.openxava.negocio.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FlushModeType;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.openxava.annotations.ListProperties;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.View;
import org.openxava.jpa.XPersistence;
import org.openxava.negocio.base.BasicBusiness;

@Entity
@View(members="nombre, codigo; principal; " +
		"DatosFiscales[cuit, razonSocial; condicionIva, monotributista, ingresosBrutos]; " +
		"Domicilio[direccion, ciudad]; " +
		"sucursales")
public class Empresa extends BasicBusiness {
	
	public static Empresa buscarEmpresaPrincipal(){
		String sql = "from Empresa e where " +
				"e.principal= :principal";
		
		Query query = XPersistence.getManager().createQuery(sql);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("principal", Boolean.TRUE);
		Empresa empresa = null;
		try{
			empresa = (Empresa) query.getSingleResult();
		}
		catch(Exception e){
		}
		return empresa;
	}
	
	private Boolean principal;
	
	@Column(length=15)
	private String cuit;
	
	@Column(length=100)
	private String razonSocial;
	
	@Column(length=200)
	private String direccion;
	
	@Column(length=100)
	private String ciudad;
	
	@Column(length=50)
	private String condicionIva;
	
	private Boolean monotributista = false;
	
	@Column(length=20)
	private String ingresosBrutos;
	
	@Column(length=100)
	@Stereotype("EMAIL")
	private String email;
	
	@Column(length=20)
	private String telefono;
	
	@OneToMany(mappedBy="empresa", cascade=CascadeType.ALL)
	@ListProperties("nombre, codigo")	
	private Collection<Sucursal> sucursales;

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}

	public Collection<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(Collection<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getCondicionIva() {
		return condicionIva;
	}

	public void setCondicionIva(String condicionIva) {
		this.condicionIva = condicionIva;
	}

	public Boolean getMonotributista() {
		return monotributista == null ? false : monotributista;
	}

	public void setMonotributista(Boolean monotributista) {
		this.monotributista = monotributista;
	}

	public String getIngresosBrutos() {
		return ingresosBrutos;
	}

	public void setIngresosBrutos(String ingresosBrutos) {
		this.ingresosBrutos = ingresosBrutos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	public boolean esMonotributista() {
		return this.getMonotributista();
	}
}
