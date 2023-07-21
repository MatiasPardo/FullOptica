package org.openxava.negocio.model;

import javax.persistence.Entity;

import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.negocio.base.BasicBusiness;
@Views({
	@View(name = "simple", members = "codigo, nombre;")
})
@Entity
public class Producto extends BasicBusiness{

}
