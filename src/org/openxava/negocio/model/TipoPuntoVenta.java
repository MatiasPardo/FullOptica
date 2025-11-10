package org.openxava.negocio.model;

public enum TipoPuntoVenta {
	Manual(false), Electronico(false), ExportacionManual(true), ExportacionElectronico(true);
	
	private boolean exportacion;
	
	public boolean exportacion(){
		return this.exportacion;
	}
	
	TipoPuntoVenta(boolean exportacion){
		this.exportacion = exportacion;
	}
}