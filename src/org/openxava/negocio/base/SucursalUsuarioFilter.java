package org.openxava.negocio.base;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openxava.filters.FilterException;
import org.openxava.filters.IFilter;
import org.openxava.negocio.model.Sucursal;
import org.openxava.util.Users;

import com.openxava.naviox.model.User;

@SuppressWarnings("serial")
public class SucursalUsuarioFilter implements IFilter {

    public final static String BASECONDITION_USUARIO = "(? member of e.sucursal.usuarios)";
    public final static String BASECONDITION_FACTURASUCURSAL = BASECONDITION_USUARIO;
	
    private User usuario = null;

    @Override
    public Object filter(Object o) throws FilterException {
    	Sucursal sucursal = Sucursal.buscarPrimerSucursalHabilitada(Users.getCurrent());
    	this.usuario = null;
		User usuario = getUsuario();	
		if (o == null) {
			return new Object [] { usuario };
        } else if (o instanceof Object[]) {
            List<Object> c = new ArrayList<>(Arrays.asList((Object[]) o));
            c.add(0, sucursal.getUsuarios());
            return c.toArray();
        } else {
            return new Object[]{sucursal.getUsuarios(), o};
        }
    }

	public User getUsuario() {
		if (this.usuario == null){
			this.usuario = User.find(Users.getCurrent()); 
		}
		return usuario;
	}
}

