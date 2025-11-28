package org.openxava.negocio.model;

public enum TipoComprobanteAfip {
    FACTURA_A("A", 1, "Factura A"),
    FACTURA_B("B", 6, "Factura B"), 
    FACTURA_C("C", 11, "Factura C"),
    FACTURA_E("E", 19, "Factura E"),
    NOTA_DEBITO_A("DA", 2, "Nota de Débito A"),
    NOTA_DEBITO_B("DB", 7, "Nota de Débito B"),
    NOTA_DEBITO_C("DC", 12, "Nota de Débito C"),
    NOTA_CREDITO_A("CA", 3, "Nota de Crédito A"),
    NOTA_CREDITO_B("CB", 8, "Nota de Crédito B"),
    NOTA_CREDITO_C("CC", 13, "Nota de Crédito C"),
    FACTURA_M("M", 51, "Factura M"),
    RECIBO_A("RA", 4, "Recibo A"),
    RECIBO_B("RB", 9, "Recibo B"),
    RECIBO_C("RC", 15, "Recibo C");

    private final String codigo;
    private final int codigoAfip;
    private final String descripcion;

    TipoComprobanteAfip(String codigo, int codigoAfip, String descripcion) {
        this.codigo = codigo;
        this.codigoAfip = codigoAfip;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getCodigoAfip() {
        return codigoAfip;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoComprobanteAfip porCodigo(String codigo) {
        for (TipoComprobanteAfip tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        return FACTURA_C; // Default
    }

    public static TipoComprobanteAfip porCodigoAfip(int codigoAfip) {
        for (TipoComprobanteAfip tipo : values()) {
            if (tipo.codigoAfip == codigoAfip) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código AFIP no encontrado: " + codigoAfip);
    }
}