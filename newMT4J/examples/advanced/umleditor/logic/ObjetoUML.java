package advanced.umleditor.logic;

import java.awt.Point;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public abstract class ObjetoUML {
	private Persona persona;
	private Timestamp tiempoInicio;
	//private Timestamp tiempoFin;
	private ArrayList<Point> puntos;
	private static Calendar calendar = Calendar.getInstance();

	
	public ObjetoUML(Persona p){
		tiempoInicio= new java.sql.Timestamp(calendar.getTime().getTime());		
		puntos=new ArrayList<Point>();
		this.persona=p;
	}
	public void anadirPunto(int x,int y){
		puntos.add(new Point(x,y));
	}
	public Point getPunto(int index){
		if (index>=puntos.size())
			return null;
		return puntos.get(index);
	}
	/*public int getIdPersona() {
		return idPersona;
	}
	public void setIdPersona(int idPersona) {
		this.idPersona = idPersona;
	}
	*/
}
