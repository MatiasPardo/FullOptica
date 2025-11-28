package org.openxava.base.model;

import java.io.*;
import java.util.Properties;

import org.openxava.application.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;

public class ConfiguracionERP {
	
	public final static String EXTENSION_LOGO = ".png";
	
	public static final String LOGO_DEFAUL = "logo";

	public static final String USUARIO_QUARTZ = "SYSTEM";
	
	public static Properties properties = null;
	
	public static String esquemaDB(){
		if (Is.emptyString(Users.getCurrentUserInfo().getOrganization())){
			return "public";
		}
		else{
			return Users.getCurrentUserInfo().getOrganization();
		}
	}
	
	public static String pathConfigAplicacionDefault(){
		String userHome = System.getProperty("user.home");
		return userHome + File.separator + "ConfigFEArg" + File.separator + "FullOptica" + File.separator;
	}
	
	public static String pathConfig(){
		// FullOptica usa una sola empresa, ruta genérica multiplataforma
		return pathConfigAplicacionDefault();
	}
	
	public static String pathConfig(String organization){
		return pathConfig();
	}

	public static String fullFileNameReporte(String nombreReporte){
		return ConfiguracionERP.fullFileNameReporte(nombreReporte,ConfiguracionERP.pathConfig());	
	}
	
	public static String fullFileNameReporte(String nombreReporte, String path){
		String fileName = path.concat(nombreReporte);
		File file = new File(fileName);
		if (file.exists()){
			return fileName;
		}
		else{
			throw new ValidationException("No se encontró el archivo: " + fileName);
		}
	}
	
	public static String pathQuartz(String nombreFile, String organization, String pathTree){
		String userHome = System.getProperty("user.home");
		return userHome + File.separator + "ConfigFEArg" + File.separator + organization + File.separator + pathTree + File.separator + nombreFile;
	}

}