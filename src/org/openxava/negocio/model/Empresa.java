package org.openxava.negocio.model;

import javax.persistence.Entity;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.openxava.jpa.XPersistence;
import org.openxava.negocio.base.BasicBusiness;

@Entity
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

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}
	
	

}
