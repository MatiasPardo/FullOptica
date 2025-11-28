package org.openxava.negocio.base.actions;

import javax.persistence.Query;

import org.openxava.actions.ViewBaseAction;
import org.openxava.jpa.XPersistence;
import org.openxava.negocio.base.adapters.ARCAWebServiceAdapter;
import org.openxava.negocio.model.Numerador;
import org.openxava.negocio.model.PuntoVenta;
import org.openxava.validators.ValidationException;

public class ConsultarUltimoComprobanteAction extends ViewBaseAction {

    @Override
    public void execute() throws Exception {
        // Obtener el numerador actual
        Numerador numerador = (Numerador) getView().getEntity();
        
        if (numerador == null) {
            throw new ValidationException("Debe seleccionar un numerador");
        }
        
        // Verificar que tenga punto de venta
        if (numerador.getPuntoVenta() == null) {
            throw new ValidationException("El numerador debe tener un punto de venta asignado");
        }
        
        // Verificar que el punto de venta sea electrónico
        if (!numerador.getPuntoVenta().getElectronico()) {
            throw new ValidationException("Solo se puede consultar numeradores de puntos de venta electrónicos");
        }
        
        String puntoVenta = numerador.getPuntoVenta().getCodigo();
        String tipoComprobante = String.valueOf(numerador.getCodigoAfip());
        
        System.out.println("[ConsultarUltimo] Consultando PV: " + puntoVenta + ", Código AFIP: " + tipoComprobante);
        
        try {
            ARCAWebServiceAdapter.UltimoComprobanteResponse response = 
                ARCAWebServiceAdapter.consultarUltimoComprobante(puntoVenta, tipoComprobante);
            
            if (response.isSuccess()) {
                String ultimoNumero = response.getUltimoNumero();
                addMessage("Último número en AFIP para PV " + puntoVenta + 
                          " tipo " + tipoComprobante + ": " + ultimoNumero);
                
                // Sugerir actualización si es diferente
                Long proximoLocal = numerador.getProximoNumero();
                try {
                    Long ultimoAfip = Long.parseLong(ultimoNumero);
                    Long proximoAfip = ultimoAfip + 1;
                    
                    if (!proximoLocal.equals(proximoAfip)) {
                        addWarning("ATENCIÓN: El próximo número local (" + proximoLocal + 
                                 ") no coincide con AFIP (" + proximoAfip + 
                                 "). Considere actualizar el numerador.");
                    } else {
                        addMessage("✓ El numerador local está sincronizado con AFIP");
                    }
                } catch (NumberFormatException e) {
                    addMessage("Último número AFIP: " + ultimoNumero + " (formato no numérico)");
                }
                
            } else {
                throw new ValidationException("Error consultando AFIP: " + response.getError());
            }
            
        } catch (Exception e) {
            System.err.println("[ConsultarUltimo] Error: " + e.getMessage());
            throw new ValidationException("Error al consultar último comprobante: " + e.getMessage());
        }
    }
}