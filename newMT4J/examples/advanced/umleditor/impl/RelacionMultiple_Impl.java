package advanced.umleditor.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.batik.util.MimeTypeConstants;
import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import advanced.umleditor.logic.ObjetoUML;

public class RelacionMultiple_Impl extends MTComponent implements ObjetoUMLGraph {
	
	Map <Integer, MTComponent>listaEntidades= new HashMap<Integer, MTComponent>();
	final MTRoundRectangle rombo;
	final MTRoundRectangle halo_rombo;
	static float ancho = 100;
	static float alto = 100;
	final public Vector3D centro;
	ObjetoUML objeto;
	
	

	public RelacionMultiple_Impl(MTApplication pApplet, final MTCanvas canvas, MTComponent container, ObjetoUML objeto ) {
		super(pApplet);
		//iniciamos el rectangulo en el punto x e y..
		System.out.println();
		centro = new Vector3D(objeto.getPosicion().x, objeto.getPosicion().y, 0);
		rombo = new MTRoundRectangle(objeto.getPosicion().x, objeto.getPosicion().y, 0, objeto.getWidth(),	objeto.getHeight(), 1, 1, pApplet);									
		halo_rombo = new MTRoundRectangle(objeto.getPosicion().x, objeto.getPosicion().y, 1, objeto.getWidth(),	objeto.getHeight(), 1, 1, pApplet);
		this.objeto = objeto;
		System.out.println("DIBUJA RELACION MULTIPLE");
		//.. y lo rotamos 90 grados para que se vea como rombo en la canvas..
		
		//.. luego seteamos parámetros generales
		rombo.setPickable(false);
		rombo.setFillColor(new MTColor(0,255,0));
		rombo.setNoStroke(false);
		rombo.setEnabled(true);
		//rombo.rotateZGlobal(new Vector3D(x, y), 90);
		
		halo_rombo.setNoFill(true); // Hacerlo invisible
		halo_rombo.setFillColor(new MTColor(0,0,0));
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
		
		//canvas.addChild(halo);
		
		//objeto.setPosicion(rombo.getCenterPointGlobal());
		canvas.addChild(halo_rombo);
		
		
		rombo.rotateZGlobal(rombo.getCenterPointGlobal(), 45);
		halo_rombo.rotateZGlobal(rombo.getCenterPointGlobal(), 45);
		
		container.addChild(rombo);
		rombo.setUserData(ObjetoUMLGraph.RELACION_MULTIPLE_KEYWORD, this);
		halo_rombo.setUserData(ObjetoUMLGraph.RELACION_MULTIPLE_KEYWORD, this);
	}

	@Override
	public void guardarDatos(String keyword, Object datos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void eliminarDatos(String keyword, Object datos) {
		// TODO Auto-generated method stub

	}

	@Override
	public LinkedList obtenerDatos(String keyword) {
		// TODO Auto-generated method stub
		return null;
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
