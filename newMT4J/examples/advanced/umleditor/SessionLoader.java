package advanced.umleditor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SessionLoader{

public static boolean verifySessionJSON(String jsonfilename){
  Calendar cal = Calendar.getInstance();
  JSONParser parser = new JSONParser();

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  String time=sdf.format(cal.getTime());

  try {

			    JSONObject sesion = (JSONObject) parser.parse(new FileReader(jsonfilename));
			    
			    JSONObject metadata = (JSONObject) sesion.get("metadata");
			    System.out.println(metadata);
	
			    JSONArray datos = (JSONArray) sesion.get("datos");
	
			    for (Object dato : datos)
			    {
			    	JSONObject accion = (JSONObject)dato;
			    	String nombreAccion = (String)accion.get("accion");
			    	JSONObject propiedades = (JSONObject)accion.get("propiedades");
			    	
			    	long object_id = (long)propiedades.get("id");
		    		String tipo =  (String)propiedades.get("tipo");
		    		double width = (double)propiedades.get("width");
		    		double height = (double)propiedades.get("height");
		    		String tiempo = (String)accion.get("tiempo");
		    		long usuario = (long)accion.get("usuario");
		    		validarObjetoUMLJSON(propiedades,tipo);
			   }
		  
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	  
	return true;

}
static boolean validarObjetoUMLJSON(JSONObject jsonObj, String tipo) throws ParseException{
	
	if (tipo.equals("Entidad")){
		String nombreEntidad = (String)jsonObj.get("nombre");
		JSONArray atributosEntidad  = (JSONArray)jsonObj.get("atributos");

	    for (Object dato : atributosEntidad)
	    {
	    	JSONObject atributo = (JSONObject)dato;
	    	long indice = (long)atributo.get("indice");
	    	String nombre_atributo = (String)atributo.get("nombre_atributo");
	    }
	    
	    JSONArray posicion = (JSONArray)jsonObj.get("posicion");
		double xCoord = (double)posicion.get(0);
		double yCoord = (double)posicion.get(1);
		double zCoord = (double)posicion.get(2);
		double wXoord = (double)posicion.get(3);
	
		return true; 
	
	}else if(tipo.equals("Relacion")){
		
		String cardinalidadInicio = (String)jsonObj.get("cardinalidadInicio");
		String cardinalidadFin = (String)jsonObj.get("cardinalidadFin");
		String textoInicio = (String)jsonObj.get("textoInicio");
		String textoFin = (String)jsonObj.get("textoFin");
		JSONArray posicionInicio = (JSONArray)jsonObj.get("posicionInicio");
		double xCoordIni = (double)posicionInicio.get(0);
		double yCoordIni = (double)posicionInicio.get(1);
		double zCoordIni = (double)posicionInicio.get(2);
		double wXoordIni = (double)posicionInicio.get(3);
		
		JSONArray posicionFin = (JSONArray)jsonObj.get("posicionInicio");
		double xCoordFin = (double)posicionFin.get(0);
		double yCoordFin = (double)posicionFin.get(1);
		double zCoordFin = (double)posicionFin.get(2);
		double wXoordFin = (double)posicionFin.get(3);		
		
		
		return true;
	}	

	
	return false;
}
	
	
}