package advanced.umleditor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextField;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import com.corundumstudio.socketio.SocketIOServer;

import advanced.drawing.MainDrawingScene;
import advanced.umleditor.UMLCollection;
import advanced.umleditor.UMLDataSaver;
import advanced.umleditor.UMLFacade;
import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.TextoFlotante;
import advanced.umleditor.logic.Usuario;
import advanced.umleditor.socketio.CardinalidadAdapter;
import advanced.umleditor.socketio.EntidadAdapter;
import advanced.umleditor.socketio.RelacionAdapter;
import advanced.umleditor.socketio.TextoFlotanteAdapter;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Relacion_Impl extends MTComponent implements ObjetoUMLGraph{

	
	private SocketIOServer server;
	private final MTLine linea ;
	private final ObjetoUML objeto;
	final MTRoundRectangle halo;
	static float DISTANCIA_FROM_NODE = 0.5f;
	
	private  ObjetoUML textoflotInicio;
	private  ObjetoUML textoflotFin;

	private static String imagesPath = "data"+MTApplication.separator;
			private MTApplication mtApp;
	
	
	private ArrayList<Vector3D> listapuntos;
	private final MTApplication app ;
	MTEllipse ini=null;
	MTEllipse fin=null;
	Map <Integer, MTComponent>listaCardinalidadInicio= new HashMap<Integer, MTComponent>();
	Map <Integer, MTComponent>listaCardinalidadFin= new HashMap<Integer, MTComponent>();


	
	private static final int CARDINALIDAD_LOCATION_DEFAULT=0;
	private static final int CARDINALIDAD_LOCATION_IZQUIERDA=1;
	private static final int CARDINALIDAD_LOCATION_DERECHA=2;
	private static final int CARDINALIDAD_LOCATION_ARRIBA=3;
	private static final int CARDINALIDAD_LOCATION_ABAJO=4;

	
	
	
	public Relacion_Impl(final MTApplication mtApp, final MTComponent container, final MTCanvas canvas, final ObjetoUML objeto, final UMLFacade recognizer,final SocketIOServer server) {
		super(mtApp);
		this.mtApp=mtApp;
		
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
		this.textoflotInicio =((Relacion)objeto).getTextoInicio();
		this.textoflotFin =((Relacion)objeto).getTextoFin();
		this.server = server;
		this.app  = mtApp;
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
		//final MTBackgroundImage backgroundImage = new MTBackgroundImage(mtApp, image, false); 
		PImage imagenCardUno = mtApp.loadImage(imagesPath + "uno.png");
		PImage imagenCardCeroUno = mtApp.loadImage(imagesPath + "ceroUnoI.png");
		PImage imagenCardCeroMuchos= mtApp.loadImage(imagesPath + "ceroMuchosI.png");
		PImage imagenCardUnoMuchos= mtApp.loadImage(imagesPath + "unoMuchosI.png");
		PImage imagenCardMuchos= mtApp.loadImage(imagesPath + "muchosI.png");
		//backgroundImage.setSizeXYGlobal(10, 10);
		//this.getCanvas().addChild(backgroundImage);

		new MTRectangle(15, 15, mtApp);
		// Circulos al inicio y fin de la linea
		
		listaCardinalidadInicio.put(Relacion.CARDINALIDAD_UNO, new MTRectangle(30, 30, mtApp));
		listaCardinalidadInicio.put(Relacion.CARDINALIDAD_CERO_UNO, new MTRectangle(30, 30, mtApp));
		listaCardinalidadInicio.put(Relacion.CARDINALIDAD_CERO_MUCHOS, new MTRectangle(30, 30, mtApp));
		listaCardinalidadInicio.put(Relacion.CARDINALIDAD_UNO_MUCHOS,new MTRectangle(30, 30, mtApp));
		listaCardinalidadInicio.put(Relacion.CARDINALIDAD_MUCHOS, new MTRectangle(30, 30, mtApp));

		listaCardinalidadFin.put(Relacion.CARDINALIDAD_UNO,  new MTRectangle(30, 30, mtApp));
		listaCardinalidadFin.put(Relacion.CARDINALIDAD_CERO_UNO, new MTRectangle(30, 30, mtApp));
		listaCardinalidadFin.put(Relacion.CARDINALIDAD_CERO_MUCHOS,new MTRectangle(30, 30, mtApp));
		listaCardinalidadFin.put(Relacion.CARDINALIDAD_UNO_MUCHOS, new MTRectangle(30, 30, mtApp));
		listaCardinalidadFin.put(Relacion.CARDINALIDAD_MUCHOS, new MTRectangle(30, 30, mtApp));

		
		
				
		((MTPolygon)listaCardinalidadInicio.get(Relacion.CARDINALIDAD_UNO)).setTexture(imagenCardUno);
		((MTPolygon)listaCardinalidadInicio.get(Relacion.CARDINALIDAD_CERO_UNO)).setTexture(imagenCardCeroUno);
		((MTPolygon)listaCardinalidadInicio.get(Relacion.CARDINALIDAD_CERO_MUCHOS)).setTexture(imagenCardCeroMuchos);
		((MTPolygon)listaCardinalidadInicio.get(Relacion.CARDINALIDAD_UNO_MUCHOS)).setTexture(imagenCardUnoMuchos);
		((MTPolygon)listaCardinalidadInicio.get(Relacion.CARDINALIDAD_MUCHOS)).setTexture(imagenCardMuchos);
		
		((MTPolygon)listaCardinalidadFin.get(Relacion.CARDINALIDAD_UNO)).setTexture(imagenCardUno);
		((MTPolygon)listaCardinalidadFin.get(Relacion.CARDINALIDAD_CERO_UNO)).setTexture(imagenCardCeroUno);
		((MTPolygon)listaCardinalidadFin.get(Relacion.CARDINALIDAD_CERO_MUCHOS)).setTexture(imagenCardCeroMuchos);
		((MTPolygon)listaCardinalidadFin.get(Relacion.CARDINALIDAD_UNO_MUCHOS)).setTexture(imagenCardUnoMuchos);
		((MTPolygon)listaCardinalidadFin.get(Relacion.CARDINALIDAD_MUCHOS)).setTexture(imagenCardMuchos);
		
		
		if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeight()/2>((Relacion)objeto).getInicio().y)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)), TAMANO_CARDINALIDAD, TAMANO_CARDINALIDAD);
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().y+((Relacion)objeto).getObjetoInicio().getHeight()/2<((Relacion)objeto).getInicio().y)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, -15)), TAMANO_CARDINALIDAD, TAMANO_CARDINALIDAD);		
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(15, 0)), TAMANO_CARDINALIDAD, TAMANO_CARDINALIDAD);
		else
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-15, 0)), TAMANO_CARDINALIDAD, TAMANO_CARDINALIDAD);

		/*if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(15, 0)), 15, 15);
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().x<((Relacion)objeto).getInicio().x)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-15, 0)), 15, 15);
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeigth()/2>((Relacion)objeto).getInicio().y)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)), 15, 15);
		else 
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, -15)), 15, 15);
		*/
		
		//ini.setFillColor(new MTColor);
		
		//ini.addChild(backgroundImage);
		//ini.setTexture(imagenCardinalidad);
		ini.setNoFill(true); // Hacerlo Invisible
		ini.setNoStroke(true);
		//ini.setStrokeColor(new MTColor(0,0,0));
		
		
		
		
		ini.addChild(listaCardinalidadInicio.get(Relacion.CARDINALIDAD_UNO));
		Set<Integer> keys=listaCardinalidadInicio.keySet();
		for(Integer key:keys){
			listaCardinalidadInicio.get(key).setPickable(false);
			((MTPolygon)listaCardinalidadInicio.get(key)).setPositionRelativeToOther(ini, ini.getCenterPointGlobal());
			((MTPolygon)listaCardinalidadInicio.get(key)).setNoStroke(true);
		}
		
					
			
		/*if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(15, 0)), 15, 15);
		else
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-15, 0)), 15, 15);
	*/
		
		
		if(((Relacion)objeto).getObjetoFin().getPosicion().y-((Relacion)objeto).getObjetoFin().getHeight()/2>((Relacion)objeto).getFin().y)
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, 15)), TAMANO_CARDINALIDAD, TAMANO_CARDINALIDAD);
		else if(((Relacion)objeto).getObjetoFin().getPosicion().y+((Relacion)objeto).getObjetoFin().getHeight()/2<((Relacion)objeto).getFin().y)
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, -15)), TAMANO_CARDINALIDAD, TAMANO_CARDINALIDAD);		
		else if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(15, 0)), TAMANO_CARDINALIDAD, TAMANO_CARDINALIDAD);
		else
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-15, 0)), TAMANO_CARDINALIDAD, TAMANO_CARDINALIDAD);

		//fin.setTexture(imagenCardinalidad);
		//fin=new MTEllipse(mtApp,new Vector3D(((Relacion)objeto).getFin()), 15, 15);
		//fin.setFillColor(ObjetoUMLGraph.azul);
		fin.setNoFill(true); // Hacerlo Invisible
		fin.setNoStroke(true);
		fin.addChild(listaCardinalidadFin.get(Relacion.CARDINALIDAD_UNO));

		Set<Integer> keys2=listaCardinalidadFin.keySet();
		for(Integer key:keys2){
			listaCardinalidadFin.get(key).setPickable(false);
			((MTPolygon)listaCardinalidadFin.get(key)).setPositionRelativeToOther(fin, fin.getCenterPointGlobal());
			((MTPolygon)listaCardinalidadFin.get(key)).setNoStroke(true);

		}	
		//ini.removeAllGestureEventListeners();
		ini.unregisterAllInputProcessors(); 
		/// mover las cardinalidades
		/*ini.registerInputProcessor(new DragProcessor(mtApp));
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
				//float pendiente=java.lang.Math.abs(distancia.y/distancia.x);
				if(java.lang.Math.abs(distancia.y/distancia.x)<1){
					haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,120)));
					haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,-0)));
					haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(0,-0)));
					haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(0,120))));
				}else{
					haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(50,0)));
					haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(-35,0)));
					haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(-35,0)));
					haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(50,0))));
				}
				halo.setVertices(haloVertex);
				halo.setFillColor(ObjetoUMLGraph.haloSelected);
				halo.setPositionGlobal(linea.getCenterPointGlobal());
				
				
				////
				/*if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
					ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(14,0)));
				else
					ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-14,0)));
			* /
				if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeight()/2>((Relacion)objeto).getInicio().y)
					ini.setPositionGlobal( new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)));
				else if(((Relacion)objeto).getObjetoInicio().getPosicion().y+((Relacion)objeto).getObjetoInicio().getHeight()/2<((Relacion)objeto).getInicio().y)
					ini.setPositionGlobal( new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, -15)));		
				else if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
					ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(15, 0)));
				else
					ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-15, 0)));
				

		
				Vector3D lineaCenter = new Vector3D(linea.getCenterPointGlobal());
				Vector3D referenciaHorizontal = new Vector3D(mtApp.getWidth(),mtApp.getHeight());
				Vector3D R1 = lineaCenter.getSubtracted(vInicio);
				Vector3D R1nuevo =R1.getScaled(DISTANCIA_FROM_NODE);
				Vector3D R1pos = vInicio.getAdded(R1nuevo);
				float angleRadians = R1.angleBetween(referenciaHorizontal);
				//float deg = angleRadians*DEG_TO_RAD;
				
				
				R1pos.y += 10;
				
				
				Vector3D R2 = lineaCenter.getSubtracted(vFin);
				Vector3D R2nuevo =R2.getScaled(DISTANCIA_FROM_NODE);
				Vector3D R2pos = vFin.getAdded(R2nuevo);
				R2pos.y += 10;
				
				TextoFlotanteImpl impInicio = (TextoFlotanteImpl)textoflotInicio.getFigura();
				if(impInicio != null){
					
					
					impInicio.rectangulo.setPositionGlobal(R1pos);
					impInicio.halo.setPositionGlobal(R1pos);
					//impInicio.rectangulo.rotateZ(R1pos, degree);
					
				}
				
	
				TextoFlotanteImpl impFin = (TextoFlotanteImpl)textoflotFin.getFigura();
				if(impFin != null){
					impFin.rectangulo.setPositionGlobal(R2pos);
					impFin.halo.setPositionGlobal(R2pos);
				}
				textoflotInicio.setPosicion(R1pos);
				
				textoflotFin.setPosicion(R2pos);
				
			


				
				
				
				////								
				return false;
			}
		});
*/


		fin.unregisterAllInputProcessors();
		/*fin.registerInputProcessor(new DragProcessor(mtApp));
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
					haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,120)));
					haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,-0)));
					haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(0,-0)));
					haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(0,120))));
				}else{
					haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(50,0)));
					haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(-35,0)));
					haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(-35,0)));
					haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(50,0))));
				}
				halo.setVertices(haloVertex);
				halo.setFillColor(ObjetoUMLGraph.haloSelected);
				halo.setPositionGlobal(linea.getCenterPointGlobal());
				
				
				
				///
				/*if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(14,0)));
				else
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-14,0)));
				* /
				if(((Relacion)objeto).getObjetoFin().getPosicion().y-((Relacion)objeto).getObjetoFin().getHeight()/2>((Relacion)objeto).getFin().y)
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, 15)));
				else if(((Relacion)objeto).getObjetoFin().getPosicion().y+((Relacion)objeto).getObjetoFin().getHeight()/2<((Relacion)objeto).getFin().y)
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, -15)));		
				else if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(15, 0)));
				else
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-15, 0)));
				
				Vector3D lineaCenter = new Vector3D(linea.getCenterPointGlobal());
				
				Vector3D R1 = lineaCenter.getSubtracted(vInicio);
				Vector3D R1nuevo =R1.getScaled(DISTANCIA_FROM_NODE);
				Vector3D R1pos = vInicio.getAdded(R1nuevo);
				R1pos.y += 10;
				
				
				Vector3D R2 = lineaCenter.getSubtracted(vFin);
				Vector3D R2nuevo =R2.getScaled(DISTANCIA_FROM_NODE);
				Vector3D R2pos = vFin.getAdded(R2nuevo);
				R2pos.y += 10;
				
				TextoFlotanteImpl impInicio = (TextoFlotanteImpl)textoflotInicio.getFigura();
				if(impInicio != null){
					
					
					impInicio.rectangulo.setPositionGlobal(R1pos);
					impInicio.halo.setPositionGlobal(R1pos);
				}
				
				TextoFlotanteImpl impFin = (TextoFlotanteImpl)textoflotFin.getFigura();
				if(impFin != null){
					impFin.rectangulo.setPositionGlobal(R2pos);
					impFin.halo.setPositionGlobal(R2pos);
				}
				
				textoflotInicio.setPosicion(R1pos);
				
				textoflotFin.setPosicion(R2pos);
				
				
				
				///
				return false;
			}
		});*/
		
		 CardinalidadProcessor iniProc=new CardinalidadProcessor(mtApp ,TAP_AND_HOLD_TIME,true); // para cardinalidad inicio, tercer atributo=true
		 ini.addGestureListener(CardinalidadProcessor.class, new TapAndHoldVisualizer(mtApp, linea));
		 ini.registerInputProcessor(iniProc);
		 ini.addGestureListener(CardinalidadProcessor.class,  iniProc);
		 
		 CardinalidadProcessor finProc=new CardinalidadProcessor(mtApp ,TAP_AND_HOLD_TIME,false); // para cardinalidad fin, tercer atributo=false
		 fin.addGestureListener(CardinalidadProcessor.class, new TapAndHoldVisualizer(mtApp, linea));
		 fin.registerInputProcessor(finProc);
		 fin.addGestureListener(CardinalidadProcessor.class,  finProc);

		
		linea.addChild(ini);
		linea.addChild(fin);
		
		halo=new MTRoundRectangle(objeto
				.getPosicion().x-ObjetoUMLGraph.haloWidth/2, objeto
				.getPosicion().y-ObjetoUMLGraph.haloWidth/2, 0, objeto
				.getWidth()+ObjetoUMLGraph.haloWidth,
				objeto.getHeight()+ObjetoUMLGraph.haloWidth, 1, 1, mtApp);		

		
		
		
		
		
		
		halo.setNoFill(true);
		
	//	halo.setFillColor(ObjetoUMLGraph.haloSelected);
		
		halo.removeAllGestureEventListeners();		
		halo.setNoStroke(false);
		halo.addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
					AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
					InputCursor cursor = cursorInputEvt.getCursor();
					IMTComponent3D target = cursorInputEvt.getTargetComponent();
					//System.out.println("Listener..............");
					
					//
					
					
					
					switch (cursorInputEvt.getId()) {
					case AbstractCursorInputEvt.INPUT_STARTED:
						System.out.println("-Mover relacion "+ ((Relacion)objeto).getId());

						
						listapuntos = new ArrayList<Vector3D>();
						
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						break;
					case AbstractCursorInputEvt.INPUT_UPDATED:
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						listapuntos.add(cursor.getPosition());
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:
						
						
						if(listapuntos.size() < 50){
							//System.out.println("hijos: " +halo.getChildCount());
							
	
							
							
							if (textoflotInicio.getFigura() == null && textoflotFin.getFigura() == null ){
							
							 	Vector3D vFin=new Vector3D(((Relacion)objeto).getFin());	 
								Vector3D vInicio=new Vector3D(((Relacion)objeto).getInicio());
								Vector3D distancia=vFin.getSubtracted(vInicio);
						
								Vector3D lineaCenter = new Vector3D(linea.getCenterPointGlobal());
								//MTLine ll  = new MTLine(mtApp, new Vertex(0,0), new Vertex(lineaCenter.x,lineaCenter.y));
								//POsicion inicio label
								Vector3D R1 = lineaCenter.getSubtracted(vInicio);
								Vector3D R1nuevo =R1.getScaled(DISTANCIA_FROM_NODE);
								Vector3D R1pos = vInicio.getAdded(R1nuevo);
								R1pos.y += 10;
								
								
								Vector3D R2 = lineaCenter.getSubtracted(vFin);
								Vector3D R2nuevo =R2.getScaled(DISTANCIA_FROM_NODE);
								Vector3D R2pos = vFin.getAdded(R2nuevo);
								R2pos.y += 10;
							
								TextoFlotanteImpl teximplinicio = new TextoFlotanteImpl(app, linea, canvas, recognizer, textoflotInicio, server);	
								teximplinicio.rectangulo.setName("TextoFlotanteImplINICIO");
								textoflotInicio.setFigura(teximplinicio);
								
								TextoFlotanteImpl teximplfin =  new TextoFlotanteImpl(app, linea, canvas, recognizer, textoflotFin, server);
								teximplfin.rectangulo.setName("TextoFlotanteImplFIN");
								textoflotFin.setFigura(teximplfin);
						
								MTRoundRectangle textInicio = (MTRoundRectangle)(teximplinicio.rectangulo);
								//textInicio.setPositionRelativeToParent(new Vector3D(ini.getCenterPointGlobal().x +DISTANCIA_FROM_NODE*distancia.length(), ini.getCenterPointGlobal().y+20, ini.getCenterPointGlobal().z));
								textInicio.setPositionGlobal(R1pos);
								
								MTRoundRectangle textFin = (MTRoundRectangle)(teximplfin.rectangulo);
								//textFin.setPositionRelativeToParent(new Vector3D(fin.getCenterPointGlobal().x -DISTANCIA_FROM_NODE*distancia.length(), fin.getCenterPointGlobal().y + 20, fin.getCenterPointGlobal().z));
								textFin.setPositionGlobal(R2pos);
								
								UMLDataSaver.agregarAccion(UMLDataSaver.EDITAR_OBJETO_ACTION, objeto, objeto.getPersona());
							}

						}
						
				
					//	System.out.println("Reconocer:");
						ObjetoUML obj=recognizer.reconocerObjeto();
						if (obj ==ObjetoUML.DELETE_OBJECT_GESTURE&&obj.getWidth()>40&&obj.getHeight()>40){
							
							
							String canal=(MainDrawingScene.getListaUsuarios().get((int)cursor.sessionID)!=null)?MainDrawingScene.getListaUsuarios().get((int)cursor.sessionID).getCanal():Usuario.CANAL_DEFAULT_USER;
							int idUsuario=(MainDrawingScene.getListaUsuarios().get((int)cursor.sessionID)!=null)?(int)cursor.sessionID:Usuario.ID_DEFAULT_USER;
							
							
							server.getNamespace("/login").getBroadcastOperations().sendEvent("eraseElement",new RelacionAdapter(((Relacion)objeto),idUsuario));
							
							 //server.getNamespace("/login").getBroadcastOperations().sendEvent("broad",new RelacionAdapter(((Relacion)objeto),idUsuario));

							removerRelacion(idUsuario,false);

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
		//((Relacion)objeto).setInicio(ini.getCenterPointGlobal());
		//((Relacion)objeto).setFin(fin.getCenterPointGlobal());
		objeto.setPosicion(linea.getCenterPointGlobal());	


		//float pendiente=java.lang.Math.abs(distancia.y/distancia.x);
		if(java.lang.Math.abs(distancia.y/distancia.x)<1){
			haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,60)));
			haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,-60)));
			haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(0,-60)));
			haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(0,60))));
		}else {
			haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(60,0)));
			haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(-60,0)));
			haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(-60,0)));
			haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(60,0))));
			//	System.out.println("Pendiente mayor a 3."+pendiente+" Distancia x:"+distancia.x+"Y:"+distancia.y);
			
		}
		halo.setVertices(haloVertex);	 	
		((MTPolygon)halo).setBoundsBehaviour(AbstractShape.BOUNDS_CHECK_THEN_GEOMETRY_CHECK);
		halo.setFillColor(ObjetoUMLGraph.haloSelected);
		
		LinkedList<TextoFlotanteImpl> textosflotantes = new LinkedList<TextoFlotanteImpl>();
		linea.setUserData(ObjetoUMLGraph.TEXTO_FLOTANTE_KEYWORD, textosflotantes);
		//this.actualizarRelacion();
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
	public synchronized void actualizarRelacion(){
		System.out.println("-Actualizar relacion "+ ((Relacion)objeto).getId());

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

		/*if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
			ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(14,0)));
		else
			ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-14,0)));
	*/
		/*	if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
		fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(14,0)));
	else
		fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-14,0)));
*/
		
		/////
		if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeight()/2>((Relacion)objeto).getInicio().y)
			ini.setPositionGlobal( new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)));
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().y+((Relacion)objeto).getObjetoInicio().getHeight()/2<((Relacion)objeto).getInicio().y)
			ini.setPositionGlobal( new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, -15)));		
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
			ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(15, 0)));
		else
			ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-15, 0)));
		
		if(((Relacion)objeto).getObjetoFin().getPosicion().y-((Relacion)objeto).getObjetoFin().getHeight()/2>((Relacion)objeto).getFin().y)
			fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, 15)));
		else if(((Relacion)objeto).getObjetoFin().getPosicion().y+((Relacion)objeto).getObjetoFin().getHeight()/2<((Relacion)objeto).getFin().y)
			fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, -15)));		
		else if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
			fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(15, 0)));
		else
			fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-15, 0)));

		//////
		
	//	ini.setUseDirectGL(true);
	
		
		//fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()));
		halo.setPositionGlobal(linea.getCenterPointGlobal());
		
		Vector3D vFin=new Vector3D(((Relacion)objeto).getFin());
		Vector3D vInicio=new Vector3D(((Relacion)objeto).getInicio());
		Vector3D distancia=vFin.getSubtracted(vInicio);
		
		
		Vector3D lineaCenter = new Vector3D(linea.getCenterPointGlobal());
		//POsicion inicio label
		Vector3D R1 = lineaCenter.getSubtracted(vInicio);
		Vector3D R1nuevo =R1.getScaled(DISTANCIA_FROM_NODE);
		Vector3D R1pos = vInicio.getAdded(R1nuevo);
		R1pos.y += 10;
		
		Vector3D R2 = lineaCenter.getSubtracted(vFin);
		Vector3D R2nuevo =R2.getScaled(DISTANCIA_FROM_NODE);
		Vector3D R2pos = vFin.getAdded(R2nuevo);
		R2pos.y += 10;
		
		TextoFlotanteImpl impInicio = (TextoFlotanteImpl)textoflotInicio.getFigura();
		if(impInicio != null){
			
			
			impInicio.rectangulo.setPositionGlobal(R1pos);
			impInicio.halo.setPositionGlobal(R1pos);
		}
		
		TextoFlotanteImpl impFin = (TextoFlotanteImpl)textoflotFin.getFigura();
		if(impFin != null){
			impFin.rectangulo.setPositionGlobal(R2pos);
			impFin.halo.setPositionGlobal(R2pos);
		}
		
		objeto.setPosicion(linea.getCenterPointGlobal());
		textoflotInicio.setPosicion(R1pos);
		textoflotFin.setPosicion(R2pos);
		


		Vertex[] haloVertex= new Vertex[4];		
		if(java.lang.Math.abs(distancia.y/distancia.x)<1){
			haloVertex[0]= new Vertex(new Vector3D(((Relacion)objeto).getFin()).getAdded(new Vector3D(0,120)));
			haloVertex[1]= new Vertex(new Vector3D(((Relacion)objeto).getFin()));
			haloVertex[2]= new Vertex(new Vector3D(((Relacion)objeto).getInicio()));
			haloVertex[3]= new Vertex(new Vector3D(((Relacion)objeto).getInicio().getAdded(new Vector3D(0,120))));
		}else {
			haloVertex[0]= new Vertex(new Vector3D(new Vector3D(((Relacion)objeto).getFin())).getAdded(new Vector3D(90,0)));
			haloVertex[1]= new Vertex(new Vector3D(new Vector3D(((Relacion)objeto).getFin())));
			haloVertex[2]= new Vertex(new Vector3D(new Vector3D(((Relacion)objeto).getInicio())));
			haloVertex[3]= new Vertex(new Vector3D(new Vector3D(((Relacion)objeto).getInicio()).getAdded(new Vector3D(90,0))));
		}		
		halo.setVertices(haloVertex);
		halo.setFillColor(ObjetoUMLGraph.haloSelected);
		
	}
	@Override
	public ObjetoUML getObjetoUML() {
		// TODO Auto-generated method stub
		return objeto;
	}
	@Override
	public void setObjetoUML(ObjetoUML objeto) {
		// TODO Auto-generated method stub
		//this.objeto=objeto;

	}
	@Override
	public void actualizarEtiquetas() {
		// TODO Auto-generated method stub
		
	}
	

	public synchronized void removerRelacion(int idUsuario,boolean propagacion){

		System.out.println("-Eliminar relacion "+ ((Relacion)objeto).getId());

		LinkedList<TextoFlotanteImpl> textosflotantes = (LinkedList<TextoFlotanteImpl>)linea.getUserData(ObjetoUMLGraph.TEXTO_FLOTANTE_KEYWORD);
		for (TextoFlotanteImpl textoflot : textosflotantes ){
			
			textoflot.removeGrafico();
			
		}
		/*
		MTComponent bus = linea.getChildByName("TextoFlotanteImplINICIO");
		System.out.println("FOUNDCOMP"  + bus);
		TextoFlotanteImpl t1 = (TextoFlotanteImpl)bus.getParent();
		t1.getHalo().removeFromParent();
		//linea.removeChild(bus);
		//textoflotInicio.setFigura(null);
		
		MTComponent bus2 = linea.getChildByName("TextoFlotanteImplFIN");
		System.out.println("FOUNDCOMP"  + bus2);
		TextoFlotanteImpl t2 = (TextoFlotanteImpl)bus2.getParent();
		t2.getHalo().removeFromParent();
		//linea.removeChild(bus2);
		//textoflotInicio.setFigura(null);*/
		
		linea.removeFromParent();
		//container.removeChild(halo);		
		halo.setVisible(false);
		halo.setPickable(false);
		if(!propagacion){
		Entidad inicio=((Entidad)((Relacion)this.objeto).getObjetoInicio());
		//System.out.println("Objetoo inicio"+inicio);
		inicio.getFigura().eliminarDatos(RELACIONES_INICIO_KEYWORD, this);
		
		Entidad fin=((Entidad)((Relacion)this.objeto).getObjetoFin());
		fin.getFigura().eliminarDatos(RELACIONES_FIN_KEYWORD, this);
		UMLDataSaver.agregarAccion(UMLDataSaver.BORRAR_OBJETO_ACTION, objeto,MainDrawingScene.getListaUsuarios().get(idUsuario) );

		}
	
		
		
	}
	@Override
	public void eliminarDatos(String keyword, Object datos) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private int ubicacionCardinalidad(MTPolygon componente){
		int ubicacion=CARDINALIDAD_LOCATION_DEFAULT;
		if(componente==ini){
			if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeight()/2>((Relacion)objeto).getInicio().y)
				ubicacion=CARDINALIDAD_LOCATION_ARRIBA;
			else if(((Relacion)objeto).getObjetoInicio().getPosicion().y+((Relacion)objeto).getObjetoInicio().getHeight()/2<((Relacion)objeto).getInicio().y)
				ubicacion=CARDINALIDAD_LOCATION_ABAJO;
			else if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
				ubicacion=CARDINALIDAD_LOCATION_IZQUIERDA;
			else
				ubicacion=CARDINALIDAD_LOCATION_DERECHA;
			
		}else if(componente==fin){
			
			if(((Relacion)objeto).getObjetoFin().getPosicion().y-((Relacion)objeto).getObjetoFin().getHeight()/2>((Relacion)objeto).getFin().y)
				ubicacion=CARDINALIDAD_LOCATION_ARRIBA;
			else if(((Relacion)objeto).getObjetoFin().getPosicion().y+((Relacion)objeto).getObjetoFin().getHeight()/2<((Relacion)objeto).getFin().y)
				ubicacion=CARDINALIDAD_LOCATION_ABAJO;
			else if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
				ubicacion=CARDINALIDAD_LOCATION_IZQUIERDA;
			else
				ubicacion=CARDINALIDAD_LOCATION_DERECHA;
		}
		return ubicacion;
	}
	public synchronized void actualizarCardinalidad(final int cardinalidad,boolean cardinalidadSwitch){
		System.out.println("-Actualizar cardinalidad relacion "+ ((Relacion)objeto).getId());

		MTPolygon componente;
		int ubicacion=0; // 1=izquierda, 2 Derecha, 3 arriba, 4 abajo del componente
		Map lista;
		if(cardinalidadSwitch){
			componente=ini;
			((Relacion)objeto).setCardinalidadInicio(cardinalidad);
			Set<Integer> keys=listaCardinalidadInicio.keySet();
			for(Integer key:keys){
				((MTPolygon)listaCardinalidadInicio.get(key)).removeFromParent();
			}
			lista=listaCardinalidadInicio;
			//System.out.println("Es Inicio");
		}else{
			componente=fin;	
			((Relacion)objeto).setCardinalidadFin(cardinalidad);
			Set<Integer> keys=listaCardinalidadFin.keySet();
			for(Integer key:keys){
				((MTPolygon)listaCardinalidadFin.get(key)).removeFromParent();
			}	
			lista=listaCardinalidadFin;
		}
		int ubicacionCardinalidad=ubicacionCardinalidad(componente);
		PImage imagenCardinalidad =null;
		String nombre="uno.png";	
		//System.out.println("cardinalidad es: " + cardinalidad +"Componente es : " +componente);
		MTPolygon cardinalidadComponent=null;
		switch (cardinalidad) {
			
			case Relacion.CARDINALIDAD_UNO:						
				//imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);
				cardinalidadComponent=(MTPolygon)lista.get(Relacion.CARDINALIDAD_UNO);
				break;
			case Relacion.CARDINALIDAD_CERO_UNO:
				/*switch (ubicacionCardinalidad) {
				case CARDINALIDAD_LOCATION_ARRIBA:
					nombre="ceroUnoA.png";
					break;
				case CARDINALIDAD_LOCATION_ABAJO:
					nombre="ceroUnoB.png";
					break;
				case CARDINALIDAD_LOCATION_IZQUIERDA:
					nombre="ceroUnoI.png";
					break;
				case CARDINALIDAD_LOCATION_DERECHA:
					nombre="ceroUnoD.png";
					break;

				default:
					break;
				}
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);	*/
				cardinalidadComponent=(MTPolygon)lista.get(Relacion.CARDINALIDAD_CERO_UNO);
				
				break;
			case Relacion.CARDINALIDAD_CERO_MUCHOS:
				/*switch (ubicacionCardinalidad) {
				case CARDINALIDAD_LOCATION_ARRIBA:
					nombre="ceroMuchosA.png";
					break;
				case CARDINALIDAD_LOCATION_ABAJO:
					nombre="ceroMuchosB.png";
					break;
				case CARDINALIDAD_LOCATION_IZQUIERDA:
					nombre="ceroMuchosI.png";
					break;
				case CARDINALIDAD_LOCATION_DERECHA:
					nombre="ceroMuchosD.png";
					break;

				default:
					break;
				}
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);*/
				cardinalidadComponent=(MTPolygon)lista.get(Relacion.CARDINALIDAD_CERO_MUCHOS);

				break;
			 
			case Relacion.CARDINALIDAD_UNO_MUCHOS:
				/*switch (ubicacionCardinalidad) {
				case CARDINALIDAD_LOCATION_ARRIBA:
					nombre="unoMuchosA.png";
					break;
				case CARDINALIDAD_LOCATION_ABAJO:
					nombre="unoMuchosB.png";
					break;
				case CARDINALIDAD_LOCATION_IZQUIERDA:
					nombre="unoMuchosI.png";
					break;
				case CARDINALIDAD_LOCATION_DERECHA:
					nombre="unoMuchosD.png";
					break;

				default:
					break;
				}
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);*/
				
				cardinalidadComponent=(MTPolygon)lista.get(Relacion.CARDINALIDAD_UNO_MUCHOS);

				break;
			
			case Relacion.CARDINALIDAD_MUCHOS:
				/*switch (ubicacionCardinalidad) {
				case CARDINALIDAD_LOCATION_ARRIBA:
					nombre="muchosA.png";
					break;
				case CARDINALIDAD_LOCATION_ABAJO:
					nombre="muchosB.png";
					break;
				case CARDINALIDAD_LOCATION_IZQUIERDA:
					nombre="muchosI.png";
					break;
				case CARDINALIDAD_LOCATION_DERECHA:
					nombre="muchosD.png";
					break;

				default:
					break;
				}
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);*/
				cardinalidadComponent=(MTPolygon)lista.get(Relacion.CARDINALIDAD_MUCHOS);

				break;
				
			default:
				break;		
		}
		
		
		/*if(imagenCardinalidad!=null){
			System.out.println("Imagen cardinalidad : " + imagesPath + nombre );
			//PImage p = mtApp.loadImage(imagesPath + nombre);
			//componente.remove
			//componente.removeFromParent();
		///	componente.
			System.out.println(MainDrawingScene.imagenCardinalidadAlt);
			//ini.removeFromParent();
			//ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)), 15, 15);

			//((ini.setTexture(MainDrawingScene.imagenCardinalidadAlt);
			alitIni2.removeFromParent();
			ini.addChild(alitIni);
			///alitIni.sendToFront();
			
			//alitIni.setsetPositionRelativeToOther(ini, new Vector3D(0,0,0));
			
			//linea.addChild(componente);
			//componente.updateComponent(0);
			}*/
		componente.addChild(cardinalidadComponent);
	}
	
	
	class CardinalidadProcessor extends TapAndHoldProcessor  implements IGestureEventListener {
		boolean cardinalidadSwitch;//true=inicio, false= fin

		public CardinalidadProcessor(MTApplication pa, int tiempo, boolean cardinalidadSwitch) {
			super(pa,tiempo);
			this.cardinalidadSwitch=cardinalidadSwitch;
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean processGestureEvent(MTGestureEvent ge) {			
			TapAndHoldEvent th = (TapAndHoldEvent)ge;
																				
				switch (th.getId()) {
				case TapAndHoldEvent.GESTURE_STARTED:
					break;
				case TapAndHoldEvent.GESTURE_UPDATED:
					break;
				case TapAndHoldEvent.GESTURE_ENDED:
					if (th.isHoldComplete()){															
						final InputCursor m = th.getCursor();
						String canal=(MainDrawingScene.getListaUsuarios().get((int)m.sessionID)!=null)?MainDrawingScene.getListaUsuarios().get((int)m.sessionID).getCanal():Usuario.CANAL_DEFAULT_USER;
						int idUsuario=(MainDrawingScene.getListaUsuarios().get((int)m.sessionID)!=null)?(int)m.sessionID:Usuario.ID_DEFAULT_USER;
						
						server.getRoomOperations(canal).sendEvent("startEdition",new CardinalidadAdapter(((Relacion)objeto),this.cardinalidadSwitch,idUsuario));						
						System.out.println("Enviado "+canal+""+server.getRoomOperations(canal).getClients().size());
						break;
					}
					break;
				default:
					break;
				}
																							
			return false;
		}
	}


}




