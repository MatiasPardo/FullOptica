package org.openxava.negocio.base.actions;

import org.openxava.actions.IChainAction;
import org.openxava.model.MapFacade;
import org.openxava.negocio.model.FacturaVenta;

public class ConfirmarYSolicitarCAEAction extends ConfirmarMovimientoTransaccionalAccion implements IChainAction {

    @Override
    public void execute() throws Exception {
        // Primero ejecutar la confirmacion normal
        super.execute();
        
        // Si la confirmacion fue exitosa, intentar solicitar CAE
        if (this.getErrors().isEmpty()) {
            try {
                FacturaVenta factura = (FacturaVenta) MapFacade.findEntity(getView().getModelName(), getView().getKeyValues());
                
                // Solo solicitar CAE si es punto de venta electrónico y no tiene CAE
                if (factura != null && 
                    factura.getPuntoVenta() != null && 
                    factura.getPuntoVenta().getElectronico() &&
                    (factura.getCae() == null || factura.getCae().isEmpty())) {
                    
                    System.out.println("[ConfirmarYSolicitarCAE] Solicitando CAE automaticamente...");
                    
                    // Crear y ejecutar la acción de solicitar CAE
                    SolicitarCAEAction solicitarCAE = new SolicitarCAEAction();
                    solicitarCAE.setView(getView());
                    solicitarCAE.setRequest(getRequest());
                    solicitarCAE.execute();
                    
                    // Transferir errores espeificos de AFIP
                    if (!solicitarCAE.getErrors().isEmpty()) {
                        // Obtener el primer error que generalmente contiene la respuesta de AFIP
                        Object firstError = solicitarCAE.getErrors().getStrings().iterator().next();
                        addError(firstError.toString());
                    } else {
                        // Transferir mensaje de exito si existe
                        if (!solicitarCAE.getMessages().isEmpty()) {
                            Object firstMessage = solicitarCAE.getMessages().getStrings().iterator().next();
                            addMessage(firstMessage.toString());
                        }
                    }
                }
            } catch (Exception e) {
                String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                System.err.println("[ConfirmarYSolicitarCAE] Error solicitando CAE: " + errorMsg);
                addError("Factura confirmada pero error al solicitar CAE: " + errorMsg);
            }
        }
    }
    
    @Override
    public String getNextAction() throws Exception {
        return "BasicBusiness.edit";
    }
}