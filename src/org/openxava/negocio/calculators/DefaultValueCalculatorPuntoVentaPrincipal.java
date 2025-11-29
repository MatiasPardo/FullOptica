package org.openxava.negocio.calculators;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.openxava.calculators.ICalculator;
import org.openxava.jpa.XPersistence;

public class DefaultValueCalculatorPuntoVentaPrincipal implements ICalculator {

    @Override
    public Object calculate() throws Exception {
        EntityManager em = XPersistence.getManager();
        Query query = em.createQuery("SELECT p FROM PuntoVenta p WHERE p.principal = true");
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            // Si no encuentra punto de venta principal, retorna null
            return null;
        }
    }
}