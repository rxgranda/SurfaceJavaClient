package advanced.umleditor.impl;

import java.util.HashMap;
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
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;

public class RelacionMultiple_Impl extends MTComponent implements ObjetoUMLGraph {
	
	Map <Integer, MTComponent>listaEntidades= new HashMap<Integer, MTComponent>();
	final MTRoundRectangle rombo;
	final MTRoundRectangle halo_rombo;
	final MTRoundRectangle zona_rombo;
	static float ancho = 100;
	static float alto = 100;
	final public Vector3D centro;
	ObjetoUML objeto;
	private boolean punto1=false, punto2=false, punto3=false;
	Vector3D v_punto1,v_punto2,v_punto3;
	
	
	

	public RelacionMultiple_Impl(MTApplication pApplet, final MTCanvas canvas, MTComponent container, ObjetoUML objeto ) {
		super(pApplet);
		//iniciamos el rectangulo en el punto x e y..
		System.out.println();
		centro = new Vector3D(objeto.getPosicion().x, objeto.getPosicion().y, 0);
		rombo = new MTRoundRectangle(objeto.getPosicion().x, objeto.getPosicion().y, 0, objeto.getWidth(),	objeto.getHeight(), 1, 1, pApplet);									
		halo_rombo = new MTRoundRectangle(objeto.getPosicion().x, objeto.getPosicion().y, 1, objeto.getWidth(),	objeto.getHeight(), 1, 1, pApplet);
		
		
		zona_rombo = new MTRoundRectangle(objeto.getPosicion().x, objeto.getPosicion().y, 1, objeto.getWidth()/2,	objeto.getHeight()/2, 1, 1, pApplet);
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
		//rombo.rotateZGlobal(new Vector3D(x, y), 90);
		
		halo_rombo.setNoFill(true); // Hacerlo invisible
		//halo_rombo.setFillColor(ObjetoUMLGraph.headerColor);
		halo_rombo.removeAllGestureEventListeners();
		
		halo_rombo.setPickable(true);
		//halo.setStrokeColor(new MTColor(0, 0, 0));
		halo_rombo.setNoStroke(true);
		
		
		halo_rombo.removeAllGestureEventListeners();
		halo_rombo.addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) { //Most input events in MT4j are an instance of AbstractCursorInputEvt (mouse, multi-touch..)
					AbstractCursorInputEvt cursorInputEvt = (AbstractCursorInputEvt) inEvt;
					System.out.println("Halo Entidad");
					//halo.sendToFront();
					switch (cursorInputEvt.getId()) {
					case AbstractCursorInputEvt.INPUT_STARTED:
						System.out.println("INICIO DE EVENTO!!!");
						break;
					case AbstractCursorInputEvt.INPUT_UPDATED:
						System.out.println("updated");
						break;
					case AbstractCursorInputEvt.INPUT_ENDED:
						System.out.println("FIN DE EVENTO!!!");

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
		
		zona_rombo.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				System.out.println("Gesture"+de.getTargetComponent());
				//objeto.setPosicion(objeto.getPosicion().getAdded(de.getTranslationVect()));
				//rectangulo.setPositionGlobal(objeto.getPosicion());
		//		halo.setPositionGlobal(new Vector3D(objeto.getPosicion().x,objeto.getPosicion().y));
				
				rombo.setPositionGlobal(rombo.getCenterPointGlobal().addLocal(de.getTranslationVect()));
				halo_rombo.setPositionGlobal(rombo.getCenterPointGlobal());

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
