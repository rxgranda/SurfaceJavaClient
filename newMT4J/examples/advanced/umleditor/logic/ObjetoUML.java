package advanced.umleditor.logic;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public abstract class ObjetoUML {
	private int idPersona;
	private Timestamp tiempoInicio;
	private Timestamp tiempoFin;
	private ArrayList<Float> []puntos;
	private static Calendar calendar = Calendar.getInstance();

	
	public ObjetoUML(){
		tiempoInicio= new java.sql.Timestamp(calendar.getTime().getTime());
		puntos=new ArrayList[2];
	}
	public void anadirPunto(float x,float y){
		
	}
}
