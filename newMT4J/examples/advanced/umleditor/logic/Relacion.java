package advanced.umleditor.logic;

public class Relacion extends ObjetoUML {

	public Relacion(Persona per) {
		super(per);		
		this.setTipo(ObjetoUML.RELACION);
	}
}