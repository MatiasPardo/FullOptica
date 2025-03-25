package org.openxava.actions;

import org.openxava.model.*;
import org.openxava.negocio.base.MovementTransactional;
import org.openxava.negocio.model.FacturaVenta;

public class BorrarItemTransaccionSeleccionadoAction extends RemoveSelectedInCollectionAction implements IChainAction{
	
	private boolean refrescarTransaccion = true;
	
	@Override
	public void execute() throws Exception{
		super.execute();
		MovementTransactional tr = (MovementTransactional)MapFacade.findEntity(this.getView().getModelName(), this.getView().getKeyValues());		
		//this.refrescarTransaccion = tr.refrescarPosModificacionItem();
		//tr.grabarTransaccion();
        if(tr instanceof FacturaVenta) {
        	((FacturaVenta) tr).recalculateData();
        }
		this.commit();
	}
	
	@Override
	public String getNextAction() throws Exception {
		if (this.refrescarTransaccion){
			return "BasicBusiness.edit";
		}
		else{
			return null;
		}
	}
}