package org.openxava.negocio.base.actions;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openxava.actions.IForwardAction;
import org.openxava.actions.ViewBaseAction;
import org.openxava.jpa.XPersistence;
import org.openxava.model.Estado;
import org.openxava.negocio.model.FacturaVenta;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class GenerarPDFFacturaVentaAction extends ViewBaseAction implements IForwardAction {

    @Override
    public void execute() throws Exception {
        Estado estado = (Estado) getView().getValue("estado");
        if (estado != Estado.Confirmada) {
            addError("Solo se puede generar PDF de facturas confirmadas");
            return;
        }
        
        String facturaId = getView().getValueString("id");
        FacturaVenta factura = XPersistence.getManager().find(FacturaVenta.class, facturaId);
        
        if (factura == null) {
            addError("Factura no encontrada");
            return;
        }
        
        generarPDF(factura);
    }
    
    private void generarPDF(FacturaVenta factura) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Encabezado con 3 columnas
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{2, 1, 2});
        
        // Datos de la empresa (izquierda)
        PdfPCell empresaCell = new PdfPCell();
        empresaCell.setBorder(Rectangle.BOX);
        empresaCell.setPadding(10);
        Font empresaFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Paragraph empresaInfo = new Paragraph();
        empresaInfo.add(new Paragraph(factura.getEmpresa().getNombre(), empresaFont));
        empresaInfo.add(new Paragraph("CUIT: 27362932039"));
        empresaInfo.add(new Paragraph("Domicilio: Inclan 4261, Caba, Argentina"));
        empresaCell.addElement(empresaInfo);
        
        // Tipo de comprobante (centro)
        PdfPCell tipoCell = new PdfPCell();
        tipoCell.setBorder(Rectangle.BOX);
        tipoCell.setPadding(10);
        tipoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        Font tipoFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Paragraph tipoInfo = new Paragraph();
        String tipoComprobante = factura.getTipoComprobante() != null ? factura.getTipoComprobante() : "C";
        tipoInfo.add(new Paragraph(tipoComprobante, tipoFont));
        tipoInfo.add(new Paragraph("FACTURA"));
        tipoCell.addElement(tipoInfo);
        
        // ARCA (derecha)
        PdfPCell arcaCell = new PdfPCell();
        arcaCell.setBorder(Rectangle.BOX);
        arcaCell.setPadding(10);
        Font arcaFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font smallFont = new Font(Font.HELVETICA, 8);
        Paragraph arcaInfo = new Paragraph();
        
        String puntoVenta = factura.getPuntoVenta() != null ? 
            String.format("%04d", Integer.parseInt(factura.getPuntoVenta().getCodigo())) : "0001";
        String numeroComprobante = factura.getNumero() != null ? factura.getNumero() : "00000001";
        
        arcaInfo.add(new Paragraph("Punto de Venta: " + puntoVenta + " Comp. Nro: " + numeroComprobante, smallFont));
        arcaInfo.add(new Paragraph("Fecha de Vencimiento para el pago:", smallFont));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        arcaInfo.add(new Paragraph(sdf.format(factura.getFecha()), smallFont));
        arcaInfo.add(new Paragraph(" "));
        arcaInfo.add(new Paragraph("ORIGINAL", arcaFont));
        arcaCell.addElement(arcaInfo);
        
        headerTable.addCell(empresaCell);
        headerTable.addCell(tipoCell);
        headerTable.addCell(arcaCell);
        document.add(headerTable);
        
        document.add(new Paragraph(" "));
        
        // Datos del cliente en formato ARCA
        PdfPTable clienteTable = new PdfPTable(2);
        clienteTable.setWidthPercentage(100);
        clienteTable.setWidths(new float[]{1, 1});
        
        // Columna izquierda - datos del cliente
        PdfPCell clienteCell = new PdfPCell();
        clienteCell.setBorder(Rectangle.BOX);
        clienteCell.setPadding(8);
        Font labelFont = new Font(Font.HELVETICA, 8, Font.BOLD);
        Font dataFont = new Font(Font.HELVETICA, 9);
        
        Paragraph clienteInfo = new Paragraph();
        clienteInfo.add(new Paragraph("Periodo Facturado Desde: " + sdf.format(factura.getFecha()), dataFont));
        clienteInfo.add(new Paragraph("Hasta: " + sdf.format(factura.getFecha()), dataFont));
        clienteInfo.add(new Paragraph("Fecha de Vto. para el pago: " + sdf.format(factura.getFecha()), dataFont));
        clienteInfo.add(new Paragraph(" "));
        clienteInfo.add(new Paragraph("Senor/es: " + factura.getCliente().getNombre() + " " + factura.getCliente().getApellido(), dataFont));
        clienteInfo.add(new Paragraph("Domicilio: ", dataFont));
        clienteInfo.add(new Paragraph("Localidad: ", dataFont));
        clienteCell.addElement(clienteInfo);
        
        // Columna derecha - condiciones
        PdfPCell condicionesCell = new PdfPCell();
        condicionesCell.setBorder(Rectangle.BOX);
        condicionesCell.setPadding(8);
        
        Paragraph condicionesInfo = new Paragraph();
        condicionesInfo.add(new Paragraph("CUIT: " + factura.getCliente().getNumeroDocumento(), dataFont));
        condicionesInfo.add(new Paragraph("Condicion frente al IVA: Consumidor Final", dataFont));
        condicionesInfo.add(new Paragraph("Condicion de Venta: Contado", dataFont));
        condicionesCell.addElement(condicionesInfo);
        
        clienteTable.addCell(clienteCell);
        clienteTable.addCell(condicionesCell);
        document.add(clienteTable);
        
        document.add(new Paragraph(" "));
        
        // Tabla de items
        PdfPTable itemsTable = new PdfPTable(4);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{3, 1, 1, 1});
        
        // Headers
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell[] headers = {
            new PdfPCell(new Paragraph("DESCRIPCION", headerFont)),
            new PdfPCell(new Paragraph("CANTIDAD", headerFont)),
            new PdfPCell(new Paragraph("PRECIO UNIT.", headerFont)),
            new PdfPCell(new Paragraph("SUBTOTAL", headerFont))
        };
        
        for (PdfPCell header : headers) {
            header.setBorder(Rectangle.BOX);
            header.setPadding(5);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            itemsTable.addCell(header);
        }
        
        // Items reales de la factura
        Font itemFont = new Font(Font.HELVETICA, 9);
        if (factura.getItems() != null && !factura.getItems().isEmpty()) {
            for (org.openxava.negocio.model.ItemFacturaVenta item : factura.getItems()) {
                // Descripcion
                PdfPCell descCell = new PdfPCell(new Paragraph(
                    item.getProducto() != null ? item.getProducto().getNombre() : "Producto", itemFont));
                descCell.setBorder(Rectangle.BOX);
                descCell.setPadding(5);
                itemsTable.addCell(descCell);
                
                // Cantidad
                PdfPCell cantCell = new PdfPCell(new Paragraph(item.getCantidad().toString(), itemFont));
                cantCell.setBorder(Rectangle.BOX);
                cantCell.setPadding(5);
                cantCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                itemsTable.addCell(cantCell);
                
                // Precio unitario
                PdfPCell precioCell = new PdfPCell(new Paragraph("$" + item.getPrecio(), itemFont));
                precioCell.setBorder(Rectangle.BOX);
                precioCell.setPadding(5);
                precioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                itemsTable.addCell(precioCell);
                
                // Subtotal
                PdfPCell subTotalCell = new PdfPCell(new Paragraph("$" + item.getSubTotal(), itemFont));
                subTotalCell.setBorder(Rectangle.BOX);
                subTotalCell.setPadding(5);
                subTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                itemsTable.addCell(subTotalCell);
            }
        } else {
            // Si no hay items, mostrar fila vacia
            for (int i = 0; i < 4; i++) {
                PdfPCell emptyItemCell = new PdfPCell(new Paragraph("-", itemFont));
                emptyItemCell.setBorder(Rectangle.BOX);
                emptyItemCell.setPadding(5);
                itemsTable.addCell(emptyItemCell);
            }
        }
        
        document.add(itemsTable);
        
        document.add(new Paragraph(" "));
        
        // Totales en la parte inferior
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{3, 1});
        
        PdfPCell emptyCell = new PdfPCell(new Paragraph(""));
        emptyCell.setBorder(Rectangle.NO_BORDER);
        
        PdfPCell totalesCell = new PdfPCell();
        totalesCell.setBorder(Rectangle.BOX);
        totalesCell.setPadding(10);
        Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10);
        
        Paragraph totalesInfo = new Paragraph();
        totalesInfo.add(new Paragraph("Subtotal: $" + factura.getTotalSinDescuento(), normalFont));
        if (factura.getSenia() != null && factura.getSenia().compareTo(java.math.BigDecimal.ZERO) > 0) {
            totalesInfo.add(new Paragraph("Sena: $" + factura.getSenia(), normalFont));
            totalesInfo.add(new Paragraph("Saldo: $" + factura.getSaldo(), normalFont));
        }
        totalesInfo.add(new Paragraph("TOTAL: $" + factura.getTotal(), totalFont));
        totalesCell.addElement(totalesInfo);
        totalesCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        totalTable.addCell(emptyCell);
        totalTable.addCell(totalesCell);
        document.add(totalTable);
        
        // Agregar información CAE al final
        document.add(new Paragraph(" "));
        
        PdfPTable caeTable = new PdfPTable(1);
        caeTable.setWidthPercentage(100);
        
        PdfPCell caeCell = new PdfPCell();
        caeCell.setBorder(Rectangle.BOX);
        caeCell.setPadding(8);
        
        Font caeFont = new Font(Font.HELVETICA, 9, Font.BOLD);
        Font caeDataFont = new Font(Font.HELVETICA, 8);
        
        Paragraph caeInfo = new Paragraph();
        if (factura.getCae() != null && !factura.getCae().isEmpty()) {
            caeInfo.add(new Paragraph("CAE: " + factura.getCae(), caeFont));
            if (factura.getFechaVencimientoCae() != null) {
                caeInfo.add(new Paragraph("Fecha Venc. CAE: " + sdf.format(factura.getFechaVencimientoCae()), caeDataFont));
            }
        } else {
            caeInfo.add(new Paragraph("CAE: Sin CAE", caeFont));
        }
        caeInfo.add(new Paragraph("Comprobante autorizado por AFIP - Sistema de Facturación Electrónica", caeDataFont));
        
        caeCell.addElement(caeInfo);
        caeTable.addCell(caeCell);
        document.add(caeTable);
        
        document.close();
        
        // Guardar PDF en sesion y mostrar enlace
        String fileName = "factura_" + (factura.getNumero() != null ? factura.getNumero() : "SN") + ".pdf";
        
        getRequest().getSession().setAttribute("pdfData", baos.toByteArray());
        getRequest().getSession().setAttribute("pdfFileName", fileName);
        
        addMessage("PDF generado correctamente.");
    }
    
    @Override
    public String getForwardURI() {
        return "/downloadPDFFile.jsp";
    }
    
    @Override
    public boolean inNewWindow() {
        return true;
    }
}