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
		System.out.println("[ConfirmarMovimiento] Iniciando confirmacion - Modelo: " + getView().getModelName());
		try {
			MovementTransactional tr = (MovementTransactional)MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
			System.out.println("[ConfirmarMovimiento] Entidad encontrada: " + (tr != null ? tr.getClass().getSimpleName() : "null"));
			
			if(tr == null) throw new ValidationException("Primero debe grabar");
			System.out.println("[ConfirmarMovimiento] Estado actual: " + tr.getEstado());
			if(tr.getEstado().equals(Estado.Confirmada) || tr.getEstado().equals(Estado.Anulada)){
				throw new ValidationException("La Factura no tiene un estado valido para confirmar");
			}
			
			System.out.println("[ConfirmarMovimiento] Ejecutando accionesPreConfirmar...");
			tr.accionesPreConfirmar();
			System.out.println("[ConfirmarMovimiento] Primer commit...");
			this.commit();
			
			System.out.println("[ConfirmarMovimiento] Refrescando entidad...");
			tr = (MovementTransactional)MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());

			if (this.getErrors().isEmpty()){
				System.out.println("[ConfirmarMovimiento] Ejecutando confirmar()...");
				tr.confirmar();
				Estado trEstado = tr.getEstado();
				System.out.println("[ConfirmarMovimiento] Estado despues de confirmar: " + trEstado);
				System.out.println("[ConfirmarMovimiento] Segundo commit...");
				this.commit();
				if(trEstado.equals(Estado.Confirmada)) this.addMessage("Se confirmo la transaccion con exito, gracias por sumar otra venta xD");
				else this.addError("No se pudo confirmar, revise los datos");
			}
		}catch(Exception e){
			System.err.println("[ConfirmarMovimiento] ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			e.printStackTrace();
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
