package org.openxava.negocio.base;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FlushModeType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Query;

import org.openxava.annotations.DisplaySize;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Stereotype;
import org.openxava.jpa.XPersistence;
import org.openxava.model.ConfiguradorEntidad;
import org.openxava.util.Users;
import org.openxava.validators.ValidationException;
import org.openxava.view.View;

@MappedSuperclass
public class BasicBusiness extends ObjectPersistent{
	
	public static <T> BasicBusiness buscarObjetoPrincipal(Class<T> clase){
		String sql = "from " + clase.getSimpleName() + " e where e.principal= :principal";
		
		Query query = XPersistence.getManager().createQuery(sql);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("principal", Boolean.TRUE);
		BasicBusiness basicBusiness = null;
		try{
			basicBusiness = (BasicBusiness) query.getSingleResult();
		}
		catch(Exception e){
		}
		return basicBusiness;
	}

	@Column(length=50)
	@ReadOnly
	@DisplaySize(30)
	private String usuario;
	
	@ReadOnly
	@Stereotype("DATETIME")
	private Date fechaCreacion;
	
	private String codigo;
	
	private String nombre;

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

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	public Long asignarNumeracion(String numeracion, Long numero){
		return numero + 1;
	}
	
	@PrePersist
	protected void onPrePersist() {
		this.setUsuario(Users.getCurrent());
		this.setFechaCreacion(new java.util.Date());
	}
	
	@PreUpdate
	protected void onPreUpdate() {
	}

	public void propiedadesOcultas(List<String> propiedadesOcultas, List<String> propiedadesVisibles) {
		// TODO Auto-generated method stub
		
	}

	public String viewName(View view) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean readOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	public void propertiesReadOnlyOnEdit(View view, ConfiguradorEntidad configuracion) {
		// TODO Auto-generated method stub
		
	}
	
	@PreRemove
	public void preEliminar() {
		throw new ValidationException("No se puede eliminar, en cambio se debe marcar como inactivo");
	}
	
	
}
