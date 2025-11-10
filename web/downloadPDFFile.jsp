<%@ page contentType="application/pdf" %>
<%@ page import="java.io.*" %>
<%
    byte[] pdfData = (byte[]) session.getAttribute("pdfData");
    String fileName = (String) session.getAttribute("pdfFileName");
    
    if (pdfData != null && fileName != null) {
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setHeader("Content-Length", String.valueOf(pdfData.length));
            response.getOutputStream().write(pdfData);
            response.getOutputStream().flush();
            
            // Limpiar sesión
            session.removeAttribute("pdfData");
            session.removeAttribute("pdfFileName");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    } else {
        out.println("No hay PDF disponible para descargar.");
    }
%>