package advanced.umleditor.logic;

import java.util.ArrayList;

public class TextoFlotante extends ObjetoUML {

	private String nombre;

	
	public TextoFlotante(Usuario per) {
		super(per);
		this.setTipo(ObjetoUML.TEXTOFLOTANTE);
	//	argumentos= new ArrayList<String>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	

}
