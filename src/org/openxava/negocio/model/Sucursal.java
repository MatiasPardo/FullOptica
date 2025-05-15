package org.openxava.negocio.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.FlushModeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Query;

import org.openxava.annotations.ListProperties;
import org.openxava.jpa.XPersistence;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.validators.ValidationException;

import com.openxava.naviox.model.User;

@Entity
public class Sucursal extends BasicBusiness {
	
	public static Sucursal obtenerSucrusalHabiitada(String userActual) {
		Sucursal sucursalPrincipal = (Sucursal) BasicBusiness.buscarObjetoPrincipal(Sucursal.class);
		if(sucursalPrincipal == null) throw new ValidationException("Se debe definir una sucursal Principal");
		if(!sucursalPrincipal.usuarioHabilitado(userActual)){
			Sucursal sucHab = Sucursal.buscarPrimerSucursalHabilitada(userActual);
			if(sucHab != null) return sucHab;
			else throw new ValidationException("El usuaro no esta en ninguna sucursal");																					
		}
		
		return sucursalPrincipal;
	}
	
	@SuppressWarnings("unchecked")
	public static Sucursal buscarPrimerSucursalHabilitada(String user) {     
		
		String sql = "from Sucursal e, User u where u member of e.usuarios and u.name = :user";

		Query query = XPersistence.getManager().createQuery(sql);      
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("user", user);
		List<Sucursal> basicBusiness = new LinkedList<Sucursal>();
		try{
			Collection<Object[]> resultList = query.getResultList();
			for(Object[] array: resultList){
				basicBusiness.add((Sucursal)array[0]);
			}
		}
		catch(Exception e){
			throw new ValidationException("Problemas para buscar la sucursal");
		}
		return basicBusiness.stream().filter(Sucursal::getPrincipal)
				.findFirst()
				.orElse(basicBusiness.stream().findFirst()
						.orElse(null));
	}
	
	@SuppressWarnings("unchecked")
	public static List<Sucursal> buscarSucursalesHabilitada(String user) {
	    String sql = "select e from Sucursal e join e.usuarios u where u.name = :user";

	    Query query = XPersistence.getManager()
	        .createQuery(sql, Sucursal.class);
	    query.setFlushMode(FlushModeType.COMMIT);
	    query.setParameter("user", user);

	    return query.getResultList();
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Empresa empresa;

	@ManyToMany(fetch=FetchType.LAZY)
	@ListProperties("name, familyName, givenName, jobTitle, middleName, nickName")
	@OrderBy("name asc")
	private Collection<User> usuarios;
	
	private Boolean principal;

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Collection<User> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Collection<User> usuarios) {
		this.usuarios = usuarios;
	}

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}

	public boolean usuarioHabilitado(String userActual) {
		Boolean habilitado = Boolean.FALSE;
		for(User user : this.getUsuarios()){
			habilitado = user.getName().equals(userActual);
			if(habilitado)	break;
		}
		return habilitado;
	}



}
