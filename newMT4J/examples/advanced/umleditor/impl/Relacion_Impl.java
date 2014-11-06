package advanced.umleditor.impl;

import java.util.ArrayList;
import java.util.Iterator;
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
import advanced.umleditor.UMLFacade;
import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.socketio.CardinalidadAdapter;
import advanced.umleditor.socketio.EntidadAdapter;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Relacion_Impl extends MTComponent implements ObjetoUMLGraph{

	
	private SocketIOServer server;
	private final MTLine linea ;
	private final ObjetoUML objeto;
	final MTRoundRectangle halo;

	//private final ObjetoUML textoflotante;
	
	private final ObjetoUML textoflotInicio;
	private final ObjetoUML textoflotFin;

	private static String imagesPath = "data" + MTApplication.separator ;		
	private MTApplication mtApp;

	
	private ArrayList<Vector3D> listapuntos;
	private final MTApplication app ;
	MTEllipse ini=null;
	MTEllipse fin=null;
	
	private static final int CARDINALIDAD_LOCATION_DEFAULT=0;
	private static final int CARDINALIDAD_LOCATION_IZQUIERDA=1;
	private static final int CARDINALIDAD_LOCATION_DERECHA=2;
	private static final int CARDINALIDAD_LOCATION_ARRIBA=3;
	private static final int CARDINALIDAD_LOCATION_ABAJO=4;

	
	
	
	public Relacion_Impl(MTApplication mtApp, final MTComponent container, final MTCanvas canvas, final ObjetoUML objeto, final ObjetoUML texttoflotini, final ObjetoUML texttoflotfin, final UMLFacade recognizer,final SocketIOServer server) {
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
		this.textoflotInicio =texttoflotini;
		this.textoflotFin = texttoflotfin;
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
		PImage imagenCardinalidad = mtApp.loadImage(imagesPath + "uno.png");
		//backgroundImage.setSizeXYGlobal(10, 10);
		//this.getCanvas().addChild(backgroundImage);

		// Circulos al inicio y fin de la linea
		
		if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeigth()/2>((Relacion)objeto).getInicio().y)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)), 15, 15);
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().y+((Relacion)objeto).getObjetoInicio().getHeigth()/2<((Relacion)objeto).getInicio().y)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, -15)), 15, 15);		
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(15, 0)), 15, 15);
		else
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-15, 0)), 15, 15);

		/*if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(15, 0)), 15, 15);
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().x<((Relacion)objeto).getInicio().x)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-15, 0)), 15, 15);
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeigth()/2>((Relacion)objeto).getInicio().y)
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)), 15, 15);
		else 
			ini=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, -15)), 15, 15);
		*/
		
		//ini.setFillColor(ObjetoUMLGraph.azul);
		//ini.setNoFill(true); // Hacerlo Invisible
		//ini.addChild(backgroundImage);
		ini.setTexture(imagenCardinalidad);
		ini.setNoStroke(true);
		
		/*if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(15, 0)), 15, 15);
		else
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-15, 0)), 15, 15);
	*/
		
		
		if(((Relacion)objeto).getObjetoFin().getPosicion().y-((Relacion)objeto).getObjetoFin().getHeigth()/2>((Relacion)objeto).getFin().y)
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, 15)), 15, 15);
		else if(((Relacion)objeto).getObjetoFin().getPosicion().y+((Relacion)objeto).getObjetoFin().getHeigth()/2<((Relacion)objeto).getFin().y)
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, -15)), 15, 15);		
		else if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(15, 0)), 15, 15);
		else
			fin=new MTEllipse(mtApp, new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-15, 0)), 15, 15);

		fin.setTexture(imagenCardinalidad);
		//fin=new MTEllipse(mtApp,new Vector3D(((Relacion)objeto).getFin()), 15, 15);
		//fin.setFillColor(ObjetoUMLGraph.azul);
		//fin.setNoFill(true); // Hacerlo Invisible
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
			*/
				if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeigth()/2>((Relacion)objeto).getInicio().y)
					ini.setPositionGlobal( new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)));
				else if(((Relacion)objeto).getObjetoInicio().getPosicion().y+((Relacion)objeto).getObjetoInicio().getHeigth()/2<((Relacion)objeto).getInicio().y)
					ini.setPositionGlobal( new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, -15)));		
				else if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
					ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(15, 0)));
				else
					ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-15, 0)));

				
				
				////								
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
				*/
				if(((Relacion)objeto).getObjetoFin().getPosicion().y-((Relacion)objeto).getObjetoFin().getHeigth()/2>((Relacion)objeto).getFin().y)
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, 15)));
				else if(((Relacion)objeto).getObjetoFin().getPosicion().y+((Relacion)objeto).getObjetoFin().getHeigth()/2<((Relacion)objeto).getFin().y)
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, -15)));		
				else if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(15, 0)));
				else
					fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-15, 0)));

				///
				return false;
			}
		});
		
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
				objeto.getHeigth()+ObjetoUMLGraph.haloWidth, 1, 1, mtApp);		

		
		
		
		
		
		
		//halo.setNoFill(true);
		
		halo.setFillColor(ObjetoUMLGraph.haloSelected);
		
		halo.removeAllGestureEventListeners();		
		halo.setNoStroke(false);
		halo.addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
					AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
					InputCursor cursor = cursorInputEvt.getCursor();
					IMTComponent3D target = cursorInputEvt.getTargetComponent();
					System.out.println("Listener..............");
					
					//
					
					
					
					switch (cursorInputEvt.getId()) {
					case AbstractCursorInputEvt.INPUT_STARTED:
						
						listapuntos = new ArrayList<Vector3D>();
						
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						break;
					case AbstractCursorInputEvt.INPUT_UPDATED:
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						listapuntos.add(cursor.getPosition());
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:
						
						
						if(listapuntos.size() < 6){
							System.out.println("hijos: " +halo.getChildCount());
							
	
							
							
							if (textoflotInicio.getFigura() == null && textoflotFin.getFigura() == null ){
								//System.out.println("FIGURA ES :" + textoflotante.getFigura().toString());
								/*
								MTComponent bus = linea.getChildByName("TextoFlotanteImpl");
								System.out.println("FOUNDCOMP"  + bus);
								linea.removeChild(bus);
								bus.destroy();
								textoflotante.setFigura(null);*/
								
								
								TextoFlotanteImpl teximplinicio = new TextoFlotanteImpl(app, linea, canvas, recognizer, textoflotInicio, server);	
								teximplinicio.rectangulo.setName("TextoFlotanteImplINICIO");
								textoflotInicio.setFigura(teximplinicio);
								
								TextoFlotanteImpl teximplfin =  new TextoFlotanteImpl(app, linea, canvas, recognizer, textoflotFin, server);
								teximplinicio.rectangulo.setName("TextoFlotanteImplFIN");
								textoflotFin.setFigura(teximplfin);
						
								MTRoundRectangle textInicio = (MTRoundRectangle)(teximplinicio.rectangulo);
								textInicio.setPositionRelativeToParent(new Vector3D(ini.getCenterPointGlobal().x +20, ini.getCenterPointGlobal().y+20, ini.getCenterPointGlobal().z));
								
								MTRoundRectangle textFin = (MTRoundRectangle)(teximplfin.rectangulo);
								textFin.setPositionRelativeToParent(new Vector3D(fin.getCenterPointGlobal().x -20, fin.getCenterPointGlobal().y + 20, fin.getCenterPointGlobal().z));
							}

						}
						
				
						System.out.println("Reconocer:");
						ObjetoUML obj=recognizer.reconocerObjeto();
						if (obj ==ObjetoUML.DELETE_OBJECT_GESTURE){
							
							
							removerRelacion();
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
		if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeigth()/2>((Relacion)objeto).getInicio().y)
			ini.setPositionGlobal( new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, 15)));
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().y+((Relacion)objeto).getObjetoInicio().getHeigth()/2<((Relacion)objeto).getInicio().y)
			ini.setPositionGlobal( new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(0, -15)));		
		else if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
			ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(15, 0)));
		else
			ini.setPositionGlobal(new Vector3D(((Relacion)objeto).getInicio()).addLocal(new Vector3D(-15, 0)));
		
		if(((Relacion)objeto).getObjetoFin().getPosicion().y-((Relacion)objeto).getObjetoFin().getHeigth()/2>((Relacion)objeto).getFin().y)
			fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, 15)));
		else if(((Relacion)objeto).getObjetoFin().getPosicion().y+((Relacion)objeto).getObjetoFin().getHeigth()/2<((Relacion)objeto).getFin().y)
			fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(0, -15)));		
		else if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
			fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(15, 0)));
		else
			fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()).addLocal(new Vector3D(-15, 0)));

		//////
		
		
	
		
		//fin.setPositionGlobal(new Vector3D(((Relacion)objeto).getFin()));
		halo.setPositionGlobal(linea.getCenterPointGlobal());
		
		TextoFlotanteImpl impInicio = (TextoFlotanteImpl)textoflotInicio.getFigura();
		if(impInicio != null){
			
			
			impInicio.rectangulo.setPositionGlobal(new Vector3D(ini.getCenterPointGlobal().x +20, ini.getCenterPointGlobal().y +20, ini.getCenterPointGlobal().z));
			impInicio.halo.setPositionGlobal(new Vector3D(ini.getCenterPointGlobal().x +20, ini.getCenterPointGlobal().y +20, ini.getCenterPointGlobal().z));
		}
		
		TextoFlotanteImpl impFin = (TextoFlotanteImpl)textoflotFin.getFigura();
		if(impFin != null){
			impFin.rectangulo.setPositionGlobal(new Vector3D(fin.getCenterPointGlobal().x -20, fin.getCenterPointGlobal().y + 20, fin.getCenterPointGlobal().z));
			impFin.halo.setPositionGlobal(new Vector3D(fin.getCenterPointGlobal().x -20, fin.getCenterPointGlobal().y +20, fin.getCenterPointGlobal().z));
		}
		
		objeto.setPosicion(linea.getCenterPointGlobal());
		textoflotInicio.setPosicion(ini.getCenterPointGlobal());
		textoflotFin.setPosicion(fin.getCenterPointGlobal());
		
		Vector3D vFin=new Vector3D(((Relacion)objeto).getFin());
		Vector3D vInicio=new Vector3D(((Relacion)objeto).getInicio());
		Vector3D distancia=vFin.getSubtracted(vInicio);

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
	
	public void removerRelacion(){
		
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
		
		Entidad inicio=((Entidad)((Relacion)this.objeto).getObjetoInicio());
		System.out.println("Objetoo inicio"+inicio);
		inicio.getFigura().eliminarDatos(RELACIONES_INICIO_KEYWORD, this);
		
		Entidad fin=((Entidad)((Relacion)this.objeto).getObjetoFin());
		fin.getFigura().eliminarDatos(RELACIONES_FIN_KEYWORD, this);
		
	

		
		
		
		
	}
	@Override
	public void eliminarDatos(String keyword, Object datos) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private int ubicacionCardinalidad(MTPolygon componente){
		int ubicacion=CARDINALIDAD_LOCATION_DEFAULT;
		if(componente==ini){
			if(((Relacion)objeto).getObjetoInicio().getPosicion().y-((Relacion)objeto).getObjetoInicio().getHeigth()/2>((Relacion)objeto).getInicio().y)
				ubicacion=CARDINALIDAD_LOCATION_ARRIBA;
			else if(((Relacion)objeto).getObjetoInicio().getPosicion().y+((Relacion)objeto).getObjetoInicio().getHeigth()/2<((Relacion)objeto).getInicio().y)
				ubicacion=CARDINALIDAD_LOCATION_ABAJO;
			else if(((Relacion)objeto).getObjetoInicio().getPosicion().x>((Relacion)objeto).getInicio().x)
				ubicacion=CARDINALIDAD_LOCATION_IZQUIERDA;
			else
				ubicacion=CARDINALIDAD_LOCATION_DERECHA;
			
		}else if(componente==fin){
			
			if(((Relacion)objeto).getObjetoFin().getPosicion().y-((Relacion)objeto).getObjetoFin().getHeigth()/2>((Relacion)objeto).getFin().y)
				ubicacion=CARDINALIDAD_LOCATION_ARRIBA;
			else if(((Relacion)objeto).getObjetoFin().getPosicion().y+((Relacion)objeto).getObjetoFin().getHeigth()/2<((Relacion)objeto).getFin().y)
				ubicacion=CARDINALIDAD_LOCATION_ABAJO;
			else if(((Relacion)objeto).getObjetoFin().getPosicion().x>((Relacion)objeto).getFin().x)
				ubicacion=CARDINALIDAD_LOCATION_IZQUIERDA;
			else
				ubicacion=CARDINALIDAD_LOCATION_DERECHA;
		}
		return ubicacion;
	}
	public void actualizarCardinalidad(int cardinalidad,boolean cardinalidadSwitch){
		MTPolygon componente;
		int ubicacion=0; // 1=izquierda, 2 Derecha, 3 arriba, 4 abajo del componente
		if(cardinalidadSwitch){
			componente=ini;						
		}else{
			componente=fin;			
		}
		int ubicacionCardinalidad=ubicacionCardinalidad(componente);
		PImage imagenCardinalidad =null;
		String nombre="uno.png";	
		switch (cardinalidad) {
			case Relacion.CARDINALIDAD_UNO:						
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);				
				break;
			case Relacion.CARDINALIDAD_CERO_UNO:
				switch (ubicacionCardinalidad) {
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
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);				
				break;
			case Relacion.CARDINALIDAD_CERO_MUCHOS:
				switch (ubicacionCardinalidad) {
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
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);
				break;
			 
			case Relacion.CARDINALIDAD_UNO_MUCHOS:
				switch (ubicacionCardinalidad) {
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
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);
				break;
			
			case Relacion.CARDINALIDAD_MUCHOS:
				switch (ubicacionCardinalidad) {
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
				imagenCardinalidad = mtApp.loadImage(imagesPath + nombre);
				break;
			default:
				break;		
		}
		
		
		if(imagenCardinalidad!=null)
			componente.setTexture(imagenCardinalidad);
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
						String canal=(MainDrawingScene.getListaUsuarios().get((int)m.sessionID)!=null)?MainDrawingScene.getListaUsuarios().get((int)m.sessionID).getCanal():"canal1";
						int idUsuario=(MainDrawingScene.getListaUsuarios().get((int)m.sessionID)!=null)?(int)m.sessionID:-1;
						
						server.getRoomOperations(canal).sendEvent("cardinalidadEdition",new CardinalidadAdapter(((Relacion)objeto),this.cardinalidadSwitch,idUsuario));						
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




