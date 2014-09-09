package advanced.umleditor;

import java.awt.Point;
import java.util.Collection;

import org.mt4j.util.math.Vector3D;

import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Persona;

public class UMLFacade {
	
	private ObjetoUML objeto;
	
	private Persona persona;
	private UMLRecognizer recognizer;
	
	//Util
	private int numMuestas; 
	private float minX=100000000,minY=10000000,maxX,maxY,acumCentroideX,acumCentroideY=0;
	//Util
	
	
	public UMLFacade(Persona p){		
		this.recognizer=new UMLRecognizer();		
		this.persona=p;
	}
	
	
	
	public void anadirPunto(float x,float y){
		recognizer.addPoint(x, y);
		
		if(minX>x)
			minX=x;
		if(minY>y)
			minY=y;
		if(maxX<x)
			maxX=x;
		if(maxY<y)
			maxY=y;
		acumCentroideX+=x;
		acumCentroideY+=y;
		numMuestas++;

	}
	
	public ObjetoUML reconocerObjeto(){
	
		int resultado=recognizer.recognize();
		if(resultado!=ObjetoUML.INVALIDO){			
			objeto=UMLCollection.anadirObjeto(resultado,persona );
			
			//// Calcular centroide,posicion y dimensiones
			this.getObjeto().setCentroide((acumCentroideX/numMuestas) -5.0f,(acumCentroideY/numMuestas) -5.0f);	
			this.getObjeto().setWidth((maxX-minX));
			this.getObjeto().setHeigth((maxY-minY));
			this.getObjeto().setPosicion(new Vector3D((this.getObjeto().getCentroide().x- (maxX-minX)/2),	(this.getObjeto().getCentroide().y-(maxY-minY)/2)));		
			//// Calcular centroide,posicion y dimensiones
			
			return objeto;
		}
		objeto=ObjetoUML.OBJETO_INVALIDO;
		return objeto;		
	}
		
	
	
	public Vector3D getPosicion(){
		return this.objeto.getPosicion();
	}
	
	public Vector3D getCentroide(){
		return this.getObjeto().getCentroide();
	}
	
	public ObjetoUML getObjeto() {
		return objeto;
	}
	
	public static Collection <ObjetoUML> getListaUML(){
		return UMLCollection.getListaUML();
	}



	public float getWidth() {
		return objeto.getWidth();
	}	


	public float getHeigth() {
		return objeto.getHeigth();
	}

}
