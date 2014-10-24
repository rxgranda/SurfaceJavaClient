package advanced.umleditor.impl;

import java.util.LinkedList;

import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import advanced.umleditor.UMLFacade;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import processing.core.PApplet;

public class Relacion_Impl extends MTComponent implements ObjetoUMLGraph{


	private final MTLine linea ;
	private final ObjetoUML objeto;
	final MTRoundRectangle halo;
	MTEllipse ini=null;
	MTEllipse fin=null;
	public Relacion_Impl(MTApplication mtApp, final MTComponent container, final MTCanvas canvas, final ObjetoUML objeto, ObjetoUMLGraph objeto1,ObjetoUMLGraph objeto2,final UMLFacade recognizer) {
		super(mtApp);
		////
		Vertex a= new Vertex(),b= new Vertex();
		Vertex []c=new Vertex[2];
		a= new Vertex(((Relacion)objeto).getInicio());
		b= new Vertex(((Relacion)objeto).getFin());
		c[0]=a;
		c[1]=b;
		////
		linea= new MTLine(mtApp,a,b);	
		linea.setVertices(c);
		linea.setPickable(false);
		linea.setFillColor(new MTColor(0, 0, 0));
		linea.setStrokeColor(new MTColor(0, 0, 0));
		linea.setNoStroke(false);
		this.objeto=objeto;

		linea.removeAllGestureEventListeners();
		linea.unregisterAllInputProcessors();
		/*linea.addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
					AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
					InputCursor cursor = cursorInputEvt.getCursor();
					IMTComponent3D target = cursorInputEvt.getTargetComponent();

					switch (cursorInputEvt.getId()) {
					case AbstractCursorInputEvt.INPUT_STARTED:
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						break;
					case AbstractCursorInputEvt.INPUT_UPDATED:
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:

						System.out.println("Reconocer:");
						ObjetoUML obj=recognizer.reconocerObjeto();
						if (obj ==ObjetoUML.DELETE_OBJECT_GESTURE){
							//container.removeChild(rectangulo);
							linea.removeFromParent();
							
						}
						break;
					default:
						break;
					}


				}else{
					//handle other input events
				}
				return false;
			}
		});
*/


		// Circulos al inicio y fin de la linea
		ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()), 15, 15);
		//ini.setFillColor(ObjetoUMLGraph.azul);
		ini.setNoFill(true); // Hacerlo Invisible
		ini.setNoStroke(true);
		fin=new MTEllipse(mtApp,new Vector3D(((Relacion)objeto).getFin()), 15, 15);
		//fin.setFillColor(ObjetoUMLGraph.azul);
		fin.setNoFill(true); // Hacerlo Invisible
		fin.setNoStroke(true);

		//ini.removeAllGestureEventListeners();
		ini.unregisterAllInputProcessors();
		ini.registerInputProcessor(new DragProcessor(mtApp));
		ini.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				((Relacion)objeto).setInicio(new Vector3D(((Relacion)objeto).getInicio()).getAdded(de.getTranslationVect()));
				Vertex[] a= new Vertex[2];
				a[0]= new Vertex(((Relacion)objeto).getInicio());
				a[1]= new Vertex(((Relacion)objeto).getFin());
				linea.setVertices(a);
				
				Vector3D vFin=new Vector3D(((Relacion)objeto).getFin());
				Vector3D vInicio=new Vector3D(((Relacion)objeto).getInicio());
				Vector3D distancia=vFin.getSubtracted(vInicio);

				Vertex[] haloVertex= new Vertex[4];		
				if(java.lang.Math.abs(distancia.y/distancia.x)<1){
					haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,25)));
					haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,-25)));
					haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(0,-25)));
					haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(0,25))));
				}else {
					haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(25,0)));
					haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(-25,0)));
					haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(-25,0)));
					haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(25,0))));
				}
				halo.setVertices(haloVertex);
				return false;
			}
		});



		fin.unregisterAllInputProcessors();
		fin.registerInputProcessor(new DragProcessor(mtApp));
		fin.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				((Relacion)objeto).setFin(new Vector3D(((Relacion)objeto).getFin()).getAdded(de.getTranslationVect()));
				//linea.setPositionGlobal(objeto.getPosicion());
				Vertex[] a= new Vertex[2];
				a[1]= new Vertex(((Relacion)objeto).getFin());
				a[0]= new Vertex(((Relacion)objeto).getInicio());								
				linea.setVertices(a);									
				
				Vector3D vFin=new Vector3D(((Relacion)objeto).getFin());
				Vector3D vInicio=new Vector3D(((Relacion)objeto).getInicio());
				Vector3D distancia=vFin.getSubtracted(vInicio);

				Vertex[] haloVertex= new Vertex[4];		
				//float pendiente=java.lang.Math.abs(distancia.y/distancia.x);
				if(java.lang.Math.abs(distancia.y/distancia.x)<1){
					haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,25)));
					haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,-25)));
					haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(0,-25)));
					haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(0,25))));
				}else {
					haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(25,0)));
					haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(-25,0)));
					haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(-25,0)));
					haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(25,0))));
				}
				halo.setVertices(haloVertex);
				return false;
			}
		});



		linea.addChild(ini);
		linea.addChild(fin);
		
		halo=new MTRoundRectangle(objeto
				.getPosicion().x-ObjetoUMLGraph.haloWidth/2, objeto
				.getPosicion().y-ObjetoUMLGraph.haloWidth/2, 0, objeto
				.getWidth()+ObjetoUMLGraph.haloWidth,
				objeto.getHeigth()+ObjetoUMLGraph.haloWidth, 1, 1, mtApp);		

		halo.setNoFill(true);
		//halo.setFillColor(ObjetoUMLGraph.transparent);
		
		halo.removeAllGestureEventListeners();		
		halo.setNoStroke(true);
		halo.addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
					AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
					InputCursor cursor = cursorInputEvt.getCursor();
					IMTComponent3D target = cursorInputEvt.getTargetComponent();
					System.out.println("Listener..............");
					switch (cursorInputEvt.getId()) {
					case AbstractCursorInputEvt.INPUT_STARTED:
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						break;
					case AbstractCursorInputEvt.INPUT_UPDATED:
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:

						System.out.println("Reconocer:");
						ObjetoUML obj=recognizer.reconocerObjeto();
						if (obj ==ObjetoUML.DELETE_OBJECT_GESTURE){
							
							linea.removeFromParent();
							//container.removeChild(halo);		
							halo.setVisible(false);
							halo.setPickable(false);
							//halo.removeFromParent();
						}
						break;
					default:
						break;
					}
				}else{
					//handle other input events
				}
				return false;
			}
		});
		canvas.addChild(halo);
		container.addChild(linea);							
	 	Vector3D vFin=new Vector3D(((Relacion)objeto).getFin());	 
		Vector3D vInicio=new Vector3D(((Relacion)objeto).getInicio());
		Vector3D distancia=vFin.getSubtracted(vInicio);
		Vertex[] haloVertex= new Vertex[4];		
		((Relacion)objeto).setInicio(ini.getCenterPointGlobal());
		((Relacion)objeto).setFin(fin.getCenterPointGlobal());

		//float pendiente=java.lang.Math.abs(distancia.y/distancia.x);
		if(java.lang.Math.abs(distancia.y/distancia.x)<1){
			haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,25)));
			haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,-25)));
			haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(0,-25)));
			haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(0,25))));
		}else {
			haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(25,0)));
			haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(-25,0)));
			haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(-25,0)));
			haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(25,0))));
			//	System.out.println("Pendiente mayor a 3."+pendiente+" Distancia x:"+distancia.x+"Y:"+distancia.y);

		}
		halo.setVertices(haloVertex);	 	
		((MTPolygon)halo).setBoundsBehaviour(AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK);
		objeto.setPosicion(linea.getCenterPointGlobal());			
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
		float width=java.lang.Math.abs(((Relacion)objeto).getInicio().x-((Relacion)objeto).getFin().x);
		objeto.setWidth(width);
		//float height=java.lang.Math.abs(((Relacion)objeto).getInicio().y-((Relacion)objeto).getFin().y);
		//objeto.setWidth(height);
		/*halo.setPositionGlobal(new Vector3D(objeto.getPosicion().x -ObjetoUMLGraph.haloWidth/2
				,objeto.getPosicion().y -ObjetoUMLGraph.haloWidth/2
				));*/
		//halo.setPositionGlobal(halo.getCenterPointGlobal().getAdded(halo.getCenterPointGlobal().getSubtracted(objeto.getPosicion())));
	//	halo.setSizeXYGlobal(width, halo.getHeightXY(TransformSpace.GLOBAL));
		Vertex[] a= new Vertex[2];		
		a[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()));
		a[0]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()));
		linea.setVertices(a);		

		ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()));
		fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()));
		halo.setPositionGlobal(linea.getCenterPointGlobal());
		objeto.setPosicion(linea.getCenterPointGlobal());

		Vector3D vFin=new Vector3D(((Relacion)objeto).getFin());
		Vector3D vInicio=new Vector3D(((Relacion)objeto).getInicio());
		Vector3D distancia=vFin.getSubtracted(vInicio);

		Vertex[] haloVertex= new Vertex[4];		
		if(java.lang.Math.abs(distancia.y/distancia.x)<1){
			haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,35)));
			haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()));
			haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()));
			haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(0,35))));
		}else {
			haloVertex[0]= new Vertex(new Vector3D(new Vector3D(((Relacion)objeto).getFin())).getAdded(new Vector3D(35,0)));
			haloVertex[1]= new Vertex(new Vector3D(new Vector3D(((Relacion)objeto).getFin())));
			haloVertex[2]= new Vertex(new Vector3D(new Vector3D(((Relacion)objeto).getInicio())));
			haloVertex[3]= new Vertex(new Vector3D(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(35,0))));
		}		
		halo.setVertices(haloVertex);
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
	@Override
	public void actualizarEtiquetas() {
		// TODO Auto-generated method stub
		
	}


}
