package org.openxava.actions;

import org.openxava.model.Estado;
import org.openxava.model.MapFacade;
import org.openxava.negocio.base.MovementTransactional;
import org.openxava.validators.ValidationException;

public class SaveBasicBusinessAction extends SaveAction implements IChainAction{

	@Override
	public void execute() throws Exception {
		super.execute();
		Object object = MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
		if(object instanceof MovementTransactional){
			if(((MovementTransactional) object).getEstado() != null && Estado.isFinal(((MovementTransactional) object).getEstado())) {
				throw new ValidationException("No se puede grabar en este estado");
			}
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
