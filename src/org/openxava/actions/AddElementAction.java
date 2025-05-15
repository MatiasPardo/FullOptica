package org.openxava.actions;

import org.openxava.model.MapFacade;
import org.openxava.negocio.base.MovementTransactional;
import org.openxava.negocio.model.FacturaVenta;

public class AddElementAction extends SaveElementInCollectionAction implements IChainAction {

	@Override
	public void execute() throws Exception {
		super.execute();
		
		MovementTransactional tr = (MovementTransactional) MapFacade.findEntity(
			getView().getModelName(),
			getView().getKeyValues()
		);
		
        if(tr instanceof FacturaVenta) {
        	((FacturaVenta) tr).recalculateData();
        }
        
	}
	
	@Override
	public String getNextAction() throws Exception {
			return null;//"BasicBusiness.edit";
	}
}