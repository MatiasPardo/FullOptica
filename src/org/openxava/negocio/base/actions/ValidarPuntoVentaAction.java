package org.openxava.negocio.base.actions;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.openxava.actions.OnChangePropertyBaseAction;
import org.openxava.jpa.XPersistence;
import org.openxava.model.MapFacade;
import org.openxava.negocio.model.FacturaVenta;
import org.openxava.negocio.model.PuntoVenta;
import org.openxava.jpa.XPersistence;

public class ValidarPuntoVentaAction extends OnChangePropertyBaseAction {

    @Override
    public void execute() throws Exception {
        // Verificar si la entidad existe (no es nueva)
        if (getView().getKeyValues() == null || getView().getKeyValues().isEmpty() || 
            getView().getKeyValues().containsValue(null)) {
            return; // Es una entidad nueva, no hacer nada
        }
        
        FacturaVenta factura = null;
        try {
            factura = (FacturaVenta) MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
        } catch (Exception e) {
            return; // Error al buscar la entidad, probablemente no existe
        }
        
        if (factura == null) {
            return; // No se encontró la entidad
        }
        Object puntoVentaValue = getView().getValue("puntoVenta");
        PuntoVenta puntoVenta = null;
        
        if (puntoVentaValue instanceof PuntoVenta) {
            puntoVenta = (PuntoVenta) puntoVentaValue;
        } else if (puntoVentaValue instanceof java.util.Map) {
            // Si es un Map, obtener el ID y buscar la entidad
            java.util.Map<?, ?> puntoVentaMap = (java.util.Map<?, ?>) puntoVentaValue;
            String puntoVentaId = (String) puntoVentaMap.get("id");
            if (puntoVentaId != null) {
                puntoVenta = XPersistence.getManager().find(PuntoVenta.class, puntoVentaId);
            }
        }
        
        if (puntoVenta != null) {
            // Verificar si existe numerador para este punto de venta
            EntityManager em = XPersistence.getManager();
            
            String jpql = "SELECT COUNT(n) FROM Numerador n WHERE n.nombre = 'FacturaVenta' " +
                         "AND n.empresa.id = :empresaId AND n.sucursal.id = :sucursalId " +
                         "AND n.puntoVenta.id = :puntoVentaId";
            
            Query query = em.createQuery(jpql);
            query.setParameter("empresaId", factura.getEmpresa().getId());
            query.setParameter("sucursalId", factura.getSucursal() != null ? factura.getSucursal().getId() : null);
            query.setParameter("puntoVentaId", puntoVenta.getId());
            
            Long count = (Long) query.getSingleResult();
            
            if (count == 0) {
                addWarning("No existe numerador configurado para el punto de venta " + 
                          puntoVenta.getCodigo() + ". Se usará numeración automática.");
            } else {
                addMessage("Punto de venta válido: " + puntoVenta.getCodigo() + 
                          (puntoVenta.getElectronico() ? " (Electrónico)" : " (Manual)"));
            }
            
            // Limpiar número si cambia el punto de venta
            String numeroActual = (String) getView().getValue("numero");
            if (numeroActual != null && !numeroActual.isEmpty()) {
                getView().setValue("numero", "");
                addMessage("Número de factura reiniciado por cambio de punto de venta");
            }
        }
    }
}