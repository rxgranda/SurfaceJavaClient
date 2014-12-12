package advanced.umleditor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.mt4j.components.MTComponent;
import org.mt4j.util.math.Vector3D;

import advanced.drawing.DrawSurfaceScene;
import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.RelacionTernaria;
import advanced.umleditor.logic.TextoFlotante;
import advanced.umleditor.logic.Usuario;
import advanced.umleditor.logic.Relacion;

public class UMLFacade {
	
	private ObjetoUML objeto;
	
	private Usuario persona;
	private UMLRecognizer recognizer;
	private boolean modo_borrador=false;
	
	
	//Util
	private int numMuestas; 
	private float minX=100000000,minY=10000000,maxX,maxY,acumCentroideX,acumCentroideY, primerX,primerY,ultimoX,ultimoY;
	//Util
	
	
	public UMLFacade(Usuario p){		
		this.recognizer=new UMLRecognizer();		
		this.persona=p;
	}
	
	
	
	public void anadirPunto(float x,float y){
		if(!modo_borrador){
		recognizer.addPoint(x, y);
		
		if(minX>x)
			minX=x;
		if(minY>y)
			minY=y;
		if(maxX<x)
			maxX=x;
		if(maxY<y)
			maxY=y;
		
		ultimoX=x;
		ultimoY=y;
		if(numMuestas<=2){
			primerX=x;
			primerY=y;
		}
		acumCentroideX+=x;
		acumCentroideY+=y;
		numMuestas++;
		if (numMuestas>5000){
			reiniciar();
		}
		}
			
	}
	
	public ObjetoUML aniadirTextoFlotante(Vector3D position){
		
		ObjetoUML objeto = UMLCollection.anadirObjeto(ObjetoUML.TEXTOFLOTANTE, persona);
		//FALTAN PONER RESTO DE ATTRIBUTOS

		((TextoFlotante)objeto).setWidth((int)(100));
		((TextoFlotante)objeto).setHeight((int)(40));
		((TextoFlotante)objeto).setPosicion(position);
		((TextoFlotante)objeto).setNombre("Default");

		
		
		return  objeto;
	}
	
	public ObjetoUML reconocerObjeto(){
		////System.out.println("Numero de muestrassssssss"+numMuestas);
		if(isModo_borrador()){
			reiniciar();
			System.out.println("ES BORRADORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
			
			objeto=ObjetoUML.DELETE_OBJECT_GESTURE;
			return objeto;
		}
		int resultado=recognizer.recognize();
		System.out.println("entrandooooooo"+ resultado);
		if(resultado!=ObjetoUML.INVALIDO&&this.numMuestas>40){	
			int width=(int)(maxX-minX);
			int height=(int)(maxY-minY);
			
			if(width!=0&&height!=0){
				if(resultado==ObjetoUML.ENTIDAD&&(java.lang.Math.abs(width/height)>3||java.lang.Math.abs(height/width)>3)){
					////System.out.println("Entidad mal reconocida");
					reiniciar();
					objeto=ObjetoUML.OBJETO_INVALIDO;
					
					return objeto;
				}
			}
		
				
			objeto=UMLCollection.anadirObjeto(resultado,persona );
			System.out.println("height: " + height+ " width:" + width );
			
			//Caso de que sea una linea, se debe conocer posicion inicial y final
			
			if (objeto.getTipo()==ObjetoUML.RELACION){
				((Relacion)objeto).inicializarDimensiones((int)primerX,(int) primerY, (int)ultimoX,(int) ultimoY);
				
			}
			if (objeto.getTipo()==ObjetoUML.ENTIDAD){
				
				if(width < DrawSurfaceScene.MIN_WIDTH){
					width = (int)DrawSurfaceScene.MIN_WIDTH;
				}
				if (height < DrawSurfaceScene.MIN_HEIGHT){				
					height = (int)DrawSurfaceScene.MIN_HEIGHT;
				}
				if (width > DrawSurfaceScene.MAX_WIDTH){
					width = (int)DrawSurfaceScene.MAX_WIDTH;
				}
				if (height > DrawSurfaceScene.MAX_HEIGHT){
					height = (int)DrawSurfaceScene.MAX_HEIGHT;
				}
				((Entidad)objeto).setNombre("Default");
				ArrayList<String> atributos=new ArrayList<String>();
				atributos.add("Id");
				((Entidad)objeto).setAtributos(atributos);
			}	
			if (objeto.getTipo()==ObjetoUML.RELACION_MULTIPLE){
				if(width < DrawSurfaceScene.MIN_WIDTH){
					width = (int)DrawSurfaceScene.MIN_WIDTH-15;
					height = width;
				}
				
				
				//System.out.println("retorna objeto RELACION MULTIPLE");
				//((RelacionMultiple)objeto).inicializarDimensiones((int)primerX, (int) primerY, (int)ultimoX, (int) ultimoY);
				
			}
			//// Calcular centroide,posicion y dimensiones
			this.getObjeto().setCentroide((int)((acumCentroideX/numMuestas) -5.0f),(int)((acumCentroideY/numMuestas) -5.0f));	
			this.getObjeto().setWidth(width);
			this.getObjeto().setHeight(height);
			this.getObjeto().setPosicion(new Vector3D((int)(this.getObjeto().getCentroide().x- (maxX-minX)/2),(int)	(this.getObjeto().getCentroide().y-(maxY-minY)/2)));		
			
			//// Para calcular centroide,posicion y dimensiones de una nueva figura
			reiniciar();
			return objeto;
		}
		reiniciar();
		objeto=ObjetoUML.OBJETO_INVALIDO;
		return objeto;		
	}
		
	public void reiniciar(){
		this.minX=100000000;
		this.minY=10000000;
		this.maxX=0;
		this.maxY=0;
		this.acumCentroideX=0;
		this.acumCentroideY=0;
		this.numMuestas=0;
		ultimoX=0;
		ultimoY=0;		
		primerX=0;
		primerY=0;
		this.recognizer.reiniciar();
		//this.setModo_borrador(false);
	
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
	
	public static  ObjetoUML getObjetoUML(int id){
		return UMLCollection.getListaUML().get(id);
	}



	public float getWidth() {
		return objeto.getWidth();
	}	


	public float getHeigth() {
		return objeto.getHeight();
	}
	
	
	public void implementarRelacion(){
		
	}
	



	public boolean isModo_borrador() {
		return modo_borrador;
	}



	public void setModo_borrador(boolean modo_borrador) {
		this.modo_borrador = modo_borrador;
	}
	
	
}
