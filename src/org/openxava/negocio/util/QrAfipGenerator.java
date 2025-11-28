package org.openxava.negocio.util;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.net.URLEncoder;

import org.openxava.negocio.model.FacturaVenta;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class QrAfipGenerator {
    
    public static String generarUrlQrAfip(FacturaVenta factura) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("ver", 1);
            json.addProperty("fecha", new SimpleDateFormat("yyyy-MM-dd").format(factura.getFecha()));
            json.addProperty("cuit", Long.parseLong(factura.getEmpresa().getCuit().replaceAll("[^0-9]", "")));
            json.addProperty("ptoVta", Integer.parseInt(factura.getPuntoVenta().getCodigo()));
            json.addProperty("tipoCmp", factura.getCodigoAfip());
            
            // Extraer solo el número después del guión
            String numeroCompleto = factura.getNumero() != null ? factura.getNumero() : "0001-00000001";
            String[] partes = numeroCompleto.split("-");
            String numeroComprobante = partes.length > 1 ? partes[1] : numeroCompleto.replaceAll("[^0-9]", "");
            json.addProperty("nroCmp", Long.parseLong(numeroComprobante));
            
            json.addProperty("importe", factura.getTotal().doubleValue());
            json.addProperty("moneda", "PES");
            json.addProperty("ctz", 1);
            json.addProperty("tipoDocRec", 80);
            
            String cuitCliente = factura.getCliente().getCuit() != null ? 
                factura.getCliente().getCuit().replaceAll("[^0-9]", "") : "0";
            json.addProperty("nroDocRec", Long.parseLong(cuitCliente));
            
            json.addProperty("tipoCodAut", "E");
            String cae = factura.getCae() != null && !factura.getCae().isEmpty() ? factura.getCae() : "75459264892191";
            json.addProperty("codAut", Long.parseLong(cae));
            
            String jsonString = new Gson().toJson(json);
            String jsonBase64 = Base64.getEncoder().encodeToString(jsonString.getBytes("UTF-8"));
            
            return "https://www.afip.gob.ar/fe/qr/?p=" + jsonBase64;
            
        } catch (Exception e) {
            System.err.println("Error generando QR AFIP: " + e.getMessage());
            return "https://www.afip.gob.ar";
        }
    }
    
    public static byte[] generarImagenQr(String url) {
        try {
            // Usar servicio externo para generar QR
            String qrServiceUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + 
                URLEncoder.encode(url, "UTF-8");
            
            java.net.URL qrUrl = new java.net.URL(qrServiceUrl);
            java.io.InputStream is = qrUrl.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            is.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            System.err.println("Error generando imagen QR: " + e.getMessage());
            return null;
        }
    }
}