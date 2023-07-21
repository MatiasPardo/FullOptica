package org.openxava.actions;

import org.openxava.calculators.*;
import org.openxava.negocio.model.Empresa;

@SuppressWarnings("serial")
public class DefaultValueCalculatorEmpresa implements ICalculator{

 
	@Override
	public Object calculate() throws Exception {
		// TODO Auto-generated method stub

        Empresa empresa = Empresa.buscarEmpresaPrincipal();

        if (empresa != null) {
            return empresa; // Devuelve la empresa encontrada
        } else {
            return null; // Si no se encontr� la empresa, devuelve null
        }

	}
}
