<%@ page contentType="application/pdf" %>
<%@ page import="java.io.*" %>
<%
    byte[] pdfData = (byte[]) session.getAttribute("pdfData");
    String fileName = (String) session.getAttribute("pdfFileName");
    
    if (pdfData != null && fileName != null) {
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(pdfData.length));
        
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(pdfData);
        outputStream.flush();
        outputStream.close();
        
        // Limpiar sesión
        session.removeAttribute("pdfData");
        session.removeAttribute("pdfFileName");
        
        out.clear();
        out = pageContext.pushBody();
    }
%>