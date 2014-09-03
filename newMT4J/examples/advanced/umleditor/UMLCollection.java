package advanced.umleditor;

import java.util.Collection;
import java.util.LinkedList;

import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Persona;
import advanced.umleditor.logic.Relacion;

public class UMLCollection {
	public static final int  INVALIDO=-1;
	public static final int  ENTIDAD=1;
	public static final int  RELACION=2;
	
	
	private static Collection <ObjetoUML> objetosUML=new LinkedList<ObjetoUML>();;
	
	
	
	public static synchronized void anadirObjeto(final int id, Persona p){
		ObjetoUML o=null;
		switch (id) {
		case ENTIDAD:
			o= new Entidad(p);
			break;
		case RELACION:	
			o= new Relacion(p);
			break;
		default:			
			break;
		}
		if(o!=null){
			objetosUML.add(o);
			System.out.print("anadir");
			
		}
	}
	
	public static Collection <ObjetoUML> getListaUML(){
		return objetosUML;
	}

}
