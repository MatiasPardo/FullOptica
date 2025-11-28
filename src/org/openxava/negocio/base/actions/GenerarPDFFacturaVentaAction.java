package org.openxava.negocio.base.actions;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.openxava.actions.ViewBaseAction;
import org.openxava.actions.IForwardAction;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.openxava.model.Estado;
import org.openxava.negocio.model.FacturaVenta;
import org.openxava.negocio.model.ItemFacturaVenta;
import org.openxava.negocio.util.QrAfipGenerator;
import org.openxava.jpa.XPersistence;

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
        // Parámetros
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("facturaId", factura.getId());
        
        // Generar QR
        try {
            String urlQr = QrAfipGenerator.generarUrlQrAfip(factura);
            byte[] qrBytes = QrAfipGenerator.generarImagenQr(urlQr);
            if (qrBytes != null) {
                BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(qrBytes));
                parameters.put("qrImage", qrImage);
            }
        } catch (Exception e) {
            System.err.println("Error generando QR: " + e.getMessage());
        }

        // Cargar reporte desde classpath
        InputStream reportStream = getClass().getResourceAsStream("/reports/FacturaVenta.jrxml");
        if (reportStream == null) {
            throw new Exception("No se pudo encontrar el archivo de reporte: /reports/FacturaVenta.jrxml");
        }
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Preparar parámetros con datos reales
        parameters.put("number", factura.getNumero());
        // Extraer solo el número sin el prefijo del punto de venta
        String numeroSinPrefijo = factura.getNumero();
        if (numeroSinPrefijo != null && numeroSinPrefijo.contains("-")) {
            numeroSinPrefijo = numeroSinPrefijo.substring(numeroSinPrefijo.lastIndexOf("-") + 1);
        }
        parameters.put("numeroSinPrefijo", numeroSinPrefijo);
        parameters.put("date", new java.text.SimpleDateFormat("dd/MM/yyyy").format(factura.getFecha()));
        parameters.put("customer", factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
        parameters.put("address", factura.getCliente().getDomicilio().getCalle() + " " + factura.getCliente().getDomicilio().getNumero());
        parameters.put("clienteCondicionIva", factura.getCliente().getPosicionIva().getDescripcion());
        parameters.put("customerNombre", factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
        parameters.put("customerAddress", factura.getCliente().getDomicilio().getCalle() + " " + factura.getCliente().getDomicilio().getNumero());
        parameters.put("docNro", factura.getCliente().getCuit() != null ? factura.getCliente().getCuit() : "");
        parameters.put("medioDePago", factura.getMedioDePago() != null ? factura.getMedioDePago().getNombre() : "Contado");
        parameters.put("cae", factura.getCae() != null ? factura.getCae() : "12345678901234");
        parameters.put("dateCae", factura.getFechaVencimientoCae() != null ? 
            new SimpleDateFormat("dd/MM/yyyy").format(factura.getFechaVencimientoCae()) : 
            new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        parameters.put("sum", factura.getTotalSinDescuento());
        parameters.put("vat", factura.getTotal().subtract(factura.getTotalSinDescuento()));
        parameters.put("total", factura.getTotal());
        parameters.put("tipoComprobante", factura.getTipoComprobante());
        parameters.put("codigoAfip", factura.getCodigoAfip());
        parameters.put("empresaCuit", factura.getEmpresa().getCuit());
        parameters.put("empresaIngresosBrutos", factura.getEmpresa().getIngresosBrutos());
        parameters.put("empresaFechaInicio", factura.getEmpresa().getFechaInicioActividades() != null ? 
            new SimpleDateFormat("dd/MM/yyyy").format(factura.getEmpresa().getFechaInicioActividades()) : "");
        parameters.put("empresaRazonSocial", factura.getEmpresa().getRazonSocial());
        parameters.put("empresaDomicilio", factura.getEmpresa().getDireccion());
        parameters.put("empresaCondicionIva", factura.getEmpresa().getCondicionIva());
        parameters.put("puntoVenta", factura.getPuntoVenta().getCodigo());
        parameters.put("cae", factura.getCae());
        parameters.put("dateCae", new SimpleDateFormat("dd/MM/yyyy").format(factura.getFechaVencimientoCae()));
        
        // Crear datasource con items de la factura
        List<Map<String, Object>> itemsData = new ArrayList<>();
        for (ItemFacturaVenta item : factura.getItems()) {
            System.out.println("Procesando item ID: " + item.getId());
            
            // Calcular importe bonificado
            BigDecimal porcentajeDescuento = item.getDescuento() != null ? item.getDescuento() : BigDecimal.ZERO;
            BigDecimal importeBonificado = item.getPrecio().multiply(item.getCantidad()).multiply(porcentajeDescuento).divide(BigDecimal.valueOf(100));
            
            Map<String, Object> itemMap = new java.util.HashMap<>();
            itemMap.put("quantity", item.getCantidad().intValue());
            itemMap.put("productDescription", item.getProducto().getNombre());
            itemMap.put("unitPrice", item.getPrecio());
            itemMap.put("amount", item.getSubTotal());
            itemMap.put("desc", porcentajeDescuento);
            itemMap.put("discount", importeBonificado);
            System.out.println("ItemMap creado: " + itemMap);
            itemsData.add(itemMap);
        }
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itemsData);

        // Generar PDF
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

        // Guardar en sesión
        getRequest().getSession().setAttribute("pdfData", baos.toByteArray());
        getRequest().getSession().setAttribute("pdfFileName", "factura-" + factura.getNumero() + ".pdf");

        addMessage("PDF generado correctamente");
    }

    @Override
    public String getForwardURI() {
        return "/downloadPDFFile.jsp";
    }

    @Override
    public boolean inNewWindow() {
        return false;
    }
}