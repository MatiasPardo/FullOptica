package org.openxava.negocio.calculators;

import javax.persistence.Query;

import org.openxava.calculators.ICalculator;
import org.openxava.jpa.XPersistence;

public class DefaultValueCalculatorPuntoVenta implements ICalculator {

    @Override
    public Object calculate() throws Exception {
        try {
            // Buscar el primer punto de venta manual (electronico = false)
            Query query = XPersistence.getManager().createQuery(
                "SELECT pv FROM PuntoVenta pv WHERE pv.electronico = false ORDER BY pv.codigo");
            query.setMaxResults(1);
            
            java.util.List<?> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                return resultados.get(0);
            }
            
            // Si no hay manual, buscar cualquier punto de venta
            query = XPersistence.getManager().createQuery(
                "SELECT pv FROM PuntoVenta pv ORDER BY pv.codigo");
            query.setMaxResults(1);
            
            resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                return resultados.get(0);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}