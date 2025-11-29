package org.openxava.negocio.calculators;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.openxava.calculators.ICalculator;
import org.openxava.jpa.XPersistence;
import org.openxava.validators.ValidationException;

public class DefaultValueCalculatorPosicionIva implements ICalculator {

    @Override
    public Object calculate() throws Exception {
        EntityManager em = XPersistence.getManager();
        Query query = em.createQuery("SELECT p FROM PosicionIva p WHERE p.codigo = 'CF'");
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            throw new ValidationException("No se encuentra la posición IVA con código CF. Debe crear una posición IVA con código 'CF' antes de crear clientes.");
        }
    }
}