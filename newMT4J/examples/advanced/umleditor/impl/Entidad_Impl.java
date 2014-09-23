package advanced.umleditor.impl;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.MTTextField;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import advanced.umleditor.logic.ObjetoUML;
import processing.core.PApplet;

public class Entidad_Impl extends MTComponent implements ObjetoUMLGraph {
	
	
	final MTRoundRectangle rectangulo;
	private MTTextField headerField;
	private MTTextArea  bodyField;

	public Entidad_Impl(MTApplication mtApp, final ObjetoUML objeto) {
		
		super(mtApp);
		rectangulo = new MTRoundRectangle(objeto
				.getPosicion().x, objeto
				.getPosicion().y, 0, objeto
				.getWidth(),
				objeto.getHeigth(), 1, 1, mtApp);									
		rectangulo.setFillColor(new MTColor(255,255,255));
		rectangulo.setStrokeColor(new MTColor(0, 0, 0));
		rectangulo.setNoStroke(false);
		
		final MTRoundRectangle header = new MTRoundRectangle(objeto
				.getPosicion().x, objeto
				.getPosicion().y, 0, objeto
				.getWidth(),
				(int)(objeto.getHeigth()*0.25), 1, 1, mtApp);									
		header.setFillColor(ObjetoUMLGraph.headerColor);
		header.setStrokeColor(new MTColor(0, 0, 0));
		header.setNoStroke(false);
		
		header.setPickable(false);
		header.removeAllGestureEventListeners();
		
		headerField = new MTTextField(objeto.getPosicion().x, objeto.getPosicion().y,objeto.getWidth(),(int)(objeto.getHeigth()*0.25),FontManager.getInstance().createFont(mtApp, "SansSerif", 18), mtApp);
		headerField.setText("No-Name");
		headerField.setFontColor(new MTColor(255,255,255));
		headerField.setPickable(false);
		headerField.setNoFill(true);
		headerField.setNoStroke(true);									
		header.addChild(headerField);
		
		
		final MTRoundRectangle body = new MTRoundRectangle(objeto
				.getPosicion().x, objeto
				.getPosicion().y+(int)(objeto.getHeigth()*0.25), 0, objeto
				.getWidth(),(int)(
				objeto.getHeigth()*0.75), 1, 1, mtApp);									
		body.setFillColor(new MTColor(255, 255, 255));
		body.setStrokeColor(new MTColor(0, 0, 0));
		body.setNoStroke(false);
		
		body.setPickable(false);									
		body.removeAllGestureEventListeners();
		
		bodyField = new MTTextArea (objeto.getPosicion().x, objeto.getPosicion().y+(int)(objeto.getHeigth()*0.25),objeto.getWidth(),(int)(objeto.getHeigth()*0.75),FontManager.getInstance().createFont(mtApp, "SansSerif", 18), mtApp);
		bodyField.setText("* Atributo 1 \n *Atributo 2");
		bodyField.setFontColor(new MTColor(0,0,0));
		bodyField.setPickable(false);
		bodyField.setNoFill(true);
		bodyField.setNoStroke(true);		
		body.addChild(bodyField);
		
		final MTEllipse e=new MTEllipse(mtApp, new Vector3D(objeto
				.getPosicion().x+objeto.getWidth(), objeto
				.getPosicion().y+objeto.getHeigth()), 5, 5);
		e.setFillColor(ObjetoUMLGraph.azul);
		e.removeAllGestureEventListeners();
		e.unregisterAllInputProcessors();
		e.registerInputProcessor(new DragProcessor(mtApp));
		e.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				objeto.setWidth(objeto.getWidth()+de.getTranslationVect().x);
				objeto.setHeigth(objeto.getHeigth()+de.getTranslationVect().y);
				rectangulo.setSizeXYGlobal(objeto.getWidth(),objeto.getHeigth());		
			e.setSizeXYGlobal(10, 10);									
				return false;
			}
		});
		
		rectangulo.addChild(e);
		rectangulo.addChild(header);
		rectangulo.addChild(body);	
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
}
