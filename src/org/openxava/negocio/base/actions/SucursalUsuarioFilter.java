package org.openxava.negocio.base.actions;



import java.util.List;

import org.openxava.filters.FilterException;
import org.openxava.filters.IFilter;
import org.openxava.negocio.model.Sucursal;
import org.openxava.util.Users;

import com.openxava.naviox.model.User;

@SuppressWarnings("serial")
public class SucursalUsuarioFilter implements IFilter {

    public final static String BASECONDITION_USUARIO = "e.sucursal in (?)";
    public final static String BASECONDITION_FACTURASUCURSAL = BASECONDITION_USUARIO;
	
    private User usuario = null;

    
    @Override
    public Object filter(Object o) throws FilterException {
        List<Sucursal> sucursales = Sucursal.buscarSucursalesHabilitada(Users.getCurrent());

        // DEBUG para verificar el contenido
        System.out.println("DEBUG - Sucursales permitidas:");
        for (Sucursal s : sucursales) {
            System.out.println(" - " + s.getCodigo() + " / " + s.getNombre());
        }

        if (o == null) {
            System.out.println("DEBUG - Filtro: null -> Enviando sólo sucursales");
            return new Object[] { sucursales };
        } 
        else if (o instanceof Object[]) {
            Object[] filtrosAdicionales = (Object[]) o;

            System.out.println("DEBUG - Filtro es Object[] con longitud " + filtrosAdicionales.length);
            for (int i = 0; i < filtrosAdicionales.length; i++) {
                System.out.println(" - filtro[" + i + "] = " + filtrosAdicionales[i] + " (" + 
                    (filtrosAdicionales[i] != null ? filtrosAdicionales[i].getClass().getName() : "null") + ")");
            }

            Object[] resultado = new Object[1 + filtrosAdicionales.length];
            resultado[0] = sucursales;
            System.arraycopy(filtrosAdicionales, 0, resultado, 1, filtrosAdicionales.length);

            System.out.println("DEBUG - Resultado final:");
            for (int i = 0; i < resultado.length; i++) {
                System.out.println(" - param[" + i + "] = " + resultado[i] + " (" + 
                    (resultado[i] != null ? resultado[i].getClass().getName() : "null") + ")");
            }

            return resultado;
        } 
        else {
            System.out.println("DEBUG - Filtro simple = " + o + " (" + o.getClass().getName() + ")");
            return new Object[] { sucursales, o };
        }
    }

}

