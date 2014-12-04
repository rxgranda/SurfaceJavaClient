package advanced.umleditor.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.batik.util.MimeTypeConstants;
import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
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

import processing.core.PApplet;
import advanced.drawing.MainDrawingScene;
import advanced.umleditor.UMLCollection;
import advanced.umleditor.UMLDataSaver;
import advanced.umleditor.UMLFacade;
import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.Usuario;
import advanced.umleditor.socketio.EntidadAdapter;
import advanced.umleditor.socketio.RelacionAdapter;

public class RelacionTernaria_Impl extends MTComponent implements ObjetoUMLGraph {
	
	Map <Integer, MTComponent>listaEntidades= new HashMap<Integer, MTComponent>();
	final MTRoundRectangle rombo;
	final MTRoundRectangle halo_rombo;
	final MTRoundRectangle zona_rombo;
	static float ancho = 100;
	static float alto = 100;
	final public Vector3D centro;
	ObjetoUML objeto;
	public boolean relacion_activa = true;
	public int cont = 3;
	private boolean punto1=false, punto2=false, punto3=false;
	public Vector3D v_punto1;
	public Vector3D v_punto2;
	public Vector3D v_punto3;
	public Vector3D v_punto4;
	static float constante_rombo;
	
	
	

	public RelacionTernaria_Impl(MTApplication pApplet, final MTCanvas canvas, MTComponent container, ObjetoUML objeto, final UMLFacade recognizer ) {
		super(pApplet);
		//iniciamos el rectangulo en el punto x e y..
		System.out.println();
		centro = new Vector3D(objeto.getPosicion().x, objeto.getPosicion().y, 0);
		rombo = new MTRoundRectangle(objeto.getPosicion().x, objeto.getPosicion().y, 0, objeto.getWidth(),	objeto.getHeight(), 1, 1, pApplet);									
		halo_rombo = new MTRoundRectangle(objeto.getPosicion().x, objeto.getPosicion().y, 1, objeto.getWidth(),	objeto.getHeight(), 1, 1, pApplet);
		
		
		zona_rombo = new MTRoundRectangle(objeto.getPosicion().x+(objeto.getWidth()/2)/2, objeto.getPosicion().y+(objeto.getHeight()/2)/2, 1, objeto.getWidth()/2,	objeto.getHeight()/2, 1, 1, pApplet);
		zona_rombo.setPickable(true);
		zona_rombo.setFillColor(ObjetoUMLGraph.headerColor);
		zona_rombo.setNoStroke(false);
		zona_rombo.setStrokeColor(ObjetoUMLGraph.headerColor);
		zona_rombo.setEnabled(true);
		zona_rombo.removeAllGestureEventListeners();
		
		this.objeto = objeto;
		System.out.println("DIBUJA RELACION MULTIPLE");
		//.. y lo rotamos 90 grados para que se vea como rombo en la canvas..
		
		//.. luego seteamos parámetros generales
		rombo.setPickable(false);
		rombo.setFillColor(new MTColor(255,255,255));
		rombo.setNoStroke(false);
		rombo.setEnabled(true);
		rombo.setStrokeColor(ObjetoUMLGraph.bodyColor);
		rombo.removeAllGestureEventListeners();
		//rombo.rotateZGlobal(new Vector3D(x, y), 90);
		
		halo_rombo.setNoFill(true); // Hacerlo invisible
		//halo_rombo.setFillColor(ObjetoUMLGraph.headerColor);
		halo_rombo.setPickable(true);
		//halo.setStrokeColor(new MTColor(0, 0, 0));
		halo_rombo.setNoStroke(true);
		halo_rombo.removeAllGestureEventListeners();
		
		constante_rombo = (float) (Math.sqrt(2*(objeto.getWidth()*objeto.getWidth())))/2;
		v_punto1 = rombo.getCenterPointGlobal();
		v_punto1.setY(rombo.getCenterPointGlobal().y+(constante_rombo+15));
		v_punto2 = rombo.getCenterPointGlobal();
		v_punto2.setX(rombo.getCenterPointGlobal().x+(constante_rombo+15));
		v_punto3 = rombo.getCenterPointGlobal();
		v_punto3.setX(rombo.getCenterPointGlobal().x-(constante_rombo));
		v_punto4 = rombo.getCenterPointGlobal();
		v_punto4.setY(rombo.getCenterPointGlobal().y-(constante_rombo));
		
		zona_rombo.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				System.out.println("Gesture"+de.getTargetComponent());
				
				rombo.setPositionGlobal(rombo.getCenterPointGlobal().addLocal(de.getTranslationVect()));
				halo_rombo.setPositionGlobal(rombo.getCenterPointGlobal());
				v_punto1.addLocal(de.getTranslationVect());
				v_punto2.addLocal(de.getTranslationVect());
				v_punto3.addLocal(de.getTranslationVect());
				v_punto4.addLocal(de.getTranslationVect());

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
				return false;
			}
		});
		
		
		
			
		
		rombo.addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
					AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
					InputCursor cursor = cursorInputEvt.getCursor();
					IMTComponent3D target = cursorInputEvt.getTargetComponent();
					System.out.println("RECONOCE TRAZO EN ROMBO!!");
					switch (cursorInputEvt.getId()) {
					case AbstractCursorInputEvt.INPUT_STARTED:
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						//body.removeFromParent();	
						//canvas.addChild(body);
						break;
					case AbstractCursorInputEvt.INPUT_UPDATED:
						recognizer.anadirPunto(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:

						System.out.println("Reconocer:");
						//canvas.removeChild(body);
						//rectangulo.addChild(body);
						ObjetoUML obj=recognizer.reconocerObjeto();
						System.out.println("BORRARAAA "+obj.getWidth()+"H"+obj.getHeight()+"C"+obj.getClass());
						if (obj ==ObjetoUML.DELETE_OBJECT_GESTURE&&obj.getWidth()>10){
							
							int idUsuario=(MainDrawingScene.getListaUsuarios().get((int)cursor.sessionID)!=null)?(int)cursor.sessionID:Usuario.ID_DEFAULT_USER;
							
							
							halo_rombo.removeFromParent();
							rombo.removeFromParent();
									

							removerRelaciones(idUsuario);
							cont = cont -1;

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
		
		
		
		//canvas.addChild(halo);
		
		//objeto.setPosicion(rombo.getCenterPointGlobal());
		canvas.addChild(halo_rombo);
		
		rombo.addChild(zona_rombo);
		rombo.rotateZGlobal(rombo.getCenterPointGlobal(), 45);
		halo_rombo.rotateZGlobal(rombo.getCenterPointGlobal(), 45);
		//zona_rombo.rotateZGlobal(zona_rombo.getCenterPointGlobal(), 45);
		
		
		
		container.addChild(rombo);
		rombo.setUserData(ObjetoUMLGraph.RELACION_MULTIPLE_KEYWORD, this);
		halo_rombo.setUserData(ObjetoUMLGraph.RELACION_MULTIPLE_KEYWORD, this);
	}
	
	public Vector3D obtenerPuntoUnion(){
		//si los tres puntos están disponibles, calcular la distancia entre los tres
		if(punto1&&punto2&&punto3){
			
		}
		return centro;
	}
	
	
	@Override
	public synchronized void  guardarDatos(String keyword, Object datos) {

		LinkedList listaDatos=(LinkedList<Object>) halo_rombo.getUserData(keyword);
		if(listaDatos==null){
			listaDatos= new LinkedList<Object>();
			halo_rombo.setUserData(keyword, listaDatos);
		}
		listaDatos.add(datos);

	}



	@Override
	public LinkedList obtenerDatos(String keyword) {

		LinkedList listaDatos=(LinkedList) halo_rombo.getUserData(keyword);
		return listaDatos;
	}

	
	@Override
	public synchronized void eliminarDatos(String keyword, Object datos) {
		System.out.println("ENTIDAD........ENTIDA... ENTIDAD....."+ objeto.getId());
		LinkedList listaDatos=(LinkedList<Object>) halo_rombo.getUserData(keyword);
		if(listaDatos!=null){
			if(listaDatos.contains(datos)){
				listaDatos.remove(datos);
			}
		}
		
		
	}
	
	public synchronized void removerRelaciones(int idUsuario){
		LinkedList listaInicio=obtenerDatos(RELACIONES_INICIO_KEYWORD);
		if(listaInicio!=null){
			for(Object o:listaInicio){
				if(o instanceof ObjetoUMLGraph){
					//((Relacion)objeto)
				
					((Relacion_Impl)o).removerRelacion(idUsuario,true);
					System.out.println("ELIMINAR INICIO.........................................");

				}

			}
			listaInicio.clear();
		}
		
		LinkedList listaFin=obtenerDatos(RELACIONES_FIN_KEYWORD);
		if(listaFin!=null){
			for(Object o:listaFin){
				if(o instanceof ObjetoUMLGraph){
					//((Relacion)objeto)
					
					((Relacion_Impl)o).removerRelacion(idUsuario,true);
					System.out.println("ELIMINAR FIN.........................................");

					}
			}
			listaFin.clear();
		}

	}
	

	public void disminuirContador(){
		this.cont = this.cont - 1;
	}
	public void aumentarContador(){
		this.cont = this.cont + 1;
	}
	
	@Override
	public MTComponent getFigura() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MTComponent getHalo() {
		// TODO Auto-generated method stub
		return null;
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
	public ObjetoUML getObjetoUML() {
		// TODO Auto-generated method stub
		return this.objeto;
	}

	@Override
	public void setObjetoUML(ObjetoUML objeto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actualizarEtiquetas() {
		// TODO Auto-generated method stub

	}
	
	
	
	

}
