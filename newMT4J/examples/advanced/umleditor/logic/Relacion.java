package advanced.umleditor.logic;

import org.mt4j.util.math.Vector3D;

public class Relacion extends ObjetoUML {

	private  Vector3D inicio,fin;
	private String nombre;
	private ObjetoUML objetoInicio;
	private ObjetoUML objetoFin;
	public Relacion(Usuario per) {
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
	
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	

	public Vector3D getInicio() {
		return inicio;
	}

	public void setInicio(Vector3D inicio) {
		this.inicio = inicio;
	}

	public ObjetoUML getObjetoInicio() {
		return objetoInicio;
	}

	public void setObjetoInicio(ObjetoUML objetoInicio) {
		this.objetoInicio = objetoInicio;
	}

	public ObjetoUML getObjetoFin() {
		return objetoFin;
	}

	public void setObjetoFin(ObjetoUML objetoFin) {
		this.objetoFin = objetoFin;
	}
	
}