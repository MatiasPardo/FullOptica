package org.openxava.negocio.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.FlushModeType;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.View;
import org.openxava.jpa.XPersistence;
import org.openxava.negocio.base.BasicBusiness;

@View(members="calle, numero,;"
		+ "codigoPostal, principal;"
		+ "provincia")

@Entity
public class Domicilio extends BasicBusiness{
	
	private String calle;
	
	private String numero;
	
	private String codigoPostal;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@ReferenceView("Simple")
	private Provincia provincia;
	
	@SuppressWarnings("unchecked")
	public static Domicilio buscarDomicilioPrincipal() {
		// TODO Auto-generated method stub
		String sql = "from Domicilio e where " +
				"e.principal= :principal";
		
		Query query = XPersistence.getManager().createQuery(sql);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("principal", Boolean.TRUE);
		Domicilio empresa = null;
		try{
			empresa = (Domicilio) query.getResultList().stream().findFirst().orElse(null);
		}
		catch(Exception e){
		}
		return empresa;
	}
	
	private Boolean principal;

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}


}
