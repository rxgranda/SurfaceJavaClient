package advanced.umleditor.logic;

import java.awt.Point;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.mt4j.components.MTComponent;
import org.mt4j.util.math.Vector3D;

import advanced.umleditor.UMLRecognizer;
import advanced.umleditor.impl.ObjetoUMLGraph;

public abstract class ObjetoUML {
	public static final int  INVALIDO=-1;
	public static final int  DELETE_GESTURE=-2;
	public static final int  ENTIDAD=1;
	public static final int  RELACION=2;
	private static Calendar calendar = Calendar.getInstance();	
	public  static ObjetoUML OBJETO_INVALIDO=new ObjetoInvalido(new Persona("","",-1)) ;
	public static ObjetoUML DELETE_OBJECT_GESTURE= new DeleteObject(new Persona("","",-1));
	private ObjetoUMLGraph figura;

	private Vector3D centroide;
	//Esquina superior izquierda
	private Vector3D posicion;
	private float width,heigth;
	private int tipo;
	private boolean borrado=true;
	private boolean tieneRelacion=false;
	private Persona persona;
	private Timestamp tiempoInicio;
	//private Timestamp tiempoFin;
	//private ArrayList<Point> puntos;


	
	public ObjetoUML(Persona p){
		tiempoInicio= new java.sql.Timestamp(calendar.getTime().getTime());		
		//puntos=new ArrayList<Point>();	
		this.persona=p;
	}


	public float getWidth() {
		return width;
	}


	public void setWidth(float width) {
		this.width = width;
	}


	public float getHeigth() {
		return heigth;
	}


	public void setHeigth(float heigth) {
		this.heigth = heigth;
	}


	public Vector3D getCentroide() {
		return centroide;
	}
	public void setCentroide(Vector3D nVector) {
		this.centroide = nVector;
	}

	public void setCentroide(float x,float y) {		
		this.centroide = new Vector3D(x,y,0);
		
	}
	
	
	public void anadirPunto(float x,float y){
		
	}


	public Vector3D getPosicion() {
		return posicion;
	}


	public void setPosicion(Vector3D posicion) {
		this.posicion = posicion;
	}


	public int getTipo() {
		return tipo;
	}


	public void setTipo(int tipo) {
		this.tipo = tipo;
	}


	public ObjetoUMLGraph getFigura() {
		return figura;
	}


	public void setFigura(ObjetoUMLGraph figura) {
		this.figura = figura;
	}


	public boolean isBorrado() {
		return borrado;
	}


	public void setBorrado(boolean borrado) {
		this.borrado = borrado;
	}


	public boolean isTieneRelacion() {
		return tieneRelacion;
	}


	public void setTieneRelacion(boolean tieneRelacion) {
		this.tieneRelacion = tieneRelacion;
	}


	


	
	/*public int getIdPersona() {
		return idPersona;
	}
	public void setIdPersona(int idPersona) {
		this.idPersona = idPersona;
	}
	*/
}

final class ObjetoInvalido extends ObjetoUML{

	//public final ObjetoUML invalido=new ObjetoInvalido(new Persona("","",-1)) ;
	public ObjetoInvalido(Persona p) {
		super(p);
		// TODO Auto-generated constructor stub
	}
	
}


final class DeleteObject extends ObjetoUML{
	public DeleteObject(Persona p) {
		super(p);
		// TODO Auto-generated constructor stub
	}
}