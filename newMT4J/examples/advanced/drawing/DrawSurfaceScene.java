package advanced.drawing;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
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
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import advanced.umleditor.UMLFacade;
import advanced.umleditor.UMLRecognizer;
import advanced.umleditor.UMLCollection;
import advanced.umleditor.impl.Entidad_Impl;
import advanced.umleditor.impl.ObjetoUMLGraph;
import advanced.umleditor.impl.Relacion_Impl;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Persona;
import advanced.umleditor.logic.Relacion;
import processing.core.PApplet;

import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.MTTextField;

;

public class DrawSurfaceScene extends AbstractScene {	

	private MTApplication mtApp;

	private MTRectangle container;

	//Pintar el trazo que se está dibujando
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
	

	// test
	private static Persona persona = new Persona("roger", "granda", 1);
	

	// TODO only works as lightweight scene atm because the framebuffer isnt
	// cleared each frame
	// TODO make it work as a heavywight scene
	// TODO scale smaller at higher speeds?
	// TODO eraser?
	// TODO get blobwidth from win7 touch events and adjust the brush scale
	ArrayList<Vector3D> puntos;
	Vector3D puntoInicio, puntoFin;
	
	public void add(Vector3D vec) {
		puntos.add(vec);
	}

	public synchronized void eliminarPuntos() {
		puntos = new ArrayList<Vector3D>();
	}

	public void limpiar() {
		registerPreDrawAction(new IPreDrawAction() {
			public void processAction() {
				Vector3D ultimo = null;
				for (Vector3D vec : puntos) {
					boolean firstPoint = false;
					Vector3D lastDrawnPoint = ultimo;
					Vector3D pos = new Vector3D(vec.x, vec.y, 0);
					// Proyecto
					// System.out.println("ID: " + m.sessionID);
					System.out.println("Eliminar: X:" + vec.x + "Y:" + vec.y);

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
					// System.out.println("Steps: " + stepsToTake);

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
						// System.out.println("X:"+diff.x+"Y:"+ diff.y);

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
													 * System.out.println
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
				// System.out.println("Eliminado");
				eliminarPuntos();
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
			MTRectangle container) {

		super(mtApplication, name);
		this.mtApp = mtApplication;
		this.container = container;

		this.getCanvas().setDepthBufferDisabled(true);

		/*
		 * this.drawShape = getDefaultBrush(); this.localBrushCenter =
		 * drawShape.getCenterPointLocal(); this.brushWidthHalf =
		 * drawShape.getWidthXY(TransformSpace.LOCAL)/2f; this.brushHeightHalf =
		 * drawShape.getHeightXY(TransformSpace.LOCAL)/2f; this.stepDistance =
		 * brushWidthHalf/2.5f;
		 */

		this.brushColor = new MTColor(0, 0, 0);
		this.brushScale = 0.05f;
		this.dynamicBrush = true;
		// this.stepDistance = 5.5f;

		this.cursorToLastDrawnPoint = new HashMap<InputCursor, Vector3D>();

		// Proyecto
		final UMLFacade recognizer = new UMLFacade(persona);
		puntos = new ArrayList<Vector3D>();
		// Proyecto

		this.getCanvas().addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) {
					final AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt) inEvt;
					final InputCursor m = posEvt.getCursor();
					IMTComponent3D comp = m.getTarget();

					System.out.println(comp.toString());

					IMTComponent3D secondresult = (IMTComponent3D) getCanvas()
							.getComponentAt((int) m.getPosition().x,
									(int) m.getPosition().y);
					System.out.println("SECOND: " + secondresult);
					// getCanvas().drawAndUpdateCanvas(mtApp.g, 0);
					getCanvas().drawAndUpdateCanvas(mtApp.g, 0);
					// getSceneCam().update();
					if (comp instanceof MTCanvas) {
						// System.out.println("PrevPos: " + prevPos);
						if (posEvt.getId() != AbstractCursorInputEvt.INPUT_ENDED) {
							registerPreDrawAction(new IPreDrawAction() {
								public void processAction() {
									boolean firstPoint = false;
									Vector3D lastDrawnPoint = cursorToLastDrawnPoint
											.get(m);
									Vector3D pos = new Vector3D(posEvt.getX(),
											posEvt.getY(), 0);
									// Proyecto
									// System.out.println("ID: " + m.sessionID);
									// System.out.println("Pos: X:"+posEvt.getX()+"Y:"+
									// posEvt.getY());

									add(new Vector3D(posEvt.getX(), posEvt
											.getY(), 0));

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
									// System.out.println("Steps: " +
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
										recognizer.anadirPunto(currentPos.x,
												currentPos.y);
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
										// System.out.println("X:"+diff.x+"Y:"+
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
										AbstractShape brushToDraw = drawShape;

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

							// int resultado=recognizer.recognize();
							final ObjetoUML objeto=recognizer
									.reconocerObjeto();
							final int tipo_objeto = objeto.getTipo();
							// if(resultado==UMLCollection.INVALIDO){
							limpiar();
							// }else{
							// UMLCollection.anadirObjeto(resultado,persona );
							// eliminarPuntos();
							// }
							System.out.println("Termino Input");
							if (recognizer.getObjeto() != ObjetoUML.OBJETO_INVALIDO) {

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

								switch (tipo_objeto) {
								case ObjetoUML.ENTIDAD:

									ObjetoUMLGraph diagrama= new Entidad_Impl(mtApp, objeto);
									objeto.setFigura(diagrama);
									anadirObjeto(diagrama.getFigura());
									break;
								case ObjetoUML.RELACION:
									ObjetoUMLGraph linea= new Relacion_Impl(mtApp, objeto);
									objeto.setFigura(linea);
									anadirObjeto(linea.getFigura());
									break;
								default:
									break;
								}
							}

							// setBrushColor(new MTColor(255,0,0));

						}
					}
				}

				return false;
			}
		});

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
}
