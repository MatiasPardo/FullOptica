package org.openxava.negocio.calculators;

import java.util.Date;

import org.openxava.calculators.ICalculator;

@SuppressWarnings("serial")
public class DefaultValueCalculatorDateNow implements ICalculator{

	@Override
	public Object calculate() throws Exception {
		return new Date();
	}

}
