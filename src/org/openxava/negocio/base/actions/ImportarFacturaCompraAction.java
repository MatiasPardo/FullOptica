package org.openxava.negocio.base.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.fileupload.FileItem;
import org.openxava.actions.ViewBaseAction;
import org.openxava.jpa.XPersistence;
import org.openxava.negocio.model.FacturaCompra;
import org.openxava.negocio.model.ItemFacturaCompra;
import org.openxava.util.Messages;

public class ImportarFacturaCompraAction extends ViewBaseAction {

    public void execute() throws Exception {
        Messages errors = new Messages();
        
        FileItem fileItem = (FileItem) getRequest().getAttribute("archivo");
        if (fileItem == null || fileItem.getSize() == 0) {
            addError("Debe seleccionar un archivo CSV");
            return;
        }

        try {
            procesarArchivo(fileItem.getInputStream(), errors);
            if (errors.isEmpty()) {
                addMessage("Importación completada exitosamente");
            } else {
                addErrors(errors);
            }
        } catch (Exception e) {
            addError("Error al procesar archivo: " + e.getMessage());
        }
    }

    private void procesarArchivo(InputStream inputStream, Messages errors) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int lineNumber = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (lineNumber == 1) continue; // Skip header
            
            try {
                String[] campos = line.split(",");
                if (campos.length < 5) {
                    errors.add("Línea " + lineNumber + ": Faltan campos");
                    continue;
                }

                Date fechaFactura = dateFormat.parse(campos[0].trim());
                BigDecimal montoTotal = new BigDecimal(campos[1].trim());
                String codigoItem = campos[2].trim();
                String codigoProveedor = campos[3].trim();
                String descripcionItem = campos[4].trim();

                crearFacturaCompra(fechaFactura, montoTotal, codigoItem, codigoProveedor, descripcionItem);

            } catch (ParseException e) {
                errors.add("Línea " + lineNumber + ": Formato de fecha inválido");
            } catch (NumberFormatException e) {
                errors.add("Línea " + lineNumber + ": Monto inválido");
            } catch (Exception e) {
                errors.add("Línea " + lineNumber + ": " + e.getMessage());
            }
        }
        
        XPersistence.commit();
    }

    private void crearFacturaCompra(Date fechaFactura, BigDecimal montoTotal, 
                                   String codigoItem, String codigoProveedor, String descripcionItem) {
        FacturaCompra factura = new FacturaCompra();
        factura.setFecha(fechaFactura);
        
        // Aquí deberías buscar el proveedor por código
        // factura.setProveedor(buscarProveedor(codigoProveedor));
        
        XPersistence.getManager().persist(factura);
        
        // Crear item de factura
        ItemFacturaCompra item = new ItemFacturaCompra();
        item.setCompra(factura);
        // item.setProducto(buscarProducto(codigoItem));
        item.setPrecio(montoTotal);
        item.setCantidad(BigDecimal.ONE);
        
        XPersistence.getManager().persist(item);
    }
}