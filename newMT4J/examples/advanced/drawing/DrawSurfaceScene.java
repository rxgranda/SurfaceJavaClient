package advanced.drawing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Recognizer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import advanced.umleditor.UMLDataSaver;
import advanced.umleditor.UMLFacade;
import advanced.umleditor.UMLRecognizer;
import advanced.umleditor.UMLCollection;
import advanced.umleditor.impl.Entidad_Impl;
import advanced.umleditor.impl.HaloHelper;
import advanced.umleditor.impl.ObjetoUMLGraph;
import advanced.umleditor.impl.RelacionTernaria_Impl;
import advanced.umleditor.impl.Relacion_Impl;
import advanced.umleditor.impl.TextoFlotanteImpl;
import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.RelacionTernaria;
import advanced.umleditor.logic.TextoFlotante;
import advanced.umleditor.logic.Usuario;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.Usuario;
import advanced.umleditor.socketio.CardinalidadAdapter;
import advanced.umleditor.socketio.EntidadAdapter;
import advanced.umleditor.socketio.TextoFlotanteAdapter;
import processing.core.PApplet;

import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.MTTextField;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class DrawSurfaceScene extends AbstractScene {	

	private MTApplication mtApp;

	private MTRectangle container;

	//Pintar el trazo que se est� dibujando
	private AbstractShape drawShape;

	// Utilizado para borrar el trazo dibujado
	private AbstractShape drawShape2;

	private float stepDistance;

	private Vector3D localBrushCenter;

	private float brushWidthHalf;

	private HashMap<InputCursor, Vector3D> cursorToLastDrawnPoint;

	private float brushHeightHalf;

	private float brushScale;

	private MTColor brushColor;

	private boolean dynamicBrush;
	
	public static float DEG_TO_RAD ;

	
	public static final int nroPtsConfirmaClick = 6;
	public static float MIN_HEIGHT, MIN_WIDTH,MAX_WIDTH,MAX_HEIGHT ;
	
	static Map< Integer, UMLFacade> listaRecognizer=new HashMap<Integer, UMLFacade>();
	static Map< Integer, UMLFacade> listaComponentes=new HashMap<Integer, UMLFacade>();
	static Map< Usuario, UMLFacade> listaComponentRecognizer=new HashMap<Usuario, UMLFacade>();
	Map< Usuario, HaloHelper> listaHaloHelper=new HashMap<Usuario, HaloHelper>();
	Map< Usuario, ArrayList<Vector3D>> listaPuntos=new HashMap<Usuario, ArrayList<Vector3D>>();
	private AbstractShape [] listaPencil; 

	
	
	
	private int paso=-1;
	
	private ArrayList<int[]> listaCoordenadasPlayer1;

	

	
	

	// TODO only works as lightweight scene atm because the framebuffer isnt
	// cleared each frame
	// TODO make it work as a heavywight scene
	// TODO scale smaller at higher speeds?
	// TODO eraser?
	// TODO get blobwidth from win7 touch events and adjust the brush scale
	/*ArrayList<Vector3D> puntos;
	//Vector3D puntoInicio, puntoFin;
	
	public void add(Vector3D vec) {
		puntos.add(vec);
	}

	public synchronized void eliminarPuntos() {
		puntos = new ArrayList<Vector3D>();
	}
*/
	public void limpiarPuntosCanvas( final Usuario user) {
		registerPreDrawAction(new IPreDrawAction() {			
			public void processAction() {
				Vector3D ultimo = null;
				for (Vector3D vec :listaPuntos.get(user)) {
					boolean firstPoint = false;
					Vector3D lastDrawnPoint = ultimo;
					Vector3D pos = new Vector3D(vec.x, vec.y, 0);
					// Proyecto
					// //System.out.println("ID: " + m.sessionID);
					////System.out.println("Eliminar: X:" + vec.x + "Y:" + vec.y);

					// Proyecto
					if (lastDrawnPoint == null) {
						lastDrawnPoint = new Vector3D(pos);
						ultimo = new Vector3D(pos);
						firstPoint = true;
					} else {
						if (lastDrawnPoint.equalsVector(pos))
							return;
					}

					float scaledStepDistance = stepDistance * brushScale;

					Vector3D direction = pos.getSubtracted(lastDrawnPoint);
					float distance = direction.length();
					direction.normalizeLocal();
					direction.scaleLocal(scaledStepDistance);

					float howManySteps = distance / scaledStepDistance;
					int stepsToTake = Math.round(howManySteps);

					// Force draw at 1st point
					if (firstPoint && stepsToTake == 0) {
						stepsToTake = 1;
					}
					// //System.out.println("Steps: " + stepsToTake);

					// GL gl = Tools3D.getGL(mtApp);
					// gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA,
					// GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_ONE,
					// GL.GL_ONE_MINUS_SRC_ALPHA);

					mtApp.pushMatrix();
					// We would have to set up a default view here for
					// stability? (default cam etc?)
					getSceneCam().update();

					Vector3D currentPos = new Vector3D(lastDrawnPoint);
					for (int i = 0; i < stepsToTake; i++) { // start i at 1? no,
															// we add first step
															// at 0 already
						currentPos.addLocal(direction);
						// Draw new brush into FBO at correct position
						Vector3D diff = currentPos
								.getSubtracted(localBrushCenter);
						// Vector3D diff= new Vector3D(currentPos);
						mtApp.pushMatrix();
						mtApp.translate(diff.x, diff.y);
						// //System.out.println("X:"+diff.x+"Y:"+ diff.y);

						// NOTE: works only if brush upper left at 0,0
						mtApp.translate(brushWidthHalf, brushHeightHalf);
						mtApp.scale(brushScale);

						if (dynamicBrush) {
							// Rotate brush randomly
							// mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(0,
							// 179)));
							// mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(-85,
							// 85)));
							// mtApp.rotateZ(PApplet.radians(ToolsMath.getRandom(-25,
							// 25)));
							// mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(-9,
							// 9)));
							mtApp.translate(-brushWidthHalf, -brushHeightHalf);
						}

						/*
						 * //Use random brush from brushes int brushIndex =
						 * Math.round(Tools3D.getRandom(0, brushes.length-1));
						 * AbstractShape brushToDraw = brushes[brushIndex];
						 */
						AbstractShape brushToDraw = drawShape2;

						// Draw brush
						brushToDraw.drawComponent(mtApp.g);

						// mtApp.translate(diff.x + 10, diff.y +10);
						// brushToDraw = drawShape;

						// Draw brush
						// brushToDraw.drawComponent(mtApp.g);
						// brushToDraw = drawShape2;
						// brushToDraw.drawComponent(mtApp.g);

						mtApp.popMatrix();
					}
					mtApp.popMatrix();
					ultimo = new Vector3D(currentPos);/*
													 * mtApp.pushMatrix();
													 * getSceneCam().update();
													 * mtApp.pushMatrix();
													 * AbstractShape brushToDraw
													 * = drawShape2;
													 * //System.out.println
													 * ("Eliminar: X:"
													 * +vec.x+"Y:"+ vec.y);
													 * mtApp.translate(vec.x,
													 * vec.y); //Draw brush
													 * brushToDraw
													 * .drawComponent(mtApp.g);
													 * mtApp.popMatrix();
													 * mtApp.popMatrix();
													 */

				}
				// //System.out.println("Eliminado");
				listaPuntos.remove(user);
				ArrayList<Vector3D> puntos=new ArrayList<Vector3D>();
				listaPuntos.put(user, puntos);
				
			}

			@Override
			public boolean isLoop() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		//
	}

	public DrawSurfaceScene(MTApplication mtApplication, String name,
			final MTRectangle container,final SocketIOServer server, final Map<Integer, Usuario> listaUsuarios) {

		super(mtApplication, name);
		this.mtApp = mtApplication;
		this.container = container;

		this.getCanvas().setDepthBufferDisabled(true);
		listaCoordenadasPlayer1 = new ArrayList<int[]>();
		DEG_TO_RAD = (float) (180.0/Math.PI);

		MIN_HEIGHT = (float) mtApplication.height * MT4jSettings.getMinimumHeightRatio();
		MIN_WIDTH = (float) mtApplication.width * MT4jSettings.getMinimumWidthRatio();
		MAX_HEIGHT = (float) mtApplication.height * MT4jSettings.getMaximumHeightRatio();
		MAX_WIDTH = (float) mtApplication.width * MT4jSettings.getMaximumWidthRatio();		
		

		/*
		 * this.drawShape = getDefaultBrush(); this.localBrushCenter =
		 * drawShape.getCenterPointLocal(); this.brushWidthHalf =
		 * drawShape.getWidthXY(TransformSpace.LOCAL)/2f; this.brushHeightHalf =
		 * drawShape.getHeightXY(TransformSpace.LOCAL)/2f; this.stepDistance =
		 * brushWidthHalf/2.5f;
		 */
		
		SocketIONamespace userListener = MainDrawingScene.getLoginListener();	        	        
		userListener.addEventListener("endEdition", EntidadAdapter.class, new DataListener<EntidadAdapter>() {
			@Override
			public void onData(SocketIOClient arg0, EntidadAdapter arg1,
					AckRequest arg2){
					try{
					//System.out.println(arg1.getId()+" "+arg1.getNombre());
					System.out.println("**Iniciar Edicion Entidad"+ arg1.getNombre());

					//listaRecognizer.get(arg1.getIdUsuario());
					ObjetoUML objeto=UMLFacade.getObjetoUML(arg1.getId());
					//System.out.println("objeto "+objeto);
					if(objeto instanceof Entidad){
						Entidad entidad=(Entidad)objeto;
						UndoHelper.agregarAccion(UndoHelper.EDITAR_OBJETO_ACTION, objeto);
						arg1.actualizar(entidad);
						//System.out.println("Nombre objeto:"+entidad.getNombre());
						objeto.getFigura().actualizarEtiquetas();
						UMLDataSaver.agregarAccion(UMLDataSaver.EDITAR_OBJETO_ACTION, objeto,listaUsuarios.get(arg1.getIdUsuario()) );
						server.getNamespace("/login").getBroadcastOperations().sendEvent("syncEdition",new EntidadAdapter(((Entidad)objeto),arg1.getIdUsuario(),-1));
					}
					}catch (Exception e){
						System.out.println("ERROR endEdition Listener");	
					}
					System.out.println("**Terminar Edicion Entidad"+ arg1.getNombre());
				}
        });        	
		
		
		userListener.addEventListener("endEditionTexto", TextoFlotanteAdapter.class, new DataListener<TextoFlotanteAdapter>() {
			@Override
			public void onData(SocketIOClient arg0, TextoFlotanteAdapter arg1,
					AckRequest arg2){
				System.out.println("**Iniciar Edicion Texto"+ arg1.getNombre());
					try{
				//	listaRecognizer.get(arg1.getIdUsuario());
					//System.out.println(arg1.getId()+" "+arg1.getNombre());
					ObjetoUML objeto=UMLFacade.getObjetoUML(arg1.getId());
					//System.out.println("objeto "+objeto);
					if(objeto instanceof TextoFlotante){
						TextoFlotante textflot=(TextoFlotante)objeto;
						if (textflot.getOwner() != null){
							ObjetoUML tem = textflot.getOwner();
							UndoHelper.agregarAccion(UndoHelper.EDITAR_OBJETO_ACTION, tem);
							if (tem instanceof Relacion){								
								arg1.actualizar(textflot);								
								objeto.getFigura().actualizarEtiquetas();
								UMLDataSaver.agregarAccion(UMLDataSaver.EDITAR_OBJETO_ACTION, tem,listaUsuarios.get(arg1.getIdUsuario()));
							}
						}

						
					}
					}catch (Exception e){
						System.out.println("ERROR endEditionTextoListener listener");
					}
					System.out.println("**Finalizar Edicion Texto"+ arg1.getNombre());

			}
        });        	
		
		
		
		userListener.addEventListener("cardinalidadEdition", CardinalidadAdapter.class, new DataListener<CardinalidadAdapter>() {
			
			@Override
			public void onData(SocketIOClient arg0,  CardinalidadAdapter cardinalidadAdpter,
					AckRequest arg2){
				System.out.println("**Iniciar Edicion cardinalidad");

					try{
					//listaRecognizer.get(cardinalidadAdpter.getIdUsuario());
					//System.out.println(cardinalidadAdpter.getId()+" "+cardinalidadAdpter.getCardinalidad());
					ObjetoUML objeto=UMLFacade.getObjetoUML(cardinalidadAdpter.getId());
					//System.out.println("objeto "+objeto);
					if(objeto instanceof Relacion){
						Relacion relacion=(Relacion)objeto;
						UndoHelper.agregarAccion(UndoHelper.EDITAR_OBJETO_ACTION, objeto);
						//System.out.println("CARD :" + cardinalidadAdpter.getCardinalidad() + "CARD SWITCH:" + cardinalidadAdpter.isCardinalidadSwitch());
						((Relacion_Impl)relacion.getFigura()).actualizarCardinalidad(cardinalidadAdpter.getCardinalidad(), cardinalidadAdpter.isCardinalidadSwitch());																
						UMLDataSaver.agregarAccion(UMLDataSaver.EDITAR_OBJETO_ACTION, objeto,listaUsuarios.get(cardinalidadAdpter.getIdUsuario()) );
					}
					}catch (Exception e){
						System.out.println("ERROR cardinalidadEdition listener");
					}
					System.out.println("**Finalizar Edicion cardinalidad");
			}
        });        	
		
		
		
		this.brushColor = new MTColor(0, 0, 0);
		this.brushScale = 0.05f;
		this.dynamicBrush = true;
		// this.stepDistance = 5.5f;

		this.cursorToLastDrawnPoint = new HashMap<InputCursor, Vector3D>();
		
		Set<Integer> keys=listaUsuarios.keySet();
		for (Integer key:keys){
			//System.out.println("Usuariossss:   "+key);
			// Proyecto
			Usuario user=listaUsuarios.get(key);
			//System.out.println("User::::"+user);
			final UMLFacade recognizer = new UMLFacade(user); //para el canvas
			final UMLFacade componentRecognizer = new UMLFacade(user); // para reconocer gestos de los componentes
			ArrayList<Vector3D> puntos= new ArrayList<Vector3D>();			
			HaloHelper helper=new HaloHelper();
			listaRecognizer.put(user.getIdPluma(), recognizer);
			listaComponentRecognizer.put(user, componentRecognizer);
			listaPuntos.put(user, puntos);		
			listaHaloHelper.put(user, helper);
		}

		final Usuario defaultUser= new Usuario(Usuario.ID_DEFAULT_USER, Usuario.NOMBRE_DEFAULT_USER, Usuario.CANAL_DEFAULT_USER, -1);
		final UMLFacade recognizer = new UMLFacade(defaultUser); //para el canvas
		final UMLFacade componentRecognizer = new UMLFacade(defaultUser); // para reconocer gestos de los componentes
		ArrayList<Vector3D> puntos= new ArrayList<Vector3D>();
		HaloHelper helper=new HaloHelper();
		listaRecognizer.put(defaultUser.getIdPluma(), recognizer);
		listaComponentRecognizer.put(defaultUser, componentRecognizer);
		listaPuntos.put(defaultUser, puntos);
		listaHaloHelper.put(defaultUser, helper);
		listaUsuarios.put(Usuario.ID_DEFAULT_USER, defaultUser);
		
		
		
		
		// Proyecto
		this.getCanvas().registerInputProcessor(new TapProcessor(mtApplication));
		this.getCanvas().addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent de = (TapEvent)ge;
				final InputCursor m = de.getCursor();
				final Usuario currentUser=(listaUsuarios.get((int)m.sessionID)!=null)?listaUsuarios.get((int)m.sessionID):defaultUser;
				
				
				//Moves the component
				switch (de.getId()) {
				case MTGestureEvent.GESTURE_STARTED:
					//listaConfirmarPunto.add(m.getPosition());
					//System.out.println("ONE CLCIK START!!");
					break;
				case MTGestureEvent.GESTURE_UPDATED:
					//listaConfirmarPunto.add(m.getPosition());          
					//System.out.println("ONE CLCIK UPDATE!!");
					break;
				case MTGestureEvent.GESTURE_ENDED:
					//System.out.println("ONE CLCIK END!!");
					
					//if(listaConfirmarPunto.size() < nroPtsConfirmaClick){
						/*//System.out.println("CLICCCCKKKKKKKKK!!!!!");
						UMLFacade recognizer=listaRecognizer.get(currentUser.getIdPluma());
						ObjetoUML objeto = recognizer.aniadirTextoFlotante(m.getPosition());
						TextoFlotanteImpl teximpl = new TextoFlotanteImpl(mtApp, container, getCanvas(), recognizer, objeto, server);
						objeto.setFigura(teximpl);*/
					//}
					break;
				default:
					break;
				}		
				return false;
			}
		});

		this.getCanvas().addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) {
					
					final AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt) inEvt;
					final InputCursor m = posEvt.getCursor();
					final Usuario currentUser=(listaUsuarios.get((int)m.sessionID)!=null)?listaUsuarios.get((int)m.sessionID):defaultUser;				
					
					
					if(currentUser!=null){
					IMTComponent3D componente = m.getTarget();
					
					
					System.out.println("COMPONENTE: "+componente.toString());
				
					
					IMTComponent3D currentComponent = (IMTComponent3D) getCanvas().getComponentAt((int) m.getPosition().x,(int) m.getPosition().y);
					System.out.println("CURRENT: "+currentComponent.toString());
					//Object entidad = null,entidad2=null;
					
					//switch para establecer relaciones entre entidades
					switch (((AbstractCursorInputEvt) inEvt).getId()) {
					case AbstractCursorInputEvt.INPUT_STARTED:
						Object entidad=((MTComponent)componente).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);
						if (entidad!=null&&entidad instanceof ObjetoUMLGraph){
							((MTPolygon)((ObjetoUMLGraph)entidad).getHalo()).setFillColor(ObjetoUMLGraph.haloSelected);
							((ObjetoUMLGraph)entidad).getHalo().sendToFront();

							////System.out.println("Pintandoooooo");
						}
						////System.out.println("Input detected on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
						listaHaloHelper.remove(currentUser);
						HaloHelper helper= new HaloHelper();
						listaHaloHelper.put(currentUser,helper);
						break;
					case AbstractCursorInputEvt.INPUT_UPDATED:
						 	
						if(componente!=currentComponent){
							 Object entidad2=((MTComponent)currentComponent).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);
							 listaHaloHelper.get(currentUser).setHoverFueraHalo(true);
							 if (entidad2 instanceof ObjetoUMLGraph){
								((MTPolygon)((ObjetoUMLGraph)entidad2).getHalo()).setFillColor(ObjetoUMLGraph.haloSelected);
								((ObjetoUMLGraph)entidad2).getHalo().sendToFront();
								
								////System.out.println("Pintandoooooo");
								//@SuppressWarnings("rawtypes")
								LinkedList listaVisitados=(LinkedList<MTComponent>) ((MTComponent)componente).getUserData(ObjetoUMLGraph.COMPONENTES_VISITADOS_KEYWORD);
								
								//Obtiene ultimo componente
								Object ultimo=null;
								if(listaVisitados==null){
									listaVisitados=new LinkedList<MTComponent>();
									((MTComponent)componente).setUserData(ObjetoUMLGraph.COMPONENTES_VISITADOS_KEYWORD,listaVisitados);
									
								}
								
								if(listaVisitados.size()>0)
								ultimo=listaVisitados.getLast();
								
								if(ultimo!=null && !ultimo.equals(entidad2))
								listaHaloHelper.get(currentUser).setHoverFin(m.getPosition());
							
								listaVisitados.add(entidad2);
								
								 
								if(ultimo!=null)
								System.out.println("ULTIMO: "+ultimo.toString());
								
								if (listaHaloHelper.get(currentUser).getHoverFin().equalsVector(new Vector3D()))
									//System.out.println("hover fin IGUAL A NEW VECTOR");
									listaHaloHelper.get(currentUser).setHoverFin(m.getPosition());
								
								ultimo=(IMTComponent3D) currentComponent;
								if(ultimo!=null)
									System.out.println("ULTIMO2: "+ultimo.toString());
								
							}else{
								//Aqui reinicio el punto final de la linea relacion 
								//Esto corrige bug relacion entidad intermedia
								listaHaloHelper.get(currentUser).setHoverFin(new Vector3D());
							}

							
						}else{
							listaHaloHelper.get(currentUser).setHoverInicio(m.getPosition());
							
						}
						System.out.println("hover inicio "+listaHaloHelper.get(currentUser).getHoverInicio());
						System.out.println("hover fin "+listaHaloHelper.get(currentUser).getHoverFin());
						
						
						/*Entidad_Impl alt_ent=(Entidad_Impl) ((MTComponent)currentComponent).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);
						if(componente==currentComponent){
							listaHaloHelper.get(currentUser).setHoverInicio(m.getPosition());
						}else if( alt_ent!=null){
							if(alt_ent.getHalo()!=null)
								if(alt_ent.getHalo()==currentComponent)
									listaHaloHelper.get(currentUser).setHoverInicio(m.getPosition());
						}*/
							
						//	//System.out.println("Holaaa Input updated on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());			
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:
						Object oEntidad=((MTComponent)componente).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);
						if (oEntidad instanceof ObjetoUMLGraph){
							((MTPolygon)((ObjetoUMLGraph)oEntidad).getHalo()).setFillColor(ObjetoUMLGraph.haloDeSelected);
							////System.out.println("Pintandoooooo");
						}
						if(componente!=currentComponent){
					//		if (listaHaloHelper.get(currentUser).getHoverFin().equalsVector(new Vector3D()))
						//		listaHaloHelper.get(currentUser).setHoverFin(m.getPosition());	
						
							Object entidad2=((MTComponent)currentComponent).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);
							if (entidad2 instanceof ObjetoUMLGraph){
								((MTPolygon)((ObjetoUMLGraph)entidad2).getHalo()).setFillColor(ObjetoUMLGraph.haloDeSelected);
								
							}
							
							
						}
						//AQUI YO
						System.out.println("hover inicio "+listaHaloHelper.get(currentUser).getHoverInicio());
						System.out.println("hover fin "+listaHaloHelper.get(currentUser).getHoverFin());
						
						//HASTA AQUI
						
						LinkedList <MTComponent>listaVisitados=(LinkedList) ((MTComponent)componente).getUserData(ObjetoUMLGraph.COMPONENTES_VISITADOS_KEYWORD);
						if(listaVisitados!=null){
							for(MTComponent componenteVisitado:listaVisitados){
								((MTPolygon)((ObjetoUMLGraph)componenteVisitado).getHalo()).setFillColor(ObjetoUMLGraph.haloDeSelected);
							}
						}
						break;
					default:
						break;
					}
				
					
					
					////System.out.println("SECOND: " + currentComponent);
					// getCanvas().drawAndUpdateCanvas(mtApp.g, 0);
					//getCanvas().drawAndUpdateCanvas(mtApp.g, 0);
					// getSceneCam().update();
					//if (comp instanceof MTCanvas) {
						// //System.out.println("PrevPos: " + prevPos);
						if (posEvt.getId() != AbstractCursorInputEvt.INPUT_ENDED) {
							registerPreDrawAction(new IPreDrawAction() {
								public void processAction() {
									boolean firstPoint = false;
									Vector3D lastDrawnPoint = cursorToLastDrawnPoint
											.get(m);
									Vector3D pos = new Vector3D(posEvt.getX(),
											posEvt.getY(), 0);
									
									if(posEvt.getId()==0)
										paso++;
									
									//System.out.println("posEvt: "+posEvt.getId()+"");
									
									listaCoordenadasPlayer1.add(new int[]{Math.round(posEvt.getX()),Math.round(posEvt.getY()),paso});// X,Y,IdCursor
								
									// Proyecto
									// //System.out.println("ID: " + m.sessionID);
									// //System.out.println("Pos: X:"+posEvt.getX()+"Y:"+
									// posEvt.getY());
									
									//Puntos para borrar el canvas									
									listaPuntos.get(currentUser).add(new Vector3D(posEvt.getX(), posEvt.getY()));									
									if(listaPuntos.get(currentUser).size()>1000000){
										listaPuntos.remove(currentUser);
										ArrayList<Vector3D> puntos=new ArrayList<Vector3D>();
										listaPuntos.put(currentUser, puntos);
									}
										
									
									//add(new Vector3D(posEvt.getX(), posEvt
										//	.getY(), 0));

									// Proyecto
									if (lastDrawnPoint == null) {
										lastDrawnPoint = new Vector3D(pos);
										cursorToLastDrawnPoint.put(m,
												lastDrawnPoint);
										// test
										// anterior= new Vector3D(pos);//->
										// esquinaA= new Vector3D(pos);
										// esquinaB= new Vector3D(pos);
										//centroideX = 0;
										//centroideY = 0;
										//numMuestras = 0;

										// test
										firstPoint = true;
									} else {
										if (lastDrawnPoint.equalsVector(pos))
											return;
									}
									/*
									 * if(minX>pos.x) minX=pos.x; if(minY>pos.y)
									 * minY=pos.y; if(maxX<pos.x) maxX=pos.x;
									 * if(MaxY<pos.y) MaxY=pos.y;
									 */

									// centroideX+=pos.x;centroideY+=pos.y;
									// numMuestras++;

									float scaledStepDistance = stepDistance
											* brushScale;

									Vector3D direction = pos
											.getSubtracted(lastDrawnPoint);
									float distance = direction.length();
									direction.normalizeLocal();
									direction.scaleLocal(scaledStepDistance);

									float howManySteps = distance
											/ scaledStepDistance;
									int stepsToTake = Math.round(howManySteps);

									// Force draw at 1st point
									if (firstPoint && stepsToTake == 0) {
										stepsToTake = 1;
									}
									// //System.out.println("Steps: " +
									// stepsToTake);

									// GL gl = Tools3D.getGL(mtApp);
									// gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA,
									// GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_ONE,
									// GL.GL_ONE_MINUS_SRC_ALPHA);

									mtApp.pushMatrix();
									// We would have to set up a default view
									// here for stability? (default cam etc?)
									getSceneCam().update();

									Vector3D currentPos = new Vector3D(
											lastDrawnPoint);
									for (int i = 0; i < stepsToTake; i++) { // start
																			// i
																			// at
																			// 1?
																			// no,
																			// we
																			// add
																			// first
																			// step
																			// at
																			// 0
																			// already
										currentPos.addLocal(direction);
										 //System.out.println("Ojooo: ID: " + m.sessionID);
										//Usuario currentUser=listaUsuarios.get((int)m.sessionID);
										UMLFacade recognizer=listaRecognizer.get(currentUser.getIdPluma());
										recognizer.anadirPunto(currentPos.x,currentPos.y);
										// centroideX+=currentPos.x;centroideY+=currentPos.y;
										// numMuestras++;

										// Draw new brush into FBO at correct
										// position
										Vector3D diff = currentPos
												.getSubtracted(localBrushCenter);
										// Vector3D diff= new
										// Vector3D(currentPos);
										mtApp.pushMatrix();
										mtApp.translate(diff.x, diff.y);
										// //System.out.println("X:"+diff.x+"Y:"+
										// diff.y);
										// centroideX+=currentPos.x+diff.x;centroideY+=currentPos.y+diff.y;
										// numMuestras++;
										// recognizer.anadirPunto(currentPos.x+diff.x,
										// currentPos.y+diff.y);

										/*
										 * /test
										 * if(currentPos.x>anterior.x&&currentPos
										 * .y>anterior.y) esquinaB=new
										 * Vector3D(new
										 * Vector3D(currentPos.x-diff.x,
										 * currentPos.y-diff.y,0)); if
										 * (currentPos
										 * .x<anterior.x&&currentPos.y
										 * <anterior.y) esquinaA=new
										 * Vector3D(new
										 * Vector3D(currentPos.x-diff.x,
										 * currentPos.y-diff.y,0)); anterior=new
										 * Vector3D(currentPos.x,
										 * currentPos.y,0); //test
										 */

										// add(new Vector3D(currentPos.x+diff.x,
										// currentPos.y+diff.y,0));
										// NOTE: works only if brush upper left
										// at 0,0
										mtApp.translate(brushWidthHalf,
												brushHeightHalf);
										mtApp.scale(brushScale);

										if (dynamicBrush) {
											// Rotate brush randomly
											// mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(0,
											// 179)));
											// mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(-85,
											// 85)));
											// mtApp.rotateZ(PApplet.radians(ToolsMath.getRandom(-25,
											// 25)));
											// mtApp.rotateZ(PApplet.radians(Tools3D.getRandom(-9,
											// 9)));
											mtApp.translate(-brushWidthHalf,
													-brushHeightHalf);
										}

										/*
										 * //Use random brush from brushes int
										 * brushIndex =
										 * Math.round(Tools3D.getRandom(0,
										 * brushes.length-1)); AbstractShape
										 * brushToDraw = brushes[brushIndex];
										 */
										AbstractShape brushToDraw ;
										switch (currentUser.getIdPluma()) { // escoger  el color de la pluma de acuerdo al idUsuario
										case 1: // Rojo
											brushToDraw=listaPencil[1];
											break;
										case 2: // Azul
											brushToDraw=listaPencil[2];
											break;
										case 3: // Verde
											brushToDraw=listaPencil[3];
											break;
										case 4: // Naranja
											brushToDraw=listaPencil[4];
											break;
										case 5: // Amarillo
											brushToDraw=listaPencil[5];
											break;
										default: // Negro
											brushToDraw = drawShape;
											//brushToDraw=listaPencil[5];
											break;
										}
										

										// Draw brush
										brushToDraw.drawComponent(mtApp.g);

										// mtApp.translate(diff.x + 10, diff.y
										// +10);
										// brushToDraw = drawShape;

										// Draw brush
										// brushToDraw.drawComponent(mtApp.g);
										// brushToDraw = drawShape2;
										// brushToDraw.drawComponent(mtApp.g);

										mtApp.popMatrix();
									}
									mtApp.popMatrix();
									cursorToLastDrawnPoint.put(m, currentPos);

								}

								public boolean isLoop() {
									return false;
								}
							});
						} else {
							cursorToLastDrawnPoint.remove(m);
							 /*MTRoundRectangle a=new
										 MTRoundRectangle(200,200,0,
										 500,
										 500, 1, 1, mtApp);
							 a.setStrokeColor(new MTColor(0,0,0));
							 a.setNoFill(true);
							a.setPickable(false);
							a.setNoStroke(false);
							 getCanvas().addChild(a);
							 getCanvas().sendToFront();*/
							// int resultado=recognizer.recognize();
							limpiarPuntosCanvas(currentUser);
							UMLFacade recognizer=listaRecognizer.get(currentUser.getIdPluma());
							final ObjetoUML objeto=recognizer.reconocerObjeto();
							final int tipo_objeto = objeto.getTipo();
							//System.out.println("tipo objeto: "+tipo_objeto);
							// if(resultado==UMLCollection.INVALIDO){
							
							// }else{
							// UMLCollection.anadirObjeto(resultado,persona );
							// eliminarPuntos();
							// }

							if( listaPuntos.get(currentUser).size() < 200 ){

								
								
								//System.out.println("CLICCCCKKKKKKKKK!!!!!");
								
								
							}
							if (objeto != ObjetoUML.OBJETO_INVALIDO) {
							


								// setBrushColor2(new MTColor(255,0,0));

								// centroideX=centroideX/numMuestras-5;
								// centroideY=centroideY/numMuestras-5;
								// drawShape2.setFillColor(new
								// MTColor(255,0,0,255));
								/*
								 * mtApp.pushMatrix(); getSceneCam().update();
								 * mtApp
								 * .translate(recognizer.getCentroide().x,recognizer
								 * .getCentroide().y); mtApp.scale(brushScale);
								 * AbstractShape brushToDraw = drawShape2;
								 * brushToDraw.drawComponent(mtApp.g);
								 * 
								 * mtApp.popMatrix();
								 */
								// MTEllipse ellipse = new MTEllipse(mtApp, new
								// Vector3D((float)centroideX,(float)centroideY,0),
								// 60, 40);
								// MTRoundRectangle a=new
								// MTRoundRectangle(recognizer.getPosicion().x,recognizer.getPosicion().y,0,
								// recognizer.getWidth(),
								// recognizer.getHeigth(), 1, 1, mtApp);
								UMLFacade componentRecognizer = listaComponentRecognizer.get(currentUser);

								switch (tipo_objeto) {

								case ObjetoUML.ENTIDAD:
									
									ObjetoUMLGraph diagrama= new Entidad_Impl(mtApp,container,getCanvas() ,componentRecognizer,objeto,server);									
									objeto.setFigura(diagrama);
									UMLDataSaver.agregarAccion(UMLDataSaver.AGREGAR_OBJETO_ACTION,objeto,currentUser);
									UndoHelper.agregarAccion(UndoHelper.AGREGAR_OBJETO_ACTION, objeto);

									//anadirObjeto(diagrama.getFigura());
									break;
								case ObjetoUML.RELACION:
									final IMTComponent3D destino=getCanvas().getComponentAt((int)m.getCurrentEvtPosX(), (int)m.getCurrentEvtPosY());
									if(componente instanceof MTPolygon && destino instanceof MTPolygon){ //verificar si el componente inicial y final son Instancias de Polygon(Diagrama entidad)
										
										Object entidad1=((MTComponent)componente).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);
										Object entidad2=((MTComponent)destino).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);

										if(entidad1!=null&&entidad2!=null&&entidad1 instanceof ObjetoUMLGraph && entidad2 instanceof ObjetoUMLGraph ){
											
											//Reubicar objeto relacion
											  HaloHelper helper=listaHaloHelper.get(currentUser);
											  //System.out.println("try resize");
											  
											  if(entidad1!=entidad2){
												  if(!helper.getHoverInicio().equalsVector(new Vector3D())&&!helper.getHoverFin().equalsVector(new Vector3D()))
												  {
													 ((Relacion)objeto).setInicio(helper.getHoverInicio());
													 ((Relacion)objeto).setFin(helper.getHoverFin());
													 listaHaloHelper.remove(currentUser);
													 helper=new HaloHelper();
													 listaHaloHelper.put(currentUser, helper);
													// System.out.println("resize done!!!!!!!!!!!");
												  }
											  }else{
												 
												  if(entidad1 instanceof Entidad_Impl){	
													 Entidad oEntidad= (Entidad)((Entidad_Impl)entidad1).getObjetoUML();
													  if(!oEntidad.isTieneRelacionRecursiva()&&listaHaloHelper.get(currentUser).isHoverFueraHalo()){// Si aun no tiene relacion recursiva
														  
														  Vector3D puntoInicio=new Vector3D(oEntidad.getPosicion()).getAdded(new Vector3D(-oEntidad.getWidth()/2,-oEntidad.getHeight()/2-ObjetoUMLGraph.TAMANO_CARDINALIDAD));
														  Vector3D puntoFin=new Vector3D(oEntidad.getPosicion()).getAdded(new Vector3D(oEntidad.getWidth()/2,-oEntidad.getHeight()/2-ObjetoUMLGraph.TAMANO_CARDINALIDAD));
														  ((Relacion)objeto).setInicio(puntoInicio);
														  ((Relacion)objeto).setFin(puntoFin);

														  ((Entidad)((Entidad_Impl)entidad1).getObjetoUML()).setTieneRelacionRecursiva(true);
														 
													  	} else{// si ya tiene relacion recursiva
													  		 break;
													  	}
														  
												  }
													  
												  
											  }
											//
											((Relacion)objeto).setObjetoInicio(((ObjetoUMLGraph)entidad1).getObjetoUML());
											((Relacion)objeto).setObjetoFin(((ObjetoUMLGraph)entidad2).getObjetoUML());
											
											TextoFlotante objetotextoInicio = (TextoFlotante)recognizer.aniadirTextoFlotante(new Vector3D(helper.getHoverInicio().x + 20,helper.getHoverInicio().y +20,helper.getHoverInicio().z));
											TextoFlotante objetotextoFin = (TextoFlotante)recognizer.aniadirTextoFlotante(new Vector3D(helper.getHoverFin().x + 20,helper.getHoverFin().y +20,helper.getHoverFin().z));
											objetotextoInicio.setOwner(((Relacion)objeto)); 
											objetotextoFin.setOwner(((Relacion)objeto)); 
											
											((Relacion)objeto).setTextoInicio(objetotextoInicio);
											((Relacion)objeto).setTextoFin(objetotextoFin);
	
											ObjetoUMLGraph linea= new Relacion_Impl(mtApp, container, getCanvas(), objeto, componentRecognizer, server);
											//((MTPolygon)((ObjetoUMLGraph)entidad1).getHalo()).setFillColor(ObjetoUMLGraph.haloDeSelected);	 										
											//((MTPolygon)((ObjetoUMLGraph)entidad2).getHalo()).setFillColor(ObjetoUMLGraph.haloDeSelected);
											((ObjetoUMLGraph)entidad1).guardarDatos(ObjetoUMLGraph.RELACIONES_INICIO_KEYWORD, linea);
											((ObjetoUMLGraph)entidad2).guardarDatos(ObjetoUMLGraph.RELACIONES_FIN_KEYWORD, linea);

											objeto.setFigura(linea);	
											UMLDataSaver.agregarAccion(UMLDataSaver.AGREGAR_OBJETO_ACTION,objeto, currentUser);
											UndoHelper.agregarAccion(UndoHelper.AGREGAR_OBJETO_ACTION, objeto);

											//anadirObjeto(linea.getFigura());
											
										}
										
										

										/**Este el codigo que permite unir relacion_multiples con las entidades, pero yendo desde una  relacion multiple
										 * hasta una entidad */
										Object entidad1_aux=((MTComponent)componente).getUserData(ObjetoUMLGraph.RELACION_MULTIPLE_KEYWORD);
										Object entidad2_aux=((MTComponent)destino).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);
										System.out.println("imprimiendo entidad1_aux: "+entidad1_aux);
										if(entidad1_aux!=null&&entidad2_aux!=null){ //verificar si el componente inicial y final son Instancias de Polygon(Diagrama entidad)
											//System.out.println("SE IDENTIFICARON OBJETOS  - DIAMANTE Y ENTIDAD");
											if(((RelacionTernaria_Impl)entidad1_aux).cont<=0)//termina de ejecutar este caso cuando 
											{
												break;
											}else
											{
												((RelacionTernaria_Impl)entidad1_aux).disminuirContador();
												
												HaloHelper helper=listaHaloHelper.get(currentUser);
												  //System.out.println("try resize");
												  if(!helper.getHoverInicio().equalsVector(new Vector3D())&&!helper.getHoverFin().equalsVector(new Vector3D()))
												  {
													  System.out.println("imprimiendo get hover inicio: " + helper.getHoverInicio());
													 ((Relacion)objeto).setInicio(((RelacionTernaria_Impl)entidad1_aux).devolverPuntoMasCercanoYDisponible(helper.getHoverInicio()));
													 ((Relacion)objeto).setFin(helper.getHoverFin());
													 listaHaloHelper.remove(currentUser);
													 helper=new HaloHelper();
													 listaHaloHelper.put(currentUser, helper);
													 System.out.println("resize done!!!!!!!!!!!");
												  }
												
												
												/**Colocacion de Texto flotante a la relacion*/
												TextoFlotante objetotextoInicio = (TextoFlotante)recognizer.aniadirTextoFlotante(((MTRoundRectangle)componente).getCenterPointGlobal());
												TextoFlotante objetotextoFin = (TextoFlotante)recognizer.aniadirTextoFlotante(((AbstractShape)destino).getCenterPointGlobal());
												objetotextoInicio.setOwner(((Relacion)objeto)); 
												objetotextoFin.setOwner(((Relacion)objeto)); 
												
												((Relacion)objeto).setTextoInicio(objetotextoInicio);
												((Relacion)objeto).setTextoFin(objetotextoFin);
												
												((Relacion)objeto).setObjetoInicio(((ObjetoUMLGraph)entidad1_aux).getObjetoUML());
												((Relacion)objeto).setObjetoFin(((ObjetoUMLGraph)entidad2_aux).getObjetoUML());
												ObjetoUMLGraph linea= new Relacion_Impl(mtApp, container, getCanvas(), objeto, componentRecognizer, server);
												((ObjetoUMLGraph)entidad1_aux).guardarDatos(ObjetoUMLGraph.RELACIONES_INICIO_KEYWORD, linea);
												((ObjetoUMLGraph)entidad2_aux).guardarDatos(ObjetoUMLGraph.RELACIONES_FIN_KEYWORD, linea);
												
												objeto.setFigura(linea);
												
												
												
											}
											
										}
										
										/**Este el codigo que permite unir relacion_multiples con las entidades, pero yendo desde una entidad
										 * hasta un relacion multiple*/
										Object entidad1_aux1=((MTComponent)componente).getUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD);
										Object entidad2_aux1=((MTComponent)destino).getUserData(ObjetoUMLGraph.RELACION_MULTIPLE_KEYWORD);
										if(entidad1_aux1!=null&&entidad2_aux1!=null){ //verificar si el componente inicial y final son Instancias de Polygon(Diagrama entidad)
											//System.out.println("SE IDENTIFICARON OBJETOS  - DIAMANTE Y ENTIDAD");
											if(((RelacionTernaria_Impl)entidad2_aux1).cont<=0)//termina de ejecutar este caso cuando 
											{
												break;
											}else
											{
												((RelacionTernaria_Impl)entidad2_aux1).disminuirContador();
												
												HaloHelper helper=listaHaloHelper.get(currentUser);
												  //System.out.println("try resize");
												  if(!helper.getHoverInicio().equalsVector(new Vector3D())&&!helper.getHoverFin().equalsVector(new Vector3D()))
												  {
													 ((Relacion)objeto).setInicio(helper.getHoverInicio());
													 ((Relacion)objeto).setFin(((RelacionTernaria_Impl)entidad1_aux).devolverPuntoMasCercanoYDisponible(helper.getHoverFin()));
													 listaHaloHelper.remove(currentUser);
													 helper=new HaloHelper();
													 listaHaloHelper.put(currentUser, helper);
													 System.out.println("resize done!!!!!!!!!!!");
												  }
												
												
												/**Colocacion de Texto flotante a la relacion*/
												TextoFlotante objetotextoInicio = (TextoFlotante)recognizer.aniadirTextoFlotante(((MTRoundRectangle)componente).getCenterPointGlobal());
												TextoFlotante objetotextoFin = (TextoFlotante)recognizer.aniadirTextoFlotante(((AbstractShape)destino).getCenterPointGlobal());
												objetotextoInicio.setOwner(((Relacion)objeto)); 
												objetotextoFin.setOwner(((Relacion)objeto)); 
												
												((Relacion)objeto).setTextoInicio(objetotextoInicio);
												((Relacion)objeto).setTextoFin(objetotextoFin);
												
												((Relacion)objeto).setObjetoInicio(((ObjetoUMLGraph)entidad1_aux1).getObjetoUML());
												((Relacion)objeto).setObjetoFin(((ObjetoUMLGraph)entidad2_aux1).getObjetoUML());
												ObjetoUMLGraph linea= new Relacion_Impl(mtApp, container, getCanvas(), objeto, componentRecognizer, server);
												((ObjetoUMLGraph)entidad1_aux1).guardarDatos(ObjetoUMLGraph.RELACIONES_INICIO_KEYWORD, linea);
												((ObjetoUMLGraph)entidad2_aux1).guardarDatos(ObjetoUMLGraph.RELACIONES_FIN_KEYWORD, linea);
												
												objeto.setFigura(linea);
												
												
												
											}
											
										}
										
										
										
										
										
										}
									break;
									
								case ObjetoUML.RELACION_MULTIPLE:
									ObjetoUMLGraph relacion_multiple = new RelacionTernaria_Impl(mtApp, getCanvas(), container, objeto, componentRecognizer);
									objeto.setFigura(relacion_multiple);
									System.out.println("DIAMANTEEEEEEE!!");
									break;
									
									
								default:
									break;
								}
							}

							// setBrushColor(new MTColor(255,0,0));

						}
					//}
					}
				}
				return false;
			}
		});

		//agregamos boton para relacion multiple
		//setBotonRelacionMultiple(container, mtApp, getCanvas());
	}

	public void setBrush(AbstractShape brush) {
		this.drawShape = brush;
		this.localBrushCenter = drawShape.getCenterPointLocal();
		this.brushWidthHalf = drawShape.getWidthXY(TransformSpace.LOCAL) / 2f;
		this.brushHeightHalf = drawShape.getHeightXY(TransformSpace.LOCAL) / 2f;
		this.stepDistance = brushWidthHalf / 2.8f;
		this.drawShape.setFillColor(this.brushColor);
		this.drawShape.setStrokeColor(this.brushColor);
	}

	/*
	 * 
	 *Descripcion: Utilizada para borrar el trazo una vez dibujado sobre el lienzo.
	 */
	public void setBrush2(AbstractShape brush) {
		this.drawShape2 = brush;
		drawShape2.setFillColor(new MTColor(255, 255, 255, 255));
		drawShape2.setStrokeColor(new MTColor(255, 255, 255, 255));
		this.localBrushCenter = drawShape2.getCenterPointLocal();
		this.brushWidthHalf = drawShape2.getWidthXY(TransformSpace.LOCAL) / 2f;
		this.brushHeightHalf = drawShape2.getHeightXY(TransformSpace.LOCAL) / 2f;
		this.stepDistance = brushWidthHalf / 2.8f;
		// this.drawShape2.setFillColor(this.brushColor);
		// this.drawShape2.setStrokeColor(this.brushColor);
	}

	public void setBrushColor(MTColor color) {
		this.brushColor = color;
		if (this.drawShape != null) {
			drawShape.setFillColor(color);
			drawShape.setStrokeColor(color);
		}
	}

	public void setBrushScale(float scale) {
		this.brushScale = scale;
	}

	public void onEnter() {
	}

	public void onLeave() {
	}
	
	public void anadirObjeto(MTComponent o){
		this.container.addChild(o);
		
	}
	
	// Cuenta cuantos pasos se dibujaron. Cuantas figuras. Para poder visualizarlo por pasos en la app web.
		public int contarPasos()
		{
			int numpasos=1;
			double pasoActual =  listaCoordenadasPlayer1.get(0)[2];
			for(int i=0;i<listaCoordenadasPlayer1.size();i++)
			{
				if(pasoActual !=  listaCoordenadasPlayer1.get(i)[2])
				{
					numpasos++;
					pasoActual=listaCoordenadasPlayer1.get(i)[2];
				}
			}
			
			return numpasos;
			
		}

		
	
	public void keyPressed() {
			System.out.println("PRESSED");
			//guardarEnArchivo(UMLDataSaver.getJsonMap());
	}
		
	public boolean guardar(){

		/*
		System.out.println("Guardado");
=======
		//System.out.println("Guardado");
>>>>>>> 1df44e59e182f84e44e72f715fcd4046372c3b37
		JFrame parentFrame = new JFrame();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");
		
		int userSelection = fileChooser.showSaveDialog(parentFrame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			//System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			String dir_archivo=fileToSave.getAbsolutePath()+".json";
		//
		parentFrame.toFront();
		parentFrame.setAlwaysOnTop(true);
		//
			
		//System.out.println("JSON 1 Guardado");
		JSONObject jsonPlayer1 = new JSONObject();
		
		/*jsonPlayer1.put("name","foo");
		jsonPlayer1.put("num",new Integer(100));
		jsonPlayer1.put("balance",new Double(1000.21));
		jsonPlayer1.put("is_vip",new Boolean(true));* /
		//jsonPlayer1.put("coordenadas",listaCoordenadasPlayer1.get(i)[0]+"/"+listaCoordenadasPlayer1.get(i)[0]);
		Map obj=new LinkedHashMap();	
		obj.put("width",mtApp.getWidth());
		obj.put("height",mtApp.getHeight());
		Map valorMap;
		List  listaTotal = new LinkedList();
		List  listaPuntos = new LinkedList();
		// no deberia ser estatico.
		java.util.Date date= new java.util.Date();
		
		int numpasos = contarPasos();
		//System.out.println("NumPasos: "+numpasos+"\n");
		
		for(int a=0; a<contarPasos();a++)
		{
			// Para una Persona
			valorMap  = new LinkedHashMap();
			valorMap.put("id",1);
			valorMap.put("idpersona",1);
			valorMap.put("forma", "rectangle");
			valorMap.put("tiempoini", (new Timestamp(date.getTime())).toString() );
			valorMap.put("tiempofin", (new Timestamp(date.getTime())).toString() );
			
			
			listaPuntos = new LinkedList();
			for(int i=0;i<listaCoordenadasPlayer1.size();i++)
			{
				if(Math.floor(listaCoordenadasPlayer1.get(i)[2]+0.5)==a)
				{
					listaPuntos.add(listaCoordenadasPlayer1.get(i)[0]+"-"+listaCoordenadasPlayer1.get(i)[1]);			
				}
			}
			
			valorMap.put("puntos", listaPuntos);
	
			obj.put("paso"+a,valorMap);
		}
		//Fin de contruccion de JSON.
		
		String jsonText = JSONValue.toJSONString(obj);
		System.out.print(jsonText);
		
		// Escribo el String en archivo .json
			BufferedWriter writer = null;
			try {
						writer = new BufferedWriter( new FileWriter(dir_archivo));
						writer.write( jsonText);
						//System.out.println("Guardado archivo .json");
			} catch (IOException e) {
						// TODO Auto-generated catch block
				//System.out.println("Error");
						e.printStackTrace();
			}finally
			{
				//System.out.println("Error2");
						try
					    {
					        if ( writer != null)
					        writer.close( );
					    }
					    catch ( IOException e)
					    {
					    }
			}
		
		
		}*/
		
		
		UMLDataSaver.guardarEnArchivo();
		return true;
	}
	

	public void setListaPencil(MTEllipse[] lista){
		listaPencil=lista;		
	}
	
	public static void setModoBorrar( int idUsuario){
		try {
			Usuario currentUser=(MainDrawingScene.getListaUsuarios().containsKey(idUsuario))?MainDrawingScene.getListaUsuarios().get(idUsuario):MainDrawingScene.getListaUsuarios().get(Usuario.ID_DEFAULT_USER);
			UMLFacade componentRecognizer=listaComponentRecognizer.get(currentUser);
			UMLFacade recognizer=(listaRecognizer.containsKey(idUsuario))?listaRecognizer.get(idUsuario):listaRecognizer.get(Usuario.ID_DEFAULT_USER);
			componentRecognizer.cambiarModo();
			recognizer.cambiarModo();
			//System.out.println("Intentando cambiar modo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
