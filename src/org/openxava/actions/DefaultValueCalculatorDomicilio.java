package org.openxava.actions;

import org.openxava.calculators.ICalculator;
import org.openxava.negocio.model.Domicilio;

@SuppressWarnings("serial")
public class DefaultValueCalculatorDomicilio implements ICalculator{

 
	@Override
	public Object calculate() throws Exception {

		
        return Domicilio.buscarDomicilioPrincipal();

	}
}
