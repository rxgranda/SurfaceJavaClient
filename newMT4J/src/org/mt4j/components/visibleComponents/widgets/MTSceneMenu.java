/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.components.visibleComponents.widgets;

import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;
import org.mt4j.util.opengl.GLTextureSettings;

import advanced.drawing.MainDrawingScene;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Class MTSceneMenu. A menu used in scenes to close the scene and/or pop back to another scene.
 * 
 * @author Christopher Ruff
 */
public class MTSceneMenu extends MTRectangle{
	/** The Constant logger. */
	private static final ILogger logger = MTLoggerFactory.getLogger(MTSceneMenu.class.getName());
	static{
//		logger.setLevel(ILogger.ERROR);
//		logger.setLevel(ILogger.WARN);
//		logger.setLevel(ILogger.DEBUG);
		logger.setLevel(ILogger.INFO);
	}
	
	/** The app. */
	private MTApplication app;
	
	/** The scene. */
	private Iscene scene;
	
	public Iscene getScene() {
		return scene;
	}

	public void setScene(Iscene scene) {
		this.scene = scene;
	}


	/** The overlay group. */
	private MTComponent overlayGroup;
	
	/** The scene texture. */
	private MTSceneTexture sceneTexture;
	
	/** The windowed scene. */
	private boolean windowedScene;
	
	/** The menu image. */
	private static PImage menuImage;
	
	/** The close button image. */
	private static PImage borrarButtonImage;
	
	/** The restore button image. */
	private static PImage guardarButtonImage;
	
	/** The restore button image. */
	private static PImage deshacerButtonImage;
	
	
	//TODO maybe add minimize mode -> dont show scene but dont destroy it -> maby keep it in a MTList
	
