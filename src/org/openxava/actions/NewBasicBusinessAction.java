package org.openxava.actions;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openxava.application.meta.MetaApplications;
import org.openxava.application.meta.MetaModule;
import org.openxava.model.ConfiguradorEntidad;
import org.openxava.model.meta.MetaMember;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.util.ElementNotFoundException;
import org.openxava.util.Users;

import com.openxava.naviox.model.User;

public class NewBasicBusinessAction extends NewAction{
	
	private ConfiguradorEntidad configuracion = null;
	
	private boolean configuracionBuscada = false;
	
	protected ConfiguradorEntidad getConfiguradorEntidad(){
		if (!configuracionBuscada){
			configuracion = ConfiguradorEntidad.buscarConfigurador(getView().getModelName());
			configuracionBuscada = true;
		}
		return configuracion;
	}
	
	@Override
	public void execute() throws Exception {
		getView().setViewName(this.getNombreVistaAlCrear());
		
		super.execute();
		
		ConfiguradorEntidad entidad = this.getConfiguradorEntidad();
		List<String> propiedadesSoloLectura = new LinkedList<String>();
    	List<String> propiedadesEditables = new LinkedList<String>();
    	this.evaluarPropiedades(propiedadesSoloLectura, propiedadesEditables, entidad);
    	for(String propiedad: propiedadesEditables){
    		getView().setEditable(propiedad, true);
    	}
    	for(String propiedad: propiedadesSoloLectura){
    		getView().setEditable(propiedad, false);
    	}
    	
    	this.ocultarAtributosPorSeguridad(entidad);
	}
	
	protected void evaluarPropiedades(List<String> propiedadesSoloLectura, List<String> propiedadesEditables, ConfiguradorEntidad configuracion) {
		// se aplican las propiedades solo lectura del objeto
		try{
			BasicBusiness objetoNegocio = (BasicBusiness) this.getView().getMetaModel().getPOJOClass().newInstance();
			Method method = objetoNegocio.getClass().getMethod("propiedadesSoloLecturaAlCrear", List.class, List.class, ConfiguradorEntidad.class);
			method.invoke(objetoNegocio, propiedadesSoloLectura, propiedadesEditables, configuracion);
			
			List<String> propiedadesVisibles = new LinkedList<String>();
		    List<String> propiedadesOcultas = new LinkedList<String>();
		    objetoNegocio.propiedadesOcultas(propiedadesOcultas, propiedadesVisibles);
		    for(String propiedad: propiedadesOcultas){
		    	this.getView().setHidden(propiedad, true);	        	
		    }
		    for(String propiedad: propiedadesVisibles){
		    	this.getView().setHidden(propiedad, false);	        	
		    }
		}
		catch(Exception e){			
		}
	}
	
	private void ocultarAtributosPorSeguridad(ConfiguradorEntidad entidad){
		try{
			User user = User.find(Users.getCurrent());
	        MetaModule metaModule = MetaApplications.getMetaApplication(MetaApplications.getApplicationsNames().iterator().next().toString()).getMetaModule(this.getView().getModelName());
	        Collection<MetaMember> collection = user.getExcludedMetaMembersForMetaModule(metaModule);
	        for(MetaMember member: collection){
	        	this.getView().setHidden(member.getName(), true);
	        }
			removeActions("MovimientoTransaccional.anular");
			addActions("BasicBusiness.save");
		}
		catch(Exception e){
			addError("Error exclusión de atributos: " + e.toString());
		}
		
		if (entidad != null){
	    	if (entidad.getOcultarImagenes()){
	        	// por defecto se muestran las imagenes
	    		try{
	    			getView().getSubview("imagen");
	    			getView().setHidden("imagen", true);
	    		}
	    		catch(ElementNotFoundException e){    			
	    		}
	    	}
    	}
	}
	
	private String nombreVistaAlCrear = null;
	
	protected String getNombreVistaAlCrear(){
		return nombreVistaAlCrear;
	}

	public void setNombreVistaAlCrear(String nombreVistaAlCrear) {
		this.nombreVistaAlCrear = nombreVistaAlCrear;
	}

		
}

