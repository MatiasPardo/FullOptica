package org.openxava.negocio.model;

import javax.persistence.Entity;

import org.openxava.negocio.base.BasicBusiness;

@Entity
public class Numerador extends BasicBusiness{
	
	private String prefijo;
	
	private Integer cantidadDigitos;
	
	private Long proximoNumero;
	
/*	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="nombre")
	@NoCreate @NoModify
	private Empresa empresa;
*/
	public String getPrefijo() {
		return prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	public void setCantidadDigitos(Integer cantidadDigitos) {
		this.cantidadDigitos = cantidadDigitos;
	}
	
	public void numberBasicBusiness(BasicBusiness objeto){
		Long numero = this.getProximoNumero();
		Long proxNro = objeto.asignarNumeracion(this.formatearNumero(numero), numero);
		if (proxNro <= numero){
	    	// el próximo numero no puede ser menor o igual al numero actual
	    	this.setProximoNumero(numero + 1);
	    }
	    else{
	    	this.setProximoNumero(proxNro);
	    }
		
	}

	public String formatearNumero(Long numero) {
		String numeroStr = numero.toString();
		
		String numeroFormateado = this.getPrefijo();
		int i = 0;
		while (i < (this.getCantidadDigitos() - numeroStr.length())){
			numeroFormateado += "0";
			i++;
		}
		numeroFormateado += numeroStr; 
		return numeroFormateado;
	}
	
	public Integer getCantidadDigitos() {
		return cantidadDigitos == null ? 0 : cantidadDigitos;
	}
	
	public Long getProximoNumero() {
		return proximoNumero;
	}

	public void setProximoNumero(Long proximoNumero) {
		this.proximoNumero = proximoNumero;
	}

}
