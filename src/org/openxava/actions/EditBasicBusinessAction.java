package org.openxava.actions;


import java.util.*;
import org.openxava.application.meta.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.negocio.base.BasicBusiness;
import org.openxava.util.*;

import com.openxava.naviox.model.*;

public class EditBasicBusinessAction extends SearchByViewKeyAction{//extends SearchExecutingOnChangeAction{
	
	private BasicBusiness basicBusiness = null;
	
	private boolean configuracionBuscada = false;
	
	private ConfiguradorEntidad configuracion = null;
	
	private String atributoFoco;
		
	protected ConfiguradorEntidad getConfiguracionEntidad(){
		if (!configuracionBuscada){
			configuracionBuscada = true;
			configuracion = ConfiguradorEntidad.buscarConfigurador(getView().getModelName()); 
		}
		return configuracion;
	}
	
	protected BasicBusiness getBasicBusiness() throws Exception{
		if (basicBusiness == null){
			Object object = MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
			if (object instanceof BasicBusiness){
				this.basicBusiness = (BasicBusiness)object;
			}
		}
		return this.basicBusiness;
	}
	
	@Override
	public void execute() throws Exception {
		BasicBusiness bo = this.getBasicBusiness();
		if (bo != null){
			// primero se asigna el nombre de la view
			String newViewName = bo.viewName(this.getView()); 
			if (!Is.equal(newViewName, getView().getViewName())){
				@SuppressWarnings("rawtypes")
				Map clave = getView().getKeyValuesWithValue();
				getView().setViewName(newViewName);
				getView().setValues(clave);
			}
		}
				
        super.execute();
        // para solucionar bugs de los showReferenceView en DescriptionList (ejemplo: campo cliente en pedido)
        this.getView().refreshDescriptionsLists();
        
        if (bo != null){
	        if (bo.readOnly()){
	        	getView().setEditable(false);	        	
	        }
	        else{
	        	List<String> propiedadesSoloLectura = new LinkedList<String>();
	        	List<String> propiedadesEditables = new LinkedList<String>();
	        	ConfiguradorEntidad configuracion = ConfiguradorEntidad.buscarConfigurador(getView().getModelName());
	        	bo.propertiesReadOnlyOnEdit(this.getView(), configuracion);
	        	for(String propiedad: propiedadesEditables){
	        		getView().setEditable(propiedad, true);
	        	}
	        	for(String propiedad: propiedadesSoloLectura){
	        		getView().setEditable(propiedad, false);
	        	}
	        }
	        
	        List<String> propiedadesVisibles = new LinkedList<String>();
	        List<String> propiedadesOcultas = new LinkedList<String>();
	        bo.propiedadesOcultas(propiedadesOcultas, propiedadesVisibles);
	        for(String propiedad: propiedadesOcultas){
	        	this.getView().setHidden(propiedad, true);	        	
	        }
	        for(String propiedad: propiedadesVisibles){
	        	this.getView().setHidden(propiedad, false);	        	
	        }
        }
        
        // Atributos ocultos
        this.ocultarAtributos();
        
        if (!Is.emptyString(this.getAtributoFoco())){
        	if (this.getView().isEditable()){
        		this.getView().setFocus(this.getAtributoFoco());        		
        	}
        }        
	}
	
	protected void ocultarAtributos(){
		try{
			User user = User.find(Users.getCurrent());
	        MetaModule metaModule = MetaApplications.getMetaApplication(MetaApplications.getApplicationsNames().iterator().next().toString()).getMetaModule(this.getView().getModelName());
	        Collection<MetaMember> collection = user.getExcludedMetaMembersForMetaModule(metaModule);
	        for(MetaMember member: collection){
	        	this.getView().setHidden(member.getName(), true);
	        }
		}
		catch(Exception e){
			addError("Error exclusión de atributos: " + e.toString());
		}
		
		if (this.getConfiguracionEntidad() != null){
			if (this.getConfiguracionEntidad().getOcultarImagenes()){
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

	public String getAtributoFoco() {
		return atributoFoco;
	}

	public void setAtributoFoco(String atributoFoco) {
		this.atributoFoco = atributoFoco;
	}

	public void setBasicBusiness(BasicBusiness basicBusiness) {
		this.basicBusiness = basicBusiness;
	}

}
