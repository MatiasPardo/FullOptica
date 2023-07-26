package org.openxava.negocio.model;

import javax.persistence.Entity;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.openxava.jpa.XPersistence;
import org.openxava.negocio.base.BasicBusiness;

@Entity
public class MedioDePago extends BasicBusiness{
	
	@SuppressWarnings("unchecked")
	public static MedioDePago buscarMedioDePagoPrincipal() {
		String sql = "from MedioDePago e where " +
				"e.principal= :principal";
		
		Query query = XPersistence.getManager().createQuery(sql);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("principal", Boolean.TRUE);
		MedioDePago medioPago = null;
		try{
			medioPago = (MedioDePago) query.getResultList().stream().findFirst().orElse(null);
		}
		catch(Exception e){
		}
		return medioPago;
	}
	
	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}

	private Boolean principal;

}
