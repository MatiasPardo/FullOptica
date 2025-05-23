package org.openxava.negocio.base.actions;

import org.openxava.actions.IChainAction;
import org.openxava.actions.SaveAction;
import org.openxava.model.Estado;
import org.openxava.model.MapFacade;
import org.openxava.negocio.base.MovementTransactional;
import org.openxava.validators.ValidationException;

public class ConfirmarMovimientoTransaccionalAccion extends SaveAction implements IChainAction{

	@Override
	public void execute() throws Exception {
		try {
			MovementTransactional tr = (MovementTransactional)MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
			
			if(tr == null) throw new ValidationException("Primero debe grabar");
			if(tr.getEstado().equals(Estado.Confirmada) || tr.getEstado().equals(Estado.Anulada)){
				throw new ValidationException("La Factura no tiene un estado valido para confirmar");
			}
			
			tr.accionesPreConfirmar();
			this.commit();
			
			tr = (MovementTransactional)MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());

			if (this.getErrors().isEmpty()){
				tr.confirmar();
				Estado trEstado = tr.getEstado();
				this.commit();
				if(trEstado.equals(Estado.Confirmada)) this.addMessage("Se confirmo la transaccion con exito, gracias por sumar otra venta xD");
				else this.addError("No se pudo confirmar, revise los datos");
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
