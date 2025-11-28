package org.openxava.negocio.base.adapters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openxava.negocio.model.FacturaVenta;

public class ARCAWebServiceAdapter {
    
    private static final String API_BASE_URL = "http://localhost:3000";
    
    public static class CAEResponse {
        private boolean success;
        private String cae;
        private Date fechaVencimiento;
        private String error;
        
        public CAEResponse(boolean success, String cae, Date fechaVencimiento, String error) {
            this.success = success;
            this.cae = cae;
            this.fechaVencimiento = fechaVencimiento;
            this.error = error;
        }
        
        public boolean isSuccess() { return success; }
        public String getCae() { return cae; }
        public Date getFechaVencimiento() { return fechaVencimiento; }
        public String getError() { return error; }
    }
    
    public static class UltimoComprobanteResponse {
        private boolean success;
        private String ultimoNumero;
        private String error;
        
        public UltimoComprobanteResponse(boolean success, String ultimoNumero, String error) {
            this.success = success;
            this.ultimoNumero = ultimoNumero;
            this.error = error;
        }
        
        public boolean isSuccess() { return success; }
        public String getUltimoNumero() { return ultimoNumero; }
        public String getError() { return error; }
    }
    
    public static CAEResponse solicitarCAE(FacturaVenta factura) throws Exception {
        try {
            URL url = new URL(API_BASE_URL + "/api/facturacion/generar");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // Crear JSON con datos de la factura
            String jsonRequest = crearJsonRequest(factura);
            
            // Enviar request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            // Leer response
            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            // Leer respuesta (exitosa o de error)
            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            }
            
            try {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            } finally {
                br.close();
            }
            
            if (responseCode == 200) {
                return parseResponse(response.toString(), null);
            } else {
                return parseErrorResponse(response.toString(), responseCode, jsonRequest);
            }
            
        } catch (Exception e) {
            throw new Exception("Error conectando con API ARCA: " + e.getMessage());
        }
    }
    
    public static UltimoComprobanteResponse consultarUltimoComprobante(String puntoVenta, String tipoComprobante) throws Exception {
        try {
            int tipoAfip = convertirTipoComprobante(tipoComprobante);
            String urlStr = API_BASE_URL + "/api/consultas/ultimo-comprobante?puntoVenta=" + puntoVenta + "&tipoComprobante=" + tipoAfip;
            
            System.out.println("[ARCAAdapter] Consultando último comprobante: " + urlStr);
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            
            // Leer response
            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            }
            
            try {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            } finally {
                br.close();
            }
            
            System.out.println("[ARCAAdapter] Respuesta último comprobante: " + response.toString());
            
            if (responseCode == 200) {
                return parseUltimoComprobanteResponse(response.toString());
            } else {
                String error = "Error HTTP " + responseCode + ": " + response.toString();
                return new UltimoComprobanteResponse(false, null, error);
            }
            
        } catch (Exception e) {
            System.err.println("[ARCAAdapter] Error consultando último comprobante: " + e.getMessage());
            throw new Exception("Error conectando con API ARCA: " + e.getMessage());
        }
    }
    
    private static UltimoComprobanteResponse parseUltimoComprobanteResponse(String jsonResponse) {
        try {
            // Si la respuesta es solo un número (sin JSON), tratarla como exitosa
            if (jsonResponse.trim().matches("^\\d+$")) {
                return new UltimoComprobanteResponse(true, jsonResponse.trim(), null);
            }
            
            // Si es JSON, intentar parsear normalmente
            String success = extraerValor(jsonResponse, "success");
            
            if ("true".equals(success)) {
                String ultimoNumero = extraerValor(jsonResponse, "ultimoNumero");
                if (ultimoNumero == null) {
                    ultimoNumero = extraerValor(jsonResponse, "numero");
                }
                return new UltimoComprobanteResponse(true, ultimoNumero, null);
            } else {
                String error = extraerValor(jsonResponse, "mensaje");
                if (error == null) {
                    error = extraerValor(jsonResponse, "message");
                }
                if (error == null) {
                    error = "Respuesta AFIP: " + jsonResponse;
                }
                return new UltimoComprobanteResponse(false, null, error);
            }
            
        } catch (Exception e) {
            return new UltimoComprobanteResponse(false, null, "Error parseando respuesta: " + e.getMessage());
        }
    }
    
    private static String crearJsonRequest(FacturaVenta factura) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String numeroSinPrefijo = extraerNumeroSinPrefijo(factura.getNumero());
        
        // Calcular IVA basado en si es monotributista
        BigDecimal importeNeto = factura.getTotal();
        BigDecimal importeIVA = BigDecimal.ZERO;
        
        if (!factura.getEmpresa().esMonotributista()) {
            // Para responsables inscriptos: calcular IVA 21%
            importeNeto = factura.getTotal().divide(new BigDecimal("1.21"), 2, RoundingMode.HALF_UP);
            importeIVA = factura.getTotal().subtract(importeNeto);
        }
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"puntoVenta\":").append(factura.getPuntoVenta().getCodigo()).append(",");
        json.append("\"tipoComprobante\":").append(convertirTipoComprobante(factura.getTipoComprobante())).append(",");
        json.append("\"numeroComprobante\":").append(numeroSinPrefijo).append(",");
        json.append("\"importeTotal\":").append(factura.getTotal()).append(",");
        json.append("\"importeNeto\":").append(importeNeto).append(",");
        json.append("\"importeIVA\":").append(importeIVA).append(",");
        json.append("\"fechaComprobante\":\"").append(sdf.format(factura.getFecha())).append("\"");
        
        // Agregar datos del cliente
        if (factura.getCliente().getCuit() != null && !factura.getCliente().getCuit().trim().isEmpty()) {
            json.append(",\"numeroDocumento\":\"").append(factura.getCliente().getCuit().trim()).append("\"");
            json.append(",\"tipoDocumento\":80"); // CUIT
        } else if (factura.getCliente().getNumeroDocumento() != null && !factura.getCliente().getNumeroDocumento().trim().isEmpty()) {
            json.append(",\"numeroDocumento\":\"").append(factura.getCliente().getNumeroDocumento().trim()).append("\"");
            json.append(",\"tipoDocumento\":96"); // DNI
        } else {
            json.append(",\"tipoDocumento\":99"); // sin nro
        }
        
        // Agregar denominacion del receptor (siempre)
        String denominacion = (factura.getCliente().getApellido() != null ? factura.getCliente().getApellido() : "") + 
                             (factura.getCliente().getNombre() != null ? " " + factura.getCliente().getNombre() : "");
        json.append(",\"denominacionReceptor\":\"").append(denominacion.trim()).append("\"");
        
        json.append("}");
        
        return json.toString();
    }
    
    private static String extraerNumeroSinPrefijo(String numeroCompleto) {
        if (numeroCompleto == null || numeroCompleto.isEmpty()) {
            return "1";
        }
        
        // Si tiene formato "0001-00000123", extraer la parte despues del guion
        int guionIndex = numeroCompleto.lastIndexOf("-");
        String numero;
        if (guionIndex != -1 && guionIndex < numeroCompleto.length() - 1) {
            numero = numeroCompleto.substring(guionIndex + 1);
        } else {
            numero = numeroCompleto;
        }
        
        // Remover ceros a la izquierda
        try {
            return String.valueOf(Integer.parseInt(numero));
        } catch (NumberFormatException e) {
            return numero; // Si no es numerico, devolver tal como esta
        }
    }
    
    private static int convertirTipoComprobante(String tipo) {
        switch (tipo.toUpperCase()) {
            case "A": return 1;
            case "B": return 6;
            case "C": return 11;
            default: return 6; // Por defecto Factura B
        }
    }
    
    private static CAEResponse parseResponse(String jsonResponse, String jsonRequest) {
        try {
            System.out.println("[ARCAAdapter] Parseando respuesta: " + jsonResponse);
            
            // Verificar si es exitoso - probar ambos campos
            String success = extraerValor(jsonResponse, "success");
            String exitoso = extraerValor(jsonResponse, "exitoso");
            
            if ("true".equals(success) || "true".equals(exitoso)) {
                // Buscar CAE en diferentes campos posibles
                String cae = extraerValor(jsonResponse, "cae");
                if (cae == null) {
                    cae = extraerValor(jsonResponse, "numero");
                }
                
                String fechaVencStr = extraerValor(jsonResponse, "fechaVencimiento");
                
                System.out.println("[ARCAAdapter] CAE extraido: " + cae);
                System.out.println("[ARCAAdapter] Fecha vencimiento: " + fechaVencStr);
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaVencimiento = sdf.parse(fechaVencStr);
                
                return new CAEResponse(true, cae, fechaVencimiento, null);
            } else {
                // Loggear request y respuesta completa para debug
                System.err.println("[ARCAAdapter] Error AFIP - Request enviado: " + jsonRequest);
                System.err.println("[ARCAAdapter] Error AFIP - Respuesta completa: " + jsonResponse);
                
                // Extraer mensaje de error de AFIP
                String error = extraerValor(jsonResponse, "mensaje");
                if (error == null) {
                    error = extraerValor(jsonResponse, "message");
                }
                if (error == null) {
                    error = extraerValor(jsonResponse, "error");
                }
                
                // Si no hay mensaje específico, mostrar respuesta completa
                if (error == null || error.trim().isEmpty()) {
                    error = "Error AFIP: " + jsonResponse;
                } else {
                    // Agregar contexto de AFIP al mensaje
                    error = "Error AFIP: " + error;
                }
                
                return new CAEResponse(false, null, null, error);
            }
            
        } catch (Exception e) {
            System.err.println("[ARCAAdapter] Error parseando respuesta AFIP - Request: " + jsonRequest);
            System.err.println("[ARCAAdapter] Error parseando respuesta AFIP - Response: " + jsonResponse);
            e.printStackTrace();
            return new CAEResponse(false, null, null, "Error parseando respuesta: " + e.getMessage());
        }
    }
    
    private static CAEResponse parseErrorResponse(String jsonResponse, int responseCode, String jsonRequest) {
        try {
            // Loggear request y error HTTP completo
            System.err.println("[ARCAAdapter] Error HTTP AFIP (" + responseCode + ") - Request: " + jsonRequest);
            System.err.println("[ARCAAdapter] Error HTTP AFIP (" + responseCode + ") - Response: " + jsonResponse);
            
            // Intentar extraer mensaje de error del JSON
            String mensaje = extraerValor(jsonResponse, "mensaje");
            if (mensaje == null) {
                mensaje = extraerValor(jsonResponse, "error");
            }
            if (mensaje == null) {
                mensaje = extraerValor(jsonResponse, "message");
            }
            
            String errorCompleto;
            if (mensaje != null && !mensaje.trim().isEmpty()) {
                errorCompleto = "Error AFIP (" + responseCode + "): " + mensaje;
            } else {
                // Si no hay mensaje específico, mostrar respuesta completa
                errorCompleto = "Error AFIP HTTP " + responseCode + ": " + jsonResponse;
            }
            
            return new CAEResponse(false, null, null, errorCompleto);
            
        } catch (Exception e) {
            return new CAEResponse(false, null, null, "Error HTTP " + responseCode + ": " + jsonResponse);
        }
    }
    
    private static String extraerValor(String json, String campo) {
        // Buscar patron con comillas para strings
        String patron = "\"" + campo + "\":\"";
        int inicio = json.indexOf(patron);
        if (inicio != -1) {
            inicio += patron.length();
            int fin = json.indexOf("\"", inicio);
            if (fin != -1) {
                return json.substring(inicio, fin);
            }
        }
        
        // Buscar patron sin comillas para booleanos
        patron = "\"" + campo + "\":";
        inicio = json.indexOf(patron);
        if (inicio != -1) {
            inicio += patron.length();
            int fin = json.indexOf(",", inicio);
            if (fin == -1) {
                fin = json.indexOf("}", inicio);
            }
            if (fin != -1) {
                return json.substring(inicio, fin).trim();
            }
        }
        
        return null;
    }
}