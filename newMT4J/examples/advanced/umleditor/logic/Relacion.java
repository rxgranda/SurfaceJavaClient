package advanced.umleditor.logic;

import org.mt4j.util.math.Vector3D;

public class Relacion extends ObjetoUML {

	private  Vector3D inicio,fin;
	public Relacion(Persona per) {
		super(per);		
		this.setTipo(ObjetoUML.RELACION);
	}
	
	public void inicializarDimensiones(float inicioX, float inicioY, float finX ,float finY){
		setInicio(new Vector3D(inicioX,inicioY));
		setFin(new Vector3D(finX,finY));		
	}

	public Vector3D getFin() {
		return fin;
	}

	public void setFin(Vector3D fin) {
		this.fin = fin;
	}

	public Vector3D getInicio() {
		return inicio;
	}

	public void setInicio(Vector3D inicio) {
		this.inicio = inicio;
	}
	
}