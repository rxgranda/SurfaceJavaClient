package advanced.drawing;


import java.util.Stack;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

import advanced.umleditor.UMLFacade;
import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.Usuario;

public class UndoHelper {
	public static final int MAX_NUMBER_UNDO_ACCTIONS=5;
	public static final int AGREGAR_OBJETO_ACTION=1;
	public static final int EDITAR_OBJETO_ACTION=2;
	public static final int BORRAR_OBJETO_ACTION=3;
	public static final int MOVER_OBJETO_ACTION=4;
	
	
	
	static Stack<UndoAction> undoStack = new Stack<UndoAction>(){
	    private static final long serialVersionUID = 1L;
	    public UndoAction push(UndoAction item) {
	        if (this.size() == MAX_NUMBER_UNDO_ACCTIONS ) {
	            this.removeElementAt(0);
	        }
	        return super.push(item);
	    }
	};
	
	public static synchronized void deshacerAccion(){
		UndoAction accion;
		//System.out.println("llamar deshacer");
		try {
			synchronized (undoStack) {				
				accion=undoStack.pop();						
			}
			ObjetoUML objetoAnterior=UMLFacade.getObjetoUML(accion.idObjeto);
			synchronized (objetoAnterior) {
				switch (accion.tipoAcccion) {
				case AGREGAR_OBJETO_ACTION:	
					objetoAnterior.setBorrado(true);
					objetoAnterior.getFigura().undoAddActions();
					// Hacer invisible el MTComponent
					break;
				case EDITAR_OBJETO_ACTION:
					
						objetoAnterior.restaurar(accion.objeto);
					
					break;
				case BORRAR_OBJETO_ACTION:			
					objetoAnterior.getFigura().undoDeleteActions();
					
					// Hacer visible el MTComponent
					break;
				default:
					break;
				}
			}
			
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public static synchronized void agregarAccion(final int tipoAccion, ObjetoUML objeto){
		ObjetoUML clon=objeto.clonar();
		UndoAction accion=new UndoAction(tipoAccion,clon,objeto.getId());
		synchronized (undoStack) {
			undoStack.push(accion);
		}
		
		/*switch (tipoAccion) {
		case AGREGAR_OBJETO_ACTION:	
			undoStack.push(null);			
			break;
		case EDITAR_OBJETO_ACTION:
			undoStack.push(new UndoAction(EDITAR_OBJETO_ACTION,objeto.clonar()));
			break;
		case BORRAR_OBJETO_ACTION:			
			undoStack.push(new UndoAction(BORRAR_OBJETO_ACTION,objeto.clonar()));	
			break;
		default:
			break;
		}	*/
		
	}
	
	public static void imprimirStack(){
		for(UndoAction a:undoStack){
			System.out.println(a);
			
		}
	}

}
class UndoAction{
	public final int tipoAcccion;
	public ObjetoUML objeto;
	public int idObjeto;
	public UndoAction(final int tipoAccion,ObjetoUML objeto, int id){
		this.tipoAcccion=tipoAccion;
		this.objeto=objeto;
		this.idObjeto=id;
	}
	@Override
	public String toString(){
		return "Accion="+tipoAcccion+" ObjetoUML="+idObjeto+"Tipo="+objeto.getClass();
		
	}
}
