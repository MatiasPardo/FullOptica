package org.openxava.negocio.base.actions;


import org.openxava.actions.ViewBaseAction;

public class ConfigurarEmpresaAction extends ViewBaseAction {

    @Override
    public void execute() throws Exception {
        
        try {
            String cuitProp = (String) getView().getValue("cuit");
            String razonSocialProp = (String) getView().getValue("razonSocial");
            String condicionIvaProp = (String) getView().getValue("condicionIva");
            String direccionProp = (String) getView().getValue("direccion");
            String emailProp = (String) getView().getValue("email");
            String telefonoProp = (String) getView().getValue("telefono");
            
            if (isEmpty(cuitProp)) {
                getView().setValue("cuit", "20123456789");
            }
            	
            if (isEmpty(razonSocialProp)) {
                getView().setValue("razonSocial", "FullOptica S.A.");
            }
            
            if (isEmpty(condicionIvaProp)) {
                getView().setValue("condicionIva", "Responsable Inscripto");
            }
            
            if (isEmpty(direccionProp)) {
                getView().setValue("direccion", "Av. Corrientes 1234");
            }
            
            if (isEmpty(emailProp)) {
                getView().setValue("email", "facturacion@fulloptica.com");
            }
            
            if (isEmpty(telefonoProp)) {
                getView().setValue("telefono", "011-4567-8900");
            }
            
            addMessage("Datos de empresa configurados");
            
        } catch (Exception e) {
            addWarning("No se pudieron configurar los datos: " + e.getMessage());
        }
    }
    
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}