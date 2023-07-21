package org.openxava.report.action;

import javax.validation.ValidationException;

import org.openxava.actions.GenerateReportAction;

public class GenerateReportBussinesAction extends GenerateReportAction{

	private static final int CANTIDAD_MAXIMA_FILAS = 50000;

	@Override
	public void execute() throws Exception{
		
		int totalFilas = 0;
		
		if(getTab().hasSelected()){
			totalFilas = getTab().getSelectedKeys().length;
		}else{
			totalFilas = this.getTab().getTotalSize();
		}
		
		if(totalFilas > CANTIDAD_MAXIMA_FILAS){
			throw new ValidationException("Cantidad de registros en pantalla mayor a " + CANTIDAD_MAXIMA_FILAS + " por favor ingrese algun filtro.");
		}
		
		this.getRequest().getSession().setAttribute("row_total", totalFilas);
		super.execute();
	}
}
