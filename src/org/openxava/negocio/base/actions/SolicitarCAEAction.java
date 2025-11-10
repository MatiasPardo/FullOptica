package org.openxava.negocio.base.actions;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.openxava.actions.ViewBaseAction;
import org.openxava.model.Estado;
import org.openxava.model.MapFacade;
import org.openxava.negocio.base.adapters.ARCAWebServiceAdapter;
import org.openxava.negocio.model.FacturaVenta;
import org.openxava.validators.ValidationException;

public class SolicitarCAEAction extends ViewBaseAction implements org.openxava.actions.IChainAction {

    @Override
    public void execute() throws Exception {
        System.out.println("[SolicitarCAE] Iniciando solicitud de CAE");
        FacturaVenta factura = (FacturaVenta) MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
        System.out.println("[SolicitarCAE] Factura encontrada - Numero: " + factura.getNumero() + ", Estado: " + factura.getEstado());
        
        if (factura.getEstado() != Estado.Confirmada) {
            throw new ValidationException("Solo se puede solicitar CAE para facturas confirmadas");
        }
        
        System.out.println("[SolicitarCAE] PuntoVenta: " + (factura.getPuntoVenta() != null ? factura.getPuntoVenta().getCodigo() : "null"));
        if (factura.getPuntoVenta() == null || !factura.getPuntoVenta().getElectronico()) {
            throw new ValidationException("Debe seleccionar un punto de venta electrónico");
        }
        
        if (factura.getCae() != null && !factura.getCae().isEmpty()) {
            throw new ValidationException("La factura ya tiene CAE asignado: " + factura.getCae());
        }
        
        System.out.println("[SolicitarCAE] Validando numeracion...");
        validarNumeracionPuntoVenta(factura);
        
        System.out.println("[SolicitarCAE] Solicitando CAE a AFIP...");
        ARCAWebServiceAdapter.CAEResponse response = ARCAWebServiceAdapter.solicitarCAE(factura);
        
        if (response.isSuccess()) {
            factura.setCae(response.getCae());
            factura.setFechaVencimientoCae(response.getFechaVencimiento());
            System.out.println("[SolicitarCAE] CAE obtenido: " + response.getCae());
            addMessage("CAE obtenido exitosamente: " + response.getCae());
        } else {
            System.err.println("[SolicitarCAE] Error en respuesta AFIP: " + response.getError());
            throw new ValidationException(response.getError());
        }
    }
    
    private void validarNumeracionPuntoVenta(FacturaVenta factura) throws Exception {
        System.out.println("[SolicitarCAE.validarNumeracion] Validando numerador...");
        EntityManager em = org.openxava.jpa.XPersistence.getManager();
        
        String jpql = "SELECT n FROM Numerador n WHERE n.empresa.id = :empresaId " +
            "AND n.sucursal.id = :sucursalId AND n.puntoVenta.id = :puntoVentaId " +
            "AND n.nombre = 'FacturaVenta' AND n.tipoComprobante = :tipoComprobante";
        
        System.out.println("[SolicitarCAE.validarNumeracion] Query: " + jpql);
        System.out.println("[SolicitarCAE.validarNumeracion] Parametros - EmpresaId: " + factura.getEmpresa().getId() + 
            ", SucursalId: " + factura.getSucursal().getId() + 
            ", PuntoVentaId: " + factura.getPuntoVenta().getId() + 
            ", TipoComprobante: " + factura.getTipoComprobante());
        
        // Buscar numerador para el punto de venta y tipo de comprobante
        Query query = em.createQuery(jpql);
        query.setParameter("empresaId", factura.getEmpresa().getId());
        query.setParameter("sucursalId", factura.getSucursal().getId());
        query.setParameter("puntoVentaId", factura.getPuntoVenta().getId());
        query.setParameter("tipoComprobante", factura.getTipoComprobante());
        
        if (query.getResultList().isEmpty()) {
            System.err.println("[SolicitarCAE.validarNumeracion] No se encontro numerador");
            throw new ValidationException("No existe numerador configurado para el punto de venta " + 
                factura.getPuntoVenta().getCodigo());
        }
        System.out.println("[SolicitarCAE.validarNumeracion] Numerador encontrado correctamente");
    }
    
    @Override
    public String getNextAction() throws Exception {
        if (this.getErrors().isEmpty()) {
            return "BasicBusiness.edit";
        } else {
            return null;
        }
    }

}