package advanced.umleditor.logic;

import java.util.ArrayList;

public class EntidadTest {

	private String nombre;
	private ArrayList<String> atributos;
	
	public EntidadTest() {
		super();
		atributos= new ArrayList<String>();
		atributos.add("Nombre");
		// TODO Auto-generated constructor stub
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public ArrayList<String> getAtributos() {
		return atributos;
	}
	public void setAtributos(ArrayList<String> atributos) {
		this.atributos = atributos;
	}
}