	/**
	 * Instantiates a new mT scene menu.
	 * @param app the app
	 * @param scene the scene
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public MTSceneMenu(MTApplication app, Iscene scene, float x, float y, float width, float height) {
		super(app, x, y, width, height);
		this.app = app;
		this.scene = scene;
		
		this.windowedScene = false;

		this.init(x, y, width, height);
	}
	
	/**
	 * Instantiates a new mT scene menu.
	 * @param app the app
	 * @param sceneTexture the scene texture
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public MTSceneMenu(MTApplication app, MTSceneTexture sceneTexture, float x, float y, float width, float height) {
		super(app, x, y, width, height);
		this.app = app;
		this.scene = sceneTexture.getScene();
		this.sceneTexture = sceneTexture;
		
		this.windowedScene = true;
		
		this.init(x, y, width, height);
	}
	
	
	/**
	 * Inits the.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	private void init(float x, float y, float width, float height){
		this.setNoStroke(true);
		this.setFillColor(new MTColor(255,255,255,150));
		
		overlayGroup = new MTOverlayContainer(app, "Window Menu Overlay Group");
		
		if (menuImage == null){
			String altImagesPath = "data" + MTApplication.separator ;	
			menuImage = app.loadImage( altImagesPath+
					"menutop2.png");
		}
		
		if (MT4jSettings.getInstance().isOpenGlMode()){
			GLTextureSettings ts = new GLTextureSettings();
			ts.wrappingHorizontal = WRAP_MODE.CLAMP;
			ts.wrappingVertical = WRAP_MODE.CLAMP;
			GLTexture glTex = new GLTexture(app, menuImage.width, menuImage.height, ts);
			glTex.loadGLTexture(menuImage);
			this.setTexture(glTex);
		}else{
			this.setTexture(menuImage);
		}
		
		AbstractShape menuShape = this;
		menuShape.unregisterAllInputProcessors();
		menuShape.removeAllGestureEventListeners(DragProcessor.class);
		menuShape.registerInputProcessor(new DragProcessor(app));
		
		float buttonWidth = 60;
		float buttonHeight = 60;
		final float buttonOpacity = 150;
		
		//CLOSE BUTTON
//		Vector3D a = new Vector3D(-width * 1.2f, height/2f);
		Vector3D a = new Vector3D(-width * 1.55f, 0);
		a.rotateZ(PApplet.radians(-170));
		final MTRectangle borrarButton = new MTRectangle(app, app.width/2 +120 -buttonWidth/2, 20, buttonWidth, buttonHeight);
		
		if (borrarButtonImage == null){
			/*borrarButtonImage = app.loadImage(MT4jSettings.getInstance().getDefaultImagesPath() +
					"borrar.png");*/
			String altImagesPath = "data" + MTApplication.separator ;	
			borrarButtonImage = app.loadImage( altImagesPath+
					"borrador.png");
		}
		
		borrarButton.setTexture(borrarButtonImage);
		borrarButton.setFillColor(new MTColor(255, 255, 255, buttonOpacity));
		borrarButton.setNoStroke(true);
		borrarButton.setVisible(true);
		borrarButton.removeAllGestureEventListeners(DragProcessor.class);
		borrarButton.removeAllGestureEventListeners(RotateProcessor.class);
		borrarButton.removeAllGestureEventListeners(ScaleProcessor.class);
		this.addChild(borrarButton);
		
		//Check if this menu belongs to a window Scene (MTSceneWindow)
		//or was added to a normal scene
		//-> if its not a windowed scene we dont display the Restore button
		if (this.windowedScene){
			//RESTORE BUTTON
			Vector3D b = new Vector3D(-width * 1.55f, 0);
			b.rotateZ(PApplet.radians(-10));
			final MTRectangle guardarButton = new MTRectangle(app, app.width/2 -120 -buttonWidth/2,20 , buttonWidth, buttonHeight);
			
			if (guardarButtonImage == null){
				/*guardarButtonImage = app.loadImage(MT4jSettings.getInstance().getDefaultImagesPath() +
						"restoreButton64.png");
						*/
				String altImagesPath = "data" + MTApplication.separator ;	
				guardarButtonImage = app.loadImage( altImagesPath+
						"guardar.png");
			}
			
			guardarButton.setTexture(guardarButtonImage);
			guardarButton.setFillColor(new MTColor(255, 255, 255, buttonOpacity));
			guardarButton.setNoStroke(true);
			guardarButton.setVisible(true);
			guardarButton.removeAllGestureEventListeners(DragProcessor.class);
			guardarButton.removeAllGestureEventListeners(RotateProcessor.class);
			guardarButton.removeAllGestureEventListeners(ScaleProcessor.class);			
			this.addChild(guardarButton);
			////
			Vector3D c = new Vector3D(-width * .55f, 0);
			c.rotateZ(PApplet.radians(-90));
			final MTRectangle deshacerButton = new MTRectangle(app,app.width/2 -buttonWidth/2, 20, buttonWidth, buttonHeight);
			
			if (deshacerButtonImage== null){
				String altImagesPath = "data" + MTApplication.separator ;	
				deshacerButtonImage = app.loadImage( altImagesPath+
						"undo.png");
			}
			
			deshacerButton.setTexture(deshacerButtonImage);
			deshacerButton.setFillColor(new MTColor(255, 255, 255, buttonOpacity));
			deshacerButton.setNoStroke(true);
			deshacerButton.setVisible(true);
			deshacerButton.removeAllGestureEventListeners(DragProcessor.class);
			deshacerButton.removeAllGestureEventListeners(RotateProcessor.class);
			deshacerButton.removeAllGestureEventListeners(ScaleProcessor.class);			
			this.addChild(deshacerButton);
			
			
			
			deshacerButton.registerInputProcessor(new TapProcessor(app));
			deshacerButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
				public boolean processGestureEvent(MTGestureEvent ge) {
					TapEvent te = (TapEvent)ge;
					switch (te.getId()) {
					case MTGestureEvent.GESTURE_STARTED:
						highlightButton(deshacerButton);
						break;
					case MTGestureEvent.GESTURE_UPDATED:
						break;
					case MTGestureEvent.GESTURE_ENDED:
						if (te.isTapped()){
													
							if(getScene() instanceof MainDrawingScene ){
								unhighlightButton(deshacerButton, buttonOpacity);
								MainDrawingScene.deshacer();
							}
						
						}
						//tapOnly.setFillColor(textAreaColor);
						break;
					}
					return false;
				}
			});
			
			
			guardarButton.registerInputProcessor(new TapProcessor(app));
			guardarButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
				public boolean processGestureEvent(MTGestureEvent ge) {
					TapEvent te = (TapEvent)ge;
					switch (te.getId()) {
					case MTGestureEvent.GESTURE_STARTED:
						highlightButton(guardarButton);
						break;
					case MTGestureEvent.GESTURE_UPDATED:
						break;
					case MTGestureEvent.GESTURE_ENDED:
						if (te.isTapped()){
													
							if(getScene() instanceof MainDrawingScene ){
								unhighlightButton(guardarButton, buttonOpacity);
								MainDrawingScene escena=(MainDrawingScene)getScene();
								escena.guardar();
							}
						
						}
						//tapOnly.setFillColor(textAreaColor);
						break;
					}
					return false;
				}
			});
			
			
			

			borrarButton.registerInputProcessor(new TapProcessor(app));
			borrarButton.addGestureListener(TapProcessor.class, new IGestureEventListener() {
				public boolean processGestureEvent(MTGestureEvent ge) {
					TapEvent te = (TapEvent)ge;
					switch (te.getId()) {
					case MTGestureEvent.GESTURE_STARTED:
						highlightButton(borrarButton);
						break;
					case MTGestureEvent.GESTURE_UPDATED:
						break;
					case MTGestureEvent.GESTURE_ENDED:
						if (te.isTapped()){
									
							if(getScene() instanceof MainDrawingScene ){
								System.out.println("--> Cambiar modo!");
								InputCursor m=te.getCursor();							
								MainDrawingScene.setDeleteMode(m.sessionID);
								unhighlightButton(borrarButton, buttonOpacity);
									
							
							}
							
						
						}
						//tapOnly.setFillColor(textAreaColor);
						break;
					}
					return false;
				}
			});
			
			////
			/*menuShape.addGestureListener(DragProcessor.class, new IGestureEventListener() {
				public boolean processGestureEvent(MTGestureEvent ge) {
					DragEvent de = (DragEvent)ge;
					switch (de.getId()) {
					case MTGestureEvent.GESTURE_STARTED:
						guardarButton.setVisible(true);
						borrarButton.setVisible(true);
						deshacerButton.setVisible(true);
						unhighlightButton(borrarButton, buttonOpacity);
						unhighlightButton(guardarButton, buttonOpacity);
						unhighlightButton(deshacerButton, buttonOpacity);
						break;
					case MTGestureEvent.GESTURE_UPDATED:
						//Mouse over effect
						if (borrarButton.containsPointGlobal(de.getTo())){
							highlightButton(borrarButton);
						}else{
							unhighlightButton(borrarButton, buttonOpacity);
						}
						if (guardarButton.containsPointGlobal(de.getTo())){
							highlightButton(guardarButton);
						}else{
							unhighlightButton(guardarButton, buttonOpacity);
						}
						if (deshacerButton.containsPointGlobal(de.getTo())){
							highlightButton(deshacerButton);
						}else{
							unhighlightButton(deshacerButton, buttonOpacity);
						}
						break;
					case MTGestureEvent.GESTURE_ENDED:
						unhighlightButton(borrarButton, buttonOpacity);
						unhighlightButton(guardarButton, buttonOpacity);
						unhighlightButton(deshacerButton, buttonOpacity);
						
						InputCursor cursor = de.getDragCursor();
						Vector3D deshacerBotonIntersection = deshacerButton.getIntersectionGlobal(Tools3D.getCameraPickRay(getRenderer(), guardarButton, cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY()));
						if (deshacerBotonIntersection != null){
							
							if(getScene() instanceof MainDrawingScene ){								
								MainDrawingScene.deshacer();
							}
							
						
						}
						
						Vector3D guardarButtonIntersection = guardarButton.getIntersectionGlobal(Tools3D.getCameraPickRay(getRenderer(), guardarButton, cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY()));
						if (guardarButtonIntersection != null){
							logger.debug("--> RESTORE!");
							//MTSceneMenu.this.sceneTexture.restore();
							// Para deshacer
							if(getScene() instanceof MainDrawingScene ){
								MainDrawingScene escena=(MainDrawingScene)getScene();
								escena.guardar();
							}
							
							
						}
						Vector3D borrarButtonIntersection = borrarButton.getIntersectionGlobal(Tools3D.getCameraPickRay(getRenderer(), borrarButton, cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY()));
						if (borrarButtonIntersection != null){
//							if (app.popScene()){
//								app.removeScene(scene); //FIXME wont work if the scene has a transition because we cant remove the still active scene
////								destroy(); //this will be destroyed with the scene
//								sceneTexture.destroy(); //destroys also the MTSceneWindow and with it the scene
//								logger.debug("--> CLOSE!");
//							}
							//if (sceneTexture.restore()){
//								app.removeScene(scene); //FIXME wont work if the scene has a transition because we cant remove the still active scene
//								destroy(); //this will be destroyed with the scene
								//sceneTexture.destroy(); //destroys also the MTSceneWindow and with it the scene
								//logger.debug("--> CLOSE!");
								System.out.println("--> MODO BORRAR!");
								if(getScene() instanceof MainDrawingScene ){
									System.out.println("--> Cambiar modo!");
									InputCursor m=de.getDragCursor();							
									MainDrawingScene.setDeleteMode(m.sessionID);
								///}
								
							}
						}
						
						guardarButton.setVisible(false);
						borrarButton.setVisible(false);
						deshacerButton.setVisible(false);
						break;
					default:
						break;
					}
					return false;
				}
			});
			
			
			*/
		}else{
			if (scene != null){
				menuShape.addGestureListener(DragProcessor.class, new IGestureEventListener() {
					public boolean processGestureEvent(MTGestureEvent ge) {
						DragEvent de = (DragEvent)ge;
						switch (de.getId()) {
						case MTGestureEvent.GESTURE_STARTED:
							borrarButton.setVisible(true);
							unhighlightButton(borrarButton, buttonOpacity);
							break;
						case MTGestureEvent.GESTURE_UPDATED:
							//Mouse over effect
							if (borrarButton.containsPointGlobal(de.getTo())){
								highlightButton(borrarButton);
							}else{
								unhighlightButton(borrarButton, buttonOpacity);
							}
							break;
						case MTGestureEvent.GESTURE_ENDED:
							unhighlightButton(borrarButton, buttonOpacity);
							
							InputCursor cursor = de.getDragCursor();
							Vector3D closeButtonIntersection = borrarButton.getIntersectionGlobal(Tools3D.getCameraPickRay(getRenderer(), borrarButton, cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY()));
							if (closeButtonIntersection != null){
								if (app.popScene()){
									destroy(); //Destroy this
									scene.destroy(); //Destroy the scene 
									logger.debug("--> CLOSE!");
								}
							}
							borrarButton.setVisible(false);
							break;
						default:
							break;
						}
						return false;
					}
				});
			}
		}
		
		
		
	}
	
