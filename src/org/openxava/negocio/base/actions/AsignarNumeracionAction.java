package org.openxava.negocio.base.actions;

import javax.persistence.Query;

import org.openxava.actions.ViewBaseAction;
import org.openxava.jpa.XPersistence;
import org.openxava.negocio.model.Numerador;
import org.openxava.validators.ValidationException;

public class AsignarNumeracionAction extends ViewBaseAction {

    public static String asignarNumero(String tipoComprobante, String empresaId, String sucursalId) {
        return asignarNumero(tipoComprobante, empresaId, sucursalId, null);
    }
    
    public static String asignarNumero(String tipoComprobante, String empresaId, String sucursalId, String puntoVentaId) {
        return asignarNumero(tipoComprobante, empresaId, sucursalId, puntoVentaId, "C");
    }
    
    public static String asignarNumero(String tipoComprobante, String empresaId, String sucursalId, String puntoVentaId, String tipoComprobanteAfip) {
        System.out.println("[AsignarNumeracion] Iniciando asignacion - Tipo: " + tipoComprobante + ", EmpresaId: " + empresaId + ", SucursalId: " + sucursalId + ", PuntoVentaId: " + puntoVentaId + ", TipoAfip: " + tipoComprobanteAfip);
        
        try {
            // Buscar numerador - priorizar por punto de venta si existe
            String jpql;
            Query query;
            
            if (puntoVentaId != null) {
                // Buscar numerador específico para punto de venta y tipo de comprobante
                jpql = "SELECT n FROM Numerador n WHERE n.nombre = :tipo AND n.empresa.id = :empresaId AND n.puntoVenta.id = :puntoVentaId AND n.tipoComprobante = :tipoComprobanteAfip";
                System.out.println("[AsignarNumeracion] Query con punto de venta: " + jpql);
                query = XPersistence.getManager().createQuery(jpql);
                query.setParameter("tipo", tipoComprobante);
                query.setParameter("empresaId", empresaId);
                query.setParameter("puntoVentaId", puntoVentaId);
                query.setParameter("tipoComprobanteAfip", tipoComprobanteAfip);
            } else {
                // Buscar numerador general sin punto de venta pero con tipo de comprobante
                jpql = "SELECT n FROM Numerador n WHERE n.nombre = :tipo AND n.empresa.id = :empresaId AND n.puntoVenta IS NULL AND n.tipoComprobante = :tipoComprobanteAfip";
                if (sucursalId != null) {
                    jpql += " AND n.sucursal.id = :sucursalId";
                } else {
                    jpql += " AND n.sucursal IS NULL";
                }
                System.out.println("[AsignarNumeracion] Query sin punto de venta: " + jpql);
                
                query = XPersistence.getManager().createQuery(jpql);
                query.setParameter("tipo", tipoComprobante);
                query.setParameter("empresaId", empresaId);
                query.setParameter("tipoComprobanteAfip", tipoComprobanteAfip);
                if (sucursalId != null) {
                    query.setParameter("sucursalId", sucursalId);
                }
            }
            
            System.out.println("[AsignarNumeracion] Ejecutando query...");
            
            Numerador numerador = null;
            try {
                numerador = (Numerador) query.getSingleResult();
                System.out.println("[AsignarNumeracion] Numerador encontrado: " + numerador.getNombre() + ", ProximoNumero: " + numerador.getProximoNumero());
            } catch (javax.persistence.NoResultException e) {
                System.err.println("[AsignarNumeracion] No se encontro numerador con criterios exactos, buscando alternativas...");
                
                // Buscar numeradores disponibles para diagnostico
                Query debugQuery = XPersistence.getManager().createQuery(
                    "SELECT n FROM Numerador n WHERE n.empresa.id = :empresaId AND n.nombre = :tipo");
                debugQuery.setParameter("empresaId", empresaId);
                debugQuery.setParameter("tipo", tipoComprobante);
                
                java.util.List<?> numeradores = debugQuery.getResultList();
                System.out.println("[AsignarNumeracion] Numeradores disponibles para empresa " + empresaId + " y tipo " + tipoComprobante + ": " + numeradores.size());
                
                for (Object obj : numeradores) {
                    Numerador n = (Numerador) obj;
                    System.out.println("[AsignarNumeracion] - Numerador: " + n.getNombre() + 
                        ", TipoComprobante: " + n.getTipoComprobante() + 
                        ", PuntoVenta: " + (n.getPuntoVenta() != null ? n.getPuntoVenta().getId() : "null") + 
                        ", Sucursal: " + (n.getSucursal() != null ? n.getSucursal().getId() : "null"));
                }
                
                // Crear mensaje de error específico
                String mensajeError = "No existe numerador configurado para Factura tipo " + tipoComprobanteAfip;
                if (puntoVentaId != null) {
                    // Obtener código del punto de venta para el mensaje
                    try {
                        Query pvQuery = XPersistence.getManager().createQuery("SELECT pv.codigo FROM PuntoVenta pv WHERE pv.id = :id");
                        pvQuery.setParameter("id", puntoVentaId);
                        String codigoPV = (String) pvQuery.getSingleResult();
                        mensajeError += " y punto de venta " + codigoPV;
                    } catch (Exception ex) {
                        mensajeError += " y punto de venta seleccionado";
                    }
                }
                mensajeError += ". Debe crear el numerador correspondiente.";
                
                throw new ValidationException(mensajeError);
            }
            
            String numero = numerador.obtenerSiguienteNumero();
            System.out.println("[AsignarNumeracion] Numero asignado: " + numero);
            
            XPersistence.getManager().merge(numerador);
            System.out.println("[AsignarNumeracion] Numerador actualizado correctamente");
            
            return numero;
            
        } catch (ValidationException ve) {
            // Re-lanzar ValidationException sin modificar
            throw ve;
        } catch (Exception e) {
            System.err.println("[AsignarNumeracion] ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new ValidationException("Error al asignar numeracion para " + tipoComprobante + ": " + e.getMessage());
        }
    }

    @Override
    public void execute() throws Exception {
        // Esta accion se puede usar desde otras acciones
        addMessage("Numeracion asignada correctamente");
    }
}