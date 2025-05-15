package org.openxava.model;

import java.util.EnumSet;
import java.util.Set;

public enum Estado {
	Borrador(false), Confirmada(true), Anulada(true), Procesando(true), Abierta(false), Cancelada(true);
	
	private static final Set<Estado> ESTADOS_FINALES = EnumSet.of(Confirmada, Anulada, Cancelada);

	private boolean soloLectura;
	
	public boolean isSoloLectura() {
		return soloLectura;
	}

	Estado(boolean soloLectura){
		this.soloLectura = soloLectura;
	}

	public static boolean isFinal(Estado estado) {
		return ESTADOS_FINALES.contains(estado);
	}
}