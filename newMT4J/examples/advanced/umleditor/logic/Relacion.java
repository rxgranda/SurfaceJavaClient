package advanced.umleditor.logic;

import org.hamcrest.core.IsInstanceOf;
import org.mt4j.util.math.Vector3D;

import advanced.umleditor.impl.ObjetoUMLGraph;
import advanced.umleditor.impl.TextoFlotanteImpl;

public class Relacion extends ObjetoUML {

	public final static int CARDINALIDAD_UNO=1;
	public final static int CARDINALIDAD_CERO_UNO=2;
	public final static int CARDINALIDAD_CERO_MUCHOS=3;
	public final static int CARDINALIDAD_UNO_MUCHOS=4;
	public final static int CARDINALIDAD_MUCHOS=5;

	
	private  Vector3D inicio,fin;
	private String nombre;
	private ObjetoUML objetoInicio;
	private ObjetoUML objetoFin;
	private ObjetoUML textoInicio;
	private ObjetoUML textoFin;
	

	private int cardinalidadInicio=CARDINALIDAD_UNO;
	private int cardinalidadFin=CARDINALIDAD_UNO;
	
	
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

	public int getCardinalidadInicio() {
		return cardinalidadInicio;
	}

	public void setCardinalidadInicio(int cardinalidadInicio) {
		this.cardinalidadInicio = cardinalidadInicio;
	}

	public int getCardinalidadFin() {
		return cardinalidadFin;
	}

	public void setCardinalidadFin(int cardinalidadFin) {
		this.cardinalidadFin = cardinalidadFin;
	}
	public ObjetoUML getTextoFin() {
		return textoFin;
	}

	public void setTextoFin(ObjetoUML textoFin) {
		this.textoFin = textoFin;
	}
	public ObjetoUML getTextoInicio() {
		return textoInicio;
	}

	public void setTextoInicio(ObjetoUML textoInicio) {
		this.textoInicio = textoInicio;
	}
	
	public boolean tieneTextoInicio(){
		if (textoInicio.getFigura()==null){
			return false;
		}
		return true;
	}
	
	public boolean tieneTextoFin(){
		if (textoFin.getFigura()==null){
			return false;
		}
		return true;
	}
	public String getLabelTextoInicio(){
		if (tieneTextoInicio()){			
			return ((TextoFlotante)textoInicio).getNombre();			
		}
		return "";
		
	}
	public String getLabelTextoFin(){
		if (tieneTextoFin()){			
			return ((TextoFlotante)textoFin).getNombre();			
		}
		return "";
		
	}


	
}