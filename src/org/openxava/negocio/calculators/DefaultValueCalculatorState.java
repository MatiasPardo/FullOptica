package org.openxava.negocio.calculators;

import org.openxava.calculators.ICalculator;
import org.openxava.model.Estado;

@SuppressWarnings("serial")
public class DefaultValueCalculatorState implements ICalculator{

	@Override
	public Object calculate() throws Exception {
		return Estado.Borrador;
	}

}
