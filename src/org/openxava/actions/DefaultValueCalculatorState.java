package org.openxava.actions;

import org.openxava.calculators.ICalculator;
import org.openxava.model.Estado;

@SuppressWarnings("serial")
public class DefaultValueCalculatorState implements ICalculator{

	@Override
	public Object calculate() throws Exception {
		// TODO Auto-generated method stub
		return Estado.Borrador;
	}

}
