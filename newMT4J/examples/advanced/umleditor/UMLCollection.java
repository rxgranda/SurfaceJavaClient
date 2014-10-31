package advanced.umleditor;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.TextoFlotante;
import advanced.umleditor.logic.Usuario;
import advanced.umleditor.logic.Relacion;

public class UMLCollection {

	
	
	private static HashMap <Integer, ObjetoUML> objetosUML= new HashMap<Integer, ObjetoUML>();

	
	
	
	
	public static synchronized ObjetoUML anadirObjeto(final int id, Usuario p){
		ObjetoUML o=null;
		switch (id) {
		case ObjetoUML.ENTIDAD:
			o= new Entidad(p);
			break;
		case ObjetoUML.RELACION:	
			o= new Relacion(p);
			break;
		case ObjetoUML.TEXTOFLOTANTE:	
			o= new TextoFlotante(p);
			break;			
		case ObjetoUML.DELETE_GESTURE:
			o= ObjetoUML.DELETE_OBJECT_GESTURE;
		default:			
			break;
		}
		if(o!=null){
			objetosUML.put(o.getId(),o);
			//System.out.print("anadir");
			return o;			
		}
		return ObjetoUML.OBJETO_INVALIDO;
	}
	
	public static HashMap <Integer, ObjetoUML>  getListaUML(){
		return objetosUML;
	}

}
