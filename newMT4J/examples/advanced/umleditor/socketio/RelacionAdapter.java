package advanced.umleditor.socketio;
import java.util.ArrayList;

import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.TextoFlotante;

public class RelacionAdapter {
	
	private int id;
	private int idUsuario;
	private int tipo;
	public RelacionAdapter(){
		
		
	}
	public RelacionAdapter(Relacion relacion, int idUsuario){
		this.id = relacion.getId();
		this.idUsuario = idUsuario;
		this.tipo = ObjetoUML.RELACION;
	}
	public int getRelacionId(){
		return this.id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int  getTipo(){
		return this.tipo;
	}
	public void setTipo(int tipo){
		this.tipo = tipo;
		
	}
}
	