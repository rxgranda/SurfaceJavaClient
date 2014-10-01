package advanced.umleditor.impl;

import java.util.LinkedList;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import processing.core.PApplet;

public class Relacion_Impl extends MTComponent implements ObjetoUMLGraph{
	

	private final MTLine linea ;
	private final ObjetoUML objeto;
	final MTEllipse ini=null;
	final MTEllipse fin=null;
	public Relacion_Impl(MTApplication mtApp, final ObjetoUML objeto, ObjetoUMLGraph objeto1,ObjetoUMLGraph objeto2) {
		super(mtApp);
		final Vector3D esquina1=((Relacion)objeto).getInicio();
		final Vector3D esquina2=((Relacion)objeto).getFin();
		linea= new MTLine(mtApp, esquina1.x,esquina1.y,esquina2.x,esquina2.y);					
		linea.setFillColor(new MTColor(0, 0, 0));
		linea.setStrokeColor(new MTColor(0, 0, 0));
		linea.setNoStroke(false);
		this.objeto=objeto;
		
		// Circulos al inicio y fin de la linea
	/*	ini=new MTEllipse(mtApp, esquina1, 5, 5);
		ini.setFillColor(ObjetoUMLGraph.azul);
		
		fin=new MTEllipse(mtApp, esquina2, 5, 5);
		fin.setFillColor(ObjetoUMLGraph.azul);
		
		//ini.removeAllGestureEventListeners();
		ini.unregisterAllInputProcessors();
		ini.registerInputProcessor(new DragProcessor(mtApp));
		ini.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				//rectangulo.setWidthXYGlobal(rectangulo.getWidthXY(TransformSpace.GLOBAL)+de.getTranslationVect().x);
				//rectangulo.setSizeXYGlobal(rectangulo.getWidthXY(TransformSpace.GLOBAL)+de.getTranslationVect().x,rectangulo.getHeightXY(TransformSpace.GLOBAL)+de.getTranslationVect().y);
				//objeto.setWidth(objeto.getWidth()+de.getTranslationVect().x);
				//objeto.setHeigth(objeto.getHeigth()+de.getTranslationVect().y);
				((Relacion)objeto).setInicio(new Vector3D(((Relacion)objeto).getInicio()).addLocal(de.getTranslationVect()));
				//linea.setPositionGlobal(objeto.getPosicion());
				Vertex[] a= new Vertex[2];
				a[0]= new Vertex(((Relacion)objeto).getInicio());
				a[1]= new Vertex(((Relacion)objeto).getFin());

				linea.setVertices(a);
				return false;
			}
		});
		
		fin.unregisterAllInputProcessors();
		fin.registerInputProcessor(new DragProcessor(mtApp));
		fin.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				((Relacion)objeto).setFin(new Vector3D(((Relacion)objeto).getFin()).addLocal(de.getTranslationVect()));
				//linea.setPositionGlobal(objeto.getPosicion());
				Vertex[] a= new Vertex[2];
				a[1]= new Vertex(((Relacion)objeto).getFin());
				a[0]= new Vertex(((Relacion)objeto).getInicio());
				linea.setVertices(a);											
				return false;
			}
		});
		
		
		
		linea.addChild(ini);
		linea.addChild(fin);*/

		// TODO Auto-generated constructor stub
	}
	@Override
	public MTComponent getFigura() {
		// TODO Auto-generated method stub
		return linea;
	}
	@Override
	public void setTitulo(String texto) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getTitulo(String texto) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setAtributo(String texto) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getAtributo(String texto) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public MTComponent getHalo() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void guardarDatos(String keyword, Object datos) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public LinkedList obtenerDatos(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}
	public void actualizarRelacion(){
		Vertex[] a= new Vertex[2];		
		a[1]= new Vertex(((Relacion)objeto).getFin());
		a[0]= new Vertex(((Relacion)objeto).getInicio());
		linea.setVertices(a);	
		final Vector3D esquina1=((Relacion)objeto).getInicio();
		final Vector3D esquina2=((Relacion)objeto).getFin();
		
		//ini.setPositionGlobal(esquina1);
		//fin.setPositionGlobal(esquina2);
	}
	@Override
	public ObjetoUML getObjetoUML() {
		// TODO Auto-generated method stub
		return objeto;
	}
	@Override
	public void setObjetoUML(ObjetoUML objeto) {
		// TODO Auto-generated method stub
		//this.objeto=
		
	}
	

}
