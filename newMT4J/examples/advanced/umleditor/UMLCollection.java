package advanced.umleditor;

import java.util.Collection;
import java.util.LinkedList;

import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Persona;
import advanced.umleditor.logic.Relacion;

public class UMLCollection {

	
	
	private static Collection <ObjetoUML> objetosUML=new LinkedList<ObjetoUML>();
	
	
	
	
	
	public static synchronized ObjetoUML anadirObjeto(final int id, Persona p){
		ObjetoUML o=null;
		switch (id) {
		case ObjetoUML.ENTIDAD:
			o= new Entidad(p);
			break;
		case ObjetoUML.RELACION:	
			o= new Relacion(p);
			break;
		case ObjetoUML.DELETE_GESTURE:
			o= ObjetoUML.DELETE_OBJECT_GESTURE;
		default:			
			break;
		}
		if(o!=null){
			objetosUML.add(o);
			System.out.print("anadir");
			return o;			
		}
		return ObjetoUML.OBJETO_INVALIDO;
	}
	
	public static Collection <ObjetoUML> getListaUML(){
		return objetosUML;
	}

}
