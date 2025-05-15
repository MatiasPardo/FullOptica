package org.openxava.negocio.calculators;

import org.openxava.actions.ViewBaseAction;
import org.openxava.model.Estado;
import org.openxava.model.MapFacade;
import org.openxava.negocio.base.MovementTransactional;

public class StateViewActionChange extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		Object bo = MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());

        if(bo instanceof MovementTransactional){
        	Estado estado = ((MovementTransactional) bo).getEstado();
			if(estado != null && estado.equals(Estado.Confirmada)){
				//showAction("");
				removeActions("BasicBusiness.save");
			}
		}
        		
	}

}
