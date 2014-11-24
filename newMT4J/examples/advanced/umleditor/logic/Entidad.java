package advanced.umleditor.logic;

import java.util.ArrayList;

public class Entidad extends ObjetoUML {

	private String nombre;
	private ArrayList<String> atributos;
	
	public Entidad(Usuario per) {
		super(per);
		this.setTipo(ObjetoUML.ENTIDAD);
	//	argumentos= new ArrayList<String>();
	}

	public String getNombre() {
		return nombre;
	}

	public synchronized void  setNombre(String nombre) {
		this.nombre = nombre;
	}


	public ArrayList<String> getAtributos() {
		return atributos;
	}

	public synchronized void setAtributos(ArrayList<String> atributos) {
		this.atributos = atributos;
	}
	
	
	
	

}
