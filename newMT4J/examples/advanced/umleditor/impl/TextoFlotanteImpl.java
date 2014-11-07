package advanced.umleditor.impl;

import java.util.LinkedList;

import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.MTTextField;
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
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import com.corundumstudio.socketio.SocketIOServer;

import advanced.drawing.DrawSurfaceScene;
import advanced.drawing.MainDrawingScene;
import advanced.umleditor.UMLFacade;
import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.TextoFlotante;
import advanced.umleditor.logic.Usuario;
import advanced.umleditor.socketio.EntidadAdapter;
import advanced.umleditor.socketio.TextoFlotanteAdapter;
import processing.core.PApplet;

public class TextoFlotanteImpl extends MTComponent implements ObjetoUMLGraph {

	final MTComponent container;
	final MTRoundRectangle rectangulo;
	final MTRoundRectangle halo;
	private MTTextField headerField;
	private MTTextArea  bodyField;
	MTEllipse botonResize=null,botonResize2=null,botonResize3=null,botonResize4=null;
	

	SocketIOServer server;
	ObjetoUML objeto;
	class DoubleClickProcessor extends TapProcessor  implements IGestureEventListener {

		public DoubleClickProcessor(PApplet pa, float maxFingerUpDistance,
				boolean enableDoubleTap, int doubleTapTime,
				boolean stopEventPropagation) {
			super(pa, maxFingerUpDistance, enableDoubleTap, doubleTapTime,
					stopEventPropagation);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean processGestureEvent(MTGestureEvent ge) {			
				TapEvent te = (TapEvent)ge;
				IMTComponent3D target = te.getTargetComponent();
				if (target instanceof MTRoundRectangle) {
					MTRoundRectangle rectangle = (MTRoundRectangle) target;
					switch (te.getTapID()) {
					case TapEvent.BUTTON_DOUBLE_CLICKED:
						System.out.println("Doble Click " + target);						
						//final AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt) ge.getSource();
						final InputCursor m = te.getCursor();
						String canal=(MainDrawingScene.getListaUsuarios().get((int)m.sessionID)!=null)?MainDrawingScene.getListaUsuarios().get((int)m.sessionID).getCanal():Usuario.CANAL_DEFAULT_USER;
						int idUsuario=(MainDrawingScene.getListaUsuarios().get((int)m.sessionID)!=null)?(int)m.sessionID:Usuario.ID_DEFAULT_USER;

						server.getRoomOperations(canal).sendEvent("startEdition",new TextoFlotanteAdapter(((TextoFlotante)objeto),idUsuario));						
						System.out.println("Enviado "+canal+""+server.getRoomOperations(canal).getClients().size());
						break;
					
					default:
						break;
					}
				}
				return false;			
		}
	
	}
	
	
	public TextoFlotanteImpl(final MTApplication mtApp,final MTComponent container, final MTCanvas canvas, final UMLFacade recognizer,final ObjetoUML objeto, final SocketIOServer server) {
		
		super(mtApp);

		rectangulo = new MTRoundRectangle(objeto
				.getPosicion().x, objeto
				.getPosicion().y, 0, objeto
				.getWidth(),
				objeto.getHeight(), 1, 1, mtApp);									
		rectangulo.setFillColor(new MTColor(255,255,255));
		rectangulo.setStrokeColor(new MTColor(0, 0, 0));
		rectangulo.setNoStroke(true);
		this.objeto=objeto;
		//corregir posicion inicial
				//objeto.setPosicion(rectangulo.getCenterPointGlobal());
		this.server=server;
		this.container = container;
		halo=new MTRoundRectangle(objeto
				.getPosicion().x-ObjetoUMLGraph.haloWidth/2, objeto
				.getPosicion().y-ObjetoUMLGraph.haloWidth/2, 1, objeto
				.getWidth()+ObjetoUMLGraph.haloWidth,
				objeto.getHeight()+ObjetoUMLGraph.haloWidth, 1, 1, mtApp);									
		//halo.setNoFill(true);
		halo.setFillColor(ObjetoUMLGraph.haloDeSelected);
		halo.removeAllGestureEventListeners();
		halo.setUserData(ObjetoUMLGraph.ENTIDADES_KEYWORD, this);
		//halo.setPickable(false);
		//halo.setStrokeColor(new MTColor(0, 0, 0));
		halo.setNoStroke(true);
		/*
		halo.addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
					AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
					InputCursor cursor = cursorInputEvt.getCursor();
					IMTComponent3D target = cursorInputEvt.getTargetComponent();
					System.out.println("Halo Entidad");
					//halo.sendToFront();
					switch (cursorInputEvt.getId()) {
					case AbstractCursorInputEvt.INPUT_STARTED:
						//System.out.println("Input detected on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
						rectangulo.setFillColor(selectedObject);
						break;
					case AbstractCursorInputEvt.INPUT_UPDATED:
						//	System.out.println("Holaaa Input updated on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());			
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:
						rectangulo.setFillColor(nonselectedObject);

						final IMTComponent3D destino=canvas.getComponentAt((int)cursor.getCurrentEvtPosX(), (int)cursor.getCurrentEvtPosY());
						//System.out.println("Inicio Input updated on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());			

						//System.out.println("Final"+destino);
						//MTRoundRectangle destino2=(MTRoundRectangle)destino;
						//halo.setNoFill(false);
						//halo.setFillColor(new MTColor(255,0,0));
						//destino2.setNoFill(false);
						//destino2.setFillColor(new MTColor(255,0,0));
						break;
					default:
						break;
					}

				}else{
					//handle other input events
				}
				return false;
			}
		});*/

		canvas.addChild(halo);

		final MTRoundRectangle header = new MTRoundRectangle(objeto
				.getPosicion().x, objeto
				.getPosicion().y, 0, objeto
				.getWidth(),
				(int)(objeto.getHeight()), 1, 1, mtApp);									
		header.setFillColor(new MTColor(255,255,255));
		header.setStrokeColor(new MTColor(30,30,30));
		header.setNoStroke(false);
		//header.setPickable(false);
		header.removeAllGestureEventListeners();
		IFont headerFont=FontManager.getInstance().createFont(mtApp, "SourceSansPro-BoldIt.otf", 12, new MTColor(30,30,30),true);

		headerField = new MTTextField(objeto.getPosicion().x, objeto.getPosicion().y ,(int)(objeto.getWidth()),(int)(objeto.getHeight()),headerFont, mtApp);
		headerField.setText(((TextoFlotante)objeto).getNombre());
		//headerField.setFontColor(new MTColor(255,255,255));
		headerField.setPickable(false);
		headerField.setNoFill(true);
		headerField.setNoStroke(true);									
		header.addChild(headerField);

		//Agregar boton resize
		botonResize=new MTEllipse(mtApp, new Vector3D(objeto
				.getPosicion().x, objeto
				.getPosicion().y), 10, 10);
		botonResize.setFillColor(ObjetoUMLGraph.resizeButtonColor);
		botonResize.setNoStroke(true);
		botonResize.removeAllGestureEventListeners();
		botonResize.unregisterAllInputProcessors();
		botonResize.registerInputProcessor(new DragProcessor(mtApp));
		botonResize.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				objeto.setWidth(objeto.getWidth()-de.getTranslationVect().x);
				objeto.setHeight(objeto.getHeight()-de.getTranslationVect().y);
				rectangulo.setSizeXYGlobal(objeto.getWidth(),objeto.getHeight());
				halo.setSizeXYGlobal(objeto.getWidth()+ObjetoUMLGraph.haloWidth,objeto.getHeight()+ObjetoUMLGraph.haloWidth);
				botonResize.setSizeXYGlobal(18, 18);
				botonResize2.setSizeXYGlobal(18, 18);
				botonResize3.setSizeXYGlobal(18, 18);
				botonResize4.setSizeXYGlobal(18, 18);
				return false;
			}
		});
		
		//Agregar boton resize2
		botonResize2=new MTEllipse(mtApp, new Vector3D(objeto
				.getPosicion().x+objeto.getWidth(), objeto
				.getPosicion().y), 10, 10);
		botonResize2.setFillColor(ObjetoUMLGraph.resizeButtonColor);
		botonResize2.setNoStroke(true);
		botonResize2.removeAllGestureEventListeners();
		botonResize2.unregisterAllInputProcessors();
		botonResize2.registerInputProcessor(new DragProcessor(mtApp));
		botonResize2.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				objeto.setWidth(objeto.getWidth()+de.getTranslationVect().x);
				objeto.setHeight(objeto.getHeight()-de.getTranslationVect().y);
				rectangulo.setSizeXYGlobal(objeto.getWidth(),objeto.getHeight());	
				halo.setSizeXYGlobal(objeto.getWidth()+ObjetoUMLGraph.haloWidth,objeto.getHeight()+ObjetoUMLGraph.haloWidth);
				botonResize.setSizeXYGlobal(18, 18);
				botonResize2.setSizeXYGlobal(18, 18);
				botonResize3.setSizeXYGlobal(18, 18);
				botonResize4.setSizeXYGlobal(18, 18);						
				return false;
			}
		});
		//Agregar boton resize3
		botonResize3=new MTEllipse(mtApp, new Vector3D(objeto
				.getPosicion().x, objeto
				.getPosicion().y+objeto.getHeight()), 10, 10);
		botonResize3.setFillColor(ObjetoUMLGraph.resizeButtonColor);
		botonResize3.setNoStroke(true);
		botonResize3.removeAllGestureEventListeners();
		botonResize3.unregisterAllInputProcessors();
		botonResize3.registerInputProcessor(new DragProcessor(mtApp));
		botonResize3.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				objeto.setWidth(objeto.getWidth()-de.getTranslationVect().x);
				objeto.setHeight(objeto.getHeight()+de.getTranslationVect().y);
				rectangulo.setSizeXYGlobal(objeto.getWidth(),objeto.getHeight());	
				halo.setSizeXYGlobal(objeto.getWidth()+ObjetoUMLGraph.haloWidth,objeto.getHeight()+ObjetoUMLGraph.haloWidth);
				botonResize.setSizeXYGlobal(18, 18);
				botonResize2.setSizeXYGlobal(18, 18);
				botonResize3.setSizeXYGlobal(18, 18);
				botonResize4.setSizeXYGlobal(18, 18);								
				return false;
			}
		});
		//Agregar boton resize
		botonResize4=new MTEllipse(mtApp, new Vector3D(objeto
				.getPosicion().x+objeto.getWidth(), objeto
				.getPosicion().y+objeto.getHeight()), 10, 10);
		botonResize4.setFillColor(ObjetoUMLGraph.resizeButtonColor);
		botonResize4.setNoStroke(true);
		botonResize4.removeAllGestureEventListeners();
		botonResize4.unregisterAllInputProcessors();
		botonResize4.registerInputProcessor(new DragProcessor(mtApp));
		botonResize4.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				objeto.setWidth(objeto.getWidth()+de.getTranslationVect().x);
				objeto.setHeight(objeto.getHeight()+de.getTranslationVect().y);
				rectangulo.setSizeXYGlobal(objeto.getWidth(),objeto.getHeight());	
				halo.setSizeXYGlobal(objeto.getWidth()+ObjetoUMLGraph.haloWidth,objeto.getHeight()+ObjetoUMLGraph.haloWidth);
				botonResize.setSizeXYGlobal(18, 18);
				botonResize2.setSizeXYGlobal(18, 18);
				botonResize3.setSizeXYGlobal(18, 18);
				botonResize4.setSizeXYGlobal(18, 18);								
				return false;
			}
		});

		///////////
		/*final MTEllipse botonBorrar=new MTEllipse(mtApp, new Vector3D(objeto
				.getPosicion().x, objeto
				.getPosicion().y+objeto.getHeigth()), 5, 5);
		botonBorrar.setFillColor(ObjetoUMLGraph.rojo);
		botonBorrar.removeAllGestureEventListeners();
		botonBorrar.unregisterAllInputProcessors();
		botonBorrar.registerInputProcessor(new DragProcessor(mtApp));
		botonBorrar.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;

				container.removeChild((MTComponent)rectangulo);
				System.out.println("Remover: "+rectangulo);
				return false;
			}
		});




		rectangulo.addChild(botonBorrar);*/

		rectangulo.addChild(header);
		rectangulo.addChild(botonResize);	
		rectangulo.addChild(botonResize2);	
		rectangulo.addChild(botonResize3);	
		rectangulo.addChild(botonResize4);	
		//botonResize.sendToFront();

		rectangulo.removeAllGestureEventListeners();
		rectangulo.unregisterAllInputProcessors();
		/*
		header.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				System.out.println("Gesture"+de.getTargetComponent());
				//objeto.setPosicion(objeto.getPosicion().getAdded(de.getTranslationVect()));
				//rectangulo.setPositionGlobal(objeto.getPosicion());
		//		halo.setPositionGlobal(new Vector3D(objeto.getPosicion().x,objeto.getPosicion().y));
				
				rectangulo.setPositionGlobal(rectangulo.getCenterPointGlobal().addLocal(de.getTranslationVect()));
				objeto.setPosicion(rectangulo.getCenterPointGlobal());
				halo.setPositionGlobal(rectangulo.getCenterPointGlobal());
				
//// TEST
				//MainDrawingScene.clear();
/// TEST
				LinkedList listaInicio=obtenerDatos(RELACIONES_INICIO_KEYWORD);
				if(listaInicio!=null){
					for(Object o:listaInicio){
						if(o instanceof ObjetoUMLGraph){
							//((Relacion)objeto)
							Relacion objeto_relacion=(Relacion) ((Relacion_Impl)o).getObjetoUML();
							//objeto_relacion.setPosicion(objeto_relacion.getPosicion().getAdded(de.getFrom().getSubtracted(de.getTo())));
							objeto_relacion.setInicio(objeto_relacion.getInicio().getAdded(de.getTranslationVect()));
							((Relacion_Impl)o).actualizarRelacion();
						}

					}
				}

				LinkedList listaFin=obtenerDatos(RELACIONES_FIN_KEYWORD);
				if(listaFin!=null){
					for(Object o:listaFin){
						if(o instanceof ObjetoUMLGraph){
							//((Relacion)objeto)
							Relacion objeto_relacion=(Relacion) ((Relacion_Impl)o).getObjetoUML();
							//objeto_relacion.setPosicion(objeto_relacion.getPosicion().getAdded(de.getFrom().getSubtracted(de.getTo())));
							objeto_relacion.setFin(objeto_relacion.getFin().getAdded(de.getTranslationVect()));
							((Relacion_Impl)o).actualizarRelacion();
						}

					}
				}


				//switch (de.getId()) {
					//case AbstractCursorInputEvt.INPUT_STARTED:
					//	canvas.removeChild(halo);
					//	break;
					//case AbstractCursorInputEvt.INPUT_UPDATED:
					//	break;
					//case AbstractCursorInputEvt.INPUT_ENDED:
					//	canvas.addChild(canvas);
					//	break;
				//	default:
				//		break;
				//	}
				return false;
			}
		});
		
		
		*/
		
		  DoubleClickProcessor proc=new DoubleClickProcessor(mtApp,(float) 0.1,true, 300,true);
		 
		 header.registerInputProcessor(proc);
		 header.addGestureListener(DoubleClickProcessor.class,  proc);

		 header.registerInputProcessor(new TapAndHoldProcessor(mtApp, 2000));
		 header.addGestureListener(TapAndHoldProcessor.class, new TapAndHoldVisualizer(mtApp, rectangulo));
		 header.addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
				public boolean processGestureEvent(MTGestureEvent ge) {
					TapAndHoldEvent th = (TapAndHoldEvent)ge;
					IMTComponent3D target = th.getTargetComponent();
					if (target instanceof MTRoundRectangle) {
						MTRoundRectangle rectangle = (MTRoundRectangle) target;
						
						
						
						switch (th.getId()) {
						case TapAndHoldEvent.GESTURE_STARTED:
							break;
						case TapAndHoldEvent.GESTURE_UPDATED:
							break;
						case TapAndHoldEvent.GESTURE_ENDED:
							if (th.isHoldComplete()){
								
								System.out.println("Tap complete!! " + target);						
								//final AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt) ge.getSource();
								final InputCursor m = th.getCursor();
								String canal=(MainDrawingScene.getListaUsuarios().get((int)m.sessionID)!=null)?MainDrawingScene.getListaUsuarios().get((int)m.sessionID).getCanal():"canal1";
								int idUsuario=(MainDrawingScene.getListaUsuarios().get((int)m.sessionID)!=null)?(int)m.sessionID:-1;

								server.getRoomOperations(canal).sendEvent("startEdition",new TextoFlotanteAdapter(((TextoFlotante)objeto),idUsuario));						
								System.out.println("Enviado "+canal+""+server.getRoomOperations(canal).getClients().size());
								break;

							}
							break;
						default:
							break;
						}

					}
					
					
					
					

					return false;
				}
			});


		/*body.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				System.out.println("Gesture"+de.getTargetComponent());
				IMTComponent3D target = de.getTargetComponent();
				switch (de.getId()) {
				case AbstractCursorInputEvt.INPUT_STARTED:
					objeto.anadirPunto(de.getFrom().x, de.getFrom().x);
					//objeto.setPosicion(objeto.getPosicion().addLocal(de.getTranslationVect()).addLocal(new Vector3D(objeto.getWidth()/2,objeto.getHeigth()/2)));

					//rectangulo.setFillColor(selectedObject);
					break;
				case AbstractCursorInputEvt.INPUT_UPDATED:
						//	System.out.println("Input updated on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());			
					break;
				case AbstractCursorInputEvt.INPUT_ENDED:
						//System.out.println("Input ended on: " + target + " at " + cursor.getCurrentEvtPosX() + "," + cursor.getCurrentEvtPosY());
					break;
				default:
					break;
				}

				return false;
			}
		});*/


		
		container.addChild(rectangulo);
		LinkedList<TextoFlotanteImpl> textosflotantes = (LinkedList<TextoFlotanteImpl>)container.getUserData(ObjetoUMLGraph.TEXTO_FLOTANTE_KEYWORD);
		textosflotantes.add(this);
		halo.setPositionGlobal(rectangulo.getCenterPointGlobal());
		
	}



	@Override
	public MTComponent getFigura() {
		// TODO Auto-generated method stub
		return  rectangulo;
	}



	@Override
	public void setTitulo(String texto) {
		headerField.setText(texto);

	}



	@Override
	public String getTitulo(String texto) {
		// TODO Auto-generated method stub
		return headerField.getText();
	}



	@Override
	public void setAtributo(String texto) {
		bodyField.setText(texto);

	}


	@Override
	public String getAtributo(String texto) {
		// TODO Auto-generated method stub
		return bodyField.getText();
	}

	@Override
	public MTComponent getHalo() {
		// TODO Auto-generated method stub
		//halo.sendToFront();
		return halo;
	}



	@Override
	public void guardarDatos(String keyword, Object datos) {

		LinkedList listaDatos=(LinkedList<Object>) halo.getUserData(keyword);
		if(listaDatos==null){
			listaDatos= new LinkedList<Object>();
			halo.setUserData(keyword, listaDatos);
		}
		listaDatos.add(datos);

	}



	@Override
	public LinkedList obtenerDatos(String keyword) {

		LinkedList listaDatos=(LinkedList) halo.getUserData(keyword);
		return listaDatos;
	}



	@Override
	public ObjetoUML getObjetoUML() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setObjetoUML(ObjetoUML objeto) {
		// TODO Auto-generated method stub

	}



	@Override
	public boolean processGestureEvent(MTGestureEvent ge) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void actualizarEtiquetas() {
		
		headerField.setText(((TextoFlotante)objeto).getNombre());
		
		
	}



	@Override
	public void eliminarDatos(String keyword, Object datos) {
		// TODO Auto-generated method stub
		
	}
	
	public void removeGrafico(){
		
		rectangulo.removeFromParent();
		halo.removeFromParent();
	}


}
