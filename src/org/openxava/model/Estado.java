package org.openxava.model;

public enum Estado {
	Borrador(false), Confirmada(true), Anulada(true), Procesando(true), Abierta(false), Cancelada(true);
	
	private boolean soloLectura;
	
	public boolean isSoloLectura() {
		return soloLectura;
	}

	Estado(boolean soloLectura){
		this.soloLectura = soloLectura;
	}
}