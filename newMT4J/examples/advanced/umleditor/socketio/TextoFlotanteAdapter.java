package advanced.umleditor.socketio;

import java.util.ArrayList;

import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.TextoFlotante;

public class TextoFlotanteAdapter {
	private String nombre;
	private int id;
	private int idUsuario;
	private int tipo;
	private int ownerId;
	public TextoFlotanteAdapter(){
		
	}
	
	
	public TextoFlotanteAdapter(TextoFlotante textflo, int idUsuario) {		
		
		this.nombre=textflo.getNombre();
		setId(textflo.getId());
		this.idUsuario=idUsuario;
		this.tipo = ObjetoUML.TEXTOFLOTANTE;
		ObjetoUML temprel = (ObjetoUML)textflo.getOwner();
		this.ownerId = -1;
		if (!temprel.equals(null)){
			this.ownerId = temprel.getId();
			System.out.println("ID OWNER ::" + this.ownerId);
		}
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}	
	
	
	public void actualizar(TextoFlotante textflo){
	
		textflo.setNombre(nombre);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerid) {
		this.ownerId = ownerid;
	}
}
