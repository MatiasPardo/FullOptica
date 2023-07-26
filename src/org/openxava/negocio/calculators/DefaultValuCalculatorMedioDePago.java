package org.openxava.negocio.calculators;

import org.openxava.calculators.ICalculator;
import org.openxava.negocio.model.MedioDePago;

@SuppressWarnings("serial")
public class DefaultValuCalculatorMedioDePago implements ICalculator{

	@Override
	public Object calculate() throws Exception {
        return MedioDePago.buscarMedioDePagoPrincipal();
	}

}
