package org.openxava.negocio.base;

import org.openxava.actions.IChainAction;
import org.openxava.actions.SaveAction;
import org.openxava.model.MapFacade;

public class ConfirmarMovimientoTransaccionalAccion extends SaveAction implements IChainAction{

	@Override
	public void execute() throws Exception {
		try {
			MovementTransactional tr = (MovementTransactional)MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
			tr.accionesPreConfirmar();
			this.commit();
			
			tr = (MovementTransactional)MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());

			if (this.getErrors().isEmpty()){
				tr.confirmar();
				this.commit();
			}
		}catch(Exception e){
			this.rollback();
			
			if (e.getMessage() != null){
				addError(e.getMessage());
			}
			else{
				addError(e.toString());
			}
			this.getMessages().removeAll();
		}
				
	}
	
	@Override
	public String getNextAction() throws Exception {
		if (this.getErrors().isEmpty()){
			return "BasicBusiness.edit";
		}
		else{
			return null;
		}		
	}
}