//	private void restoreSceneWindow(){
//		this.removeFromScene();
//	}
//	
//	private void closeSceneWindow(){
//		MTSceneWindow.this.destroy();
//	}
	
	/**
 * Highlight button.
 * 
 * @param shape the shape
 */
private void highlightButton(AbstractShape shape){
		MTColor c = shape.getFillColor();
		c.setAlpha(255);
		shape.setFillColor(c);
	}
	
	/**
	 * Unhighlight button.
	 * 
	 * @param shape the shape
	 * @param opacity the opacity
	 */
	private void unhighlightButton(AbstractShape shape, float opacity){
		MTColor c = shape.getFillColor();
		c.setAlpha(opacity);
		shape.setFillColor(c);
	}
	
	
	/**
	 * Adds the to scene.
	 */
	public void addToScene(){
		MTComponent cursorTraceContainer = null;
		MTCanvas canvas = scene.getCanvas();
		
		/*
		//Re-use cursor trace group which is always on top for this menu
		MTComponent[] children = canvas.getChildren();
		for (int i = 0; i < children.length; i++) {
			MTComponent component = children[i];
			if (component instanceof MTOverlayContainer 
					&&
				component.getName().equalsIgnoreCase("Cursor Trace group")){
				cursorTraceContainer  = component;
				component.addChild(0, this); //add to cursor trace overlay container
			}
		}
		*/
		
//		/*
		//cursor tracer group NOT found in the scene -> add overlay container to canvas
		if (cursorTraceContainer == null){ 
			overlayGroup.addChild(this);
			canvas.addChild(overlayGroup);
		}
//		*/
	}
	
	
	/**
	 * Removes the from scene.
	 */
	public void removeFromScene(){
		MTComponent cursorTraceContainer = null;
		MTCanvas canvas = scene.getCanvas();
		
		/*
		//Re-use cursor trace group which is always on top for this menu
		MTComponent[] children = canvas.getChildren();
		for (int i = 0; i < children.length; i++) {
			MTComponent component = children[i];
			if (component instanceof MTOverlayContainer 
					&&
				component.getName().equalsIgnoreCase("Cursor Trace group")){
				cursorTraceContainer  = component;
				if (cursorTraceContainer.containsChild(this)){
					cursorTraceContainer.removeChild(this);
				}
			}
		}
		*/
		
//		/*
		//cursor tracer group NOT found in the scene -> add overlay container to canvas
		if (cursorTraceContainer == null){ 
			if (canvas.containsChild(overlayGroup)){
				canvas.removeChild(overlayGroup);
			}
		}
//		*/
	}
	
	
}
