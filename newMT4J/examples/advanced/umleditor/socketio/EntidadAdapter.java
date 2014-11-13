package advanced.umleditor.socketio;

import java.util.ArrayList;

import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;

public class EntidadAdapter {
	private String nombre;
	private ArrayList<String> atributos;
	private int id;
	private int idUsuario;
	private int tipo;
	
	public EntidadAdapter(){
		
	}
	
	public EntidadAdapter(Entidad entidad, int idUsuario) {		
		atributos= entidad.getAtributos();
		this.nombre=entidad.getNombre();
		setId(entidad.getId());
		this.idUsuario=idUsuario;
		this.tipo = ObjetoUML.ENTIDAD;
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
	public ArrayList<String> getAtributos() {
		return atributos;
	}
	public void setAtributos(ArrayList<String> atributos) {
		this.atributos = atributos;
	}
	
	public void actualizar(Entidad entidad){
		entidad.setAtributos(atributos);
		entidad.setNombre(nombre);
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
	
	
}
