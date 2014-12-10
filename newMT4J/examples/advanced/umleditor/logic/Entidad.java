package advanced.umleditor.logic;

import java.util.ArrayList;

public class Entidad extends ObjetoUML  {

	private String nombre;
	private boolean tieneRelacionRecursiva=false;
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

	public boolean isTieneRelacionRecursiva() {
		return tieneRelacionRecursiva;
	}

	public void setTieneRelacionRecursiva(boolean tieneRelacionRecursiva) {
		this.tieneRelacionRecursiva = tieneRelacionRecursiva;
	}
	
	
	@Override
	public ObjetoUML clonar(){
		Entidad clon=new Entidad(this.getPersona());
		clon.setNombre(new String(this.getNombre()));
		clon.setAtributos(new ArrayList<String>(this.getAtributos()));
		clon.setTieneRelacionRecursiva(this.isTieneRelacionRecursiva());
		return clon;		
	}
	@Override
	public void restaurar(ObjetoUML objeto){
		if(objeto instanceof Entidad){			
			this.setNombre(new String(((Entidad) objeto).getNombre()));
			this.setAtributos(new ArrayList<String>(((Entidad) objeto).getAtributos()));
			this.setTieneRelacionRecursiva(((Entidad) objeto).isTieneRelacionRecursiva());		
		}				
	}

}
