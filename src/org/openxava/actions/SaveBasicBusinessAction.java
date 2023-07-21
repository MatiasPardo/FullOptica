package org.openxava.actions;

public class SaveBasicBusinessAction extends SaveAction implements IChainAction{

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
