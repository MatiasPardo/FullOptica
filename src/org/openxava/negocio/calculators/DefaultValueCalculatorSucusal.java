package org.openxava.negocio.calculators;

import org.openxava.calculators.ICalculator;
import org.openxava.negocio.model.Sucursal;
import org.openxava.util.Users;

@SuppressWarnings("serial")
public class DefaultValueCalculatorSucusal implements ICalculator{

	@Override
	public Object calculate() throws Exception {
		   
		String userActual = Users.getCurrent();
		return Sucursal.obtenerSucrusalHabiitada(userActual);
	} 	

}
