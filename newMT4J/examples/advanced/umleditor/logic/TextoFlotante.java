package advanced.umleditor.logic;

import java.util.ArrayList;

public class TextoFlotante extends ObjetoUML {

	private String nombre;
	private ObjetoUML owner;
	
	public TextoFlotante(Usuario per) {
		super(per);
		this.setTipo(ObjetoUML.TEXTOFLOTANTE);
		owner = null;
	//	argumentos= new ArrayList<String>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setOwner(ObjetoUML idOwner){
		
		this.owner = idOwner;
	}
	public ObjetoUML getOwner(){
		
		return this.owner; 
	}
	
	
	

}
