package advanced.umleditor.logic;

public class Entidad extends ObjetoUML {

	public Entidad(Persona per) {
		super(per);
		this.setTipo(ObjetoUML.ENTIDAD);
	}

}
