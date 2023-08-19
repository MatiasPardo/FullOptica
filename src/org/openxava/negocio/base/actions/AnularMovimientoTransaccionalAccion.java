package org.openxava.negocio.base.actions;

import org.openxava.actions.IChainAction;
import org.openxava.actions.ViewBaseAction;
import org.openxava.model.Estado;
import org.openxava.model.MapFacade;
import org.openxava.negocio.base.MovementTransactional;
import org.openxava.validators.ValidationException;

public class AnularMovimientoTransaccionalAccion extends ViewBaseAction implements IChainAction{

	@Override
	public void execute() throws Exception {
		try {
			MovementTransactional tr = (MovementTransactional)MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
			
			if(tr == null) throw new ValidationException("Error al buscar la transaccion");
			
			tr.accionesPreAnular();
			this.commit();
			
			tr = (MovementTransactional)MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());

			if (this.getErrors().isEmpty()){
				tr.anular();
				Estado trEstado = tr.getEstado();
				this.commit();
				if(trEstado.equals(Estado.Anulada)) this.addMessage("Se anulo la transaccion con exito T_T");
				else this.addError("No se pudo Anular, revise los datos");
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
