package advanced.physics.scenes;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTBlobInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.MultipleDragProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;
import processing.core.PImage;
import TUIO.TuioPoint;
import advanced.physics.physicsShapes.IPhysicsComponent;
import advanced.physics.physicsShapes.PhysicsCircle;
import advanced.physics.physicsShapes.PhysicsPolygon;
import advanced.physics.physicsShapes.PhysicsRectangle;
import advanced.physics.physicsShapes.Poligono;
import advanced.physics.util.PhysicsHelper;
import advanced.physics.util.UpdatePhysicsAction;

public class AirHockeyScene extends AbstractScene {
	private float timeStep = 1.0f / 60.0f;
	private int constraintIterations = 10;
	
	/** THE CANVAS SCALE **/
	private float scale = 20;
	private MTApplication app;
	private World world;
	
	private MTComponent physicsContainer;
	private MTTextArea t1;
	private MTTextArea t2;
	private HashMap< InputCursor, MTComponent> cursores;
	private int scorePlayer1;
	private int scorePlayer2;
	private HockeyBall ball;
	/*
	private Minim minim;
	private AudioSnippet wallHit;
	private AudioSample paddleBallClash;
	private AudioSnippet goalHit;
	private AudioSnippet paddleHit;
	*/
	private Paddle redCircle;
	private Paddle blueCircle,blueCircle1,blueCircle2;
	MTRectangle contenedor ;
	private boolean enableSound = true;
	
	
	//TODO sometimes goal not recognized when shot at fast
	//TODO after goal reset puck and make 3 second countdown -> dont use physics until countdown up!
	//TODO get graphics, sounds, effects
	
//	private String imagesPath = System.getProperty("user.dir") + File.separator + "examples" + File.separator +"advanced"+ File.separator +  "physics"  + File.separator + "data" +  File.separator  + "images" +  File.separator;
	private String imagesPath =  "advanced" + MTApplication.separator +  "physics"  + MTApplication.separator + "data" +  MTApplication.separator  + "images" +  MTApplication.separator;
	
	
	public AirHockeyScene(MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
//		this.setClearColor(new MTColor(120,150,150));
//		this.setClearColor(new MTColor(190, 190, 170, 255));
		this.setClearColor(new MTColor(0, 0, 0, 255));
//		this.setClearColor(new MTColor(40, 40, 40, 255));
		//this.registerGlobalInputProcessor(new CursorTracer(app, this));
		
		this.scorePlayer1 = 0;
		this.scorePlayer2 = 0;
		
		float worldOffset = 10; //Make Physics world slightly bigger than screen borders
		//Physics world dimensions
		AABB worldAABB = new AABB(new Vec2(-worldOffset, -worldOffset), new Vec2((app.width)/scale + worldOffset, (app.height)/scale + worldOffset));
		Vec2 gravity = new Vec2(0, 0);
		boolean sleep = true;
		//Create the pyhsics world
		this.world = new World(worldAABB, gravity, sleep);
		
		//Update the positions of the components according the the physics simulation each frame
		this.registerPreDrawAction(new UpdatePhysicsAction(world, timeStep, constraintIterations, scale));
		
		physicsContainer = new MTComponent(app);
		physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		this.getCanvas().addChild(physicsContainer);
		
		//Create borders around the screen
		this.createScreenBorders(physicsContainer);
		
		//Create gamefield marks
		MTLine line = new MTLine(mtApplication, mtApplication.width/2f/scale, 0, mtApplication.width/2f/scale, mtApplication.height/scale);
		line.setPickable(false);
//		line.setStrokeColor(new MTColor(0,0,0));
		line.setStrokeColor(new MTColor(150,150,150));
		line.setStrokeWeight(0.5f);
		physicsContainer.addChild(line);
		
		MTEllipse centerCircle = new MTEllipse(mtApplication, new Vector3D(mtApplication.width/2f/scale, mtApplication.height/2f/scale), 80/scale, 80/scale);
		centerCircle.setPickable(false);
		centerCircle.setNoFill(true);
//		centerCircle.setStrokeColor(new MTColor(0,0,0));
		centerCircle.setStrokeColor(new MTColor(150,150,150));
		centerCircle.setStrokeWeight(0.5f);
		physicsContainer.addChild(centerCircle);
		
		MTEllipse centerCircleInner = new MTEllipse(mtApplication, new Vector3D(mtApplication.width/2f/scale, mtApplication.height/2f/scale), 10/scale, 10/scale);
		centerCircleInner.setPickable(false);
		centerCircleInner.setFillColor(new MTColor(160,160,160));
//		centerCircleInner.setStrokeColor(new MTColor(150,150,150));
//		centerCircleInner.setStrokeColor(new MTColor(0,0,0));
		centerCircleInner.setStrokeColor(new MTColor(150,150,150));
		centerCircleInner.setStrokeWeight(0.5f);
		physicsContainer.addChild(centerCircleInner);
		this.cursores = new HashMap<InputCursor, MTComponent>();
		//Create the paddles
		PImage paddleTex = mtApplication.loadImage(imagesPath + "paddle.png");
		redCircle = new Paddle(app, new Vector3D(mtApplication.width - 60, mtApplication.height/2f), 50, world, 1.0f, 0.3f, 0.4f, scale);
		redCircle.setTexture(paddleTex);
		redCircle.setFillColor(new MTColor(255,50,50));
		redCircle.setNoStroke(true);
		redCircle.setName("red");
		redCircle.setPickable(false);
		physicsContainer.addChild(redCircle);
		
		blueCircle = new Paddle(app, new Vector3D(80, mtApplication.height/2f), 50, world, 1.0f, 0.3f, 0.4f, scale);
		blueCircle.setTexture(paddleTex);
		blueCircle.setFillColor(new MTColor(50,50,255));
		blueCircle.setNoStroke(true);
		blueCircle.setName("blue");
		blueCircle.setPickable(false);
		physicsContainer.addChild(blueCircle);
		
		blueCircle1 = new Paddle(app, new Vector3D(80, mtApplication.height/2f), 50, world, 1.0f, 0.3f, 0.4f, scale);
		blueCircle1.setTexture(paddleTex);
		blueCircle1.setFillColor(new MTColor(50,50,255));
		blueCircle1.setNoStroke(true);
		blueCircle1.setName("blue");
		blueCircle1.setPickable(false);
		physicsContainer.addChild(blueCircle1);
		
		blueCircle2 = new Paddle(app, new Vector3D(80, mtApplication.height/2f), 50, world, 1.0f, 0.3f, 0.4f, scale);
		blueCircle2.setTexture(paddleTex);
		blueCircle2.setFillColor(new MTColor(50,50,255));
		blueCircle2.setNoStroke(true);
		blueCircle2.setName("blue");
		blueCircle2.setPickable(false);
		physicsContainer.addChild(blueCircle2);
		
		
		
		
		//Create the ball
		ball = new HockeyBall(app, new Vector3D(mtApplication.width/2f, mtApplication.height/2f), 38, world, 0.5f, 0.005f, 0.70f, scale);
//		MTColor ballCol = new MTColor(0,255,0);
//		ball.setFillColor(ballCol);
		PImage ballTex = mtApplication.loadImage(imagesPath + "puk.png");
		ball.setTexture(ballTex);
//		ball.setFillColor(new MTColor(160,160,160,255));
		ball.setFillColor(new MTColor(255,255,255,255));
		ball.setNoStroke(true);
		ball.setName("ball");
		physicsContainer.addChild(ball);
		ball.getBody().applyImpulse(new Vec2(ToolsMath.getRandom(-8f, 8),ToolsMath.getRandom(-8, 8)), ball.getBody().getWorldCenter());
		
		//Create the GOALS
		HockeyGoal goal1 = new HockeyGoal(new Vector3D(0, mtApplication.height/2f), 50, mtApplication.height/4f, mtApplication, world, 0.0f, 0.1f, 0.0f, scale);
		goal1.setName("goal1");
		goal1.setFillColor(new MTColor(0,0,255));
		goal1.setStrokeColor(new MTColor(0,0,255));
		physicsContainer.addChild(goal1);
		
		HockeyGoal goal2 = new HockeyGoal(new Vector3D(mtApplication.width, mtApplication.height/2f), 50, mtApplication.height/4f, mtApplication, world, 0.0f, 0.1f, 0.0f, scale);
		goal2.setName("goal2");
		goal2.setFillColor(new MTColor(255,0,0));
		goal2.setStrokeColor(new MTColor(255,0,0));
		physicsContainer.addChild(goal2);
		
		//Make two components for both game field sides to drag the puks upon
		MTRectangle leftSide = new MTRectangle(
				app, PhysicsHelper.scaleDown(0, scale), 
				PhysicsHelper.scaleDown(0, scale), PhysicsHelper.scaleDown(app.width/2f, scale)
				, PhysicsHelper.scaleDown(app.height, scale));
		leftSide.setName("left side");
		leftSide.setNoFill(true); //Make it invisible -> only used for dragging
		leftSide.setNoStroke(true);
		leftSide.unregisterAllInputProcessors();
	//	leftSide.removeAllGestureEventListeners(DragProcessor.class);
		//leftSide.registerInputProcessor(new DragProcessor(app));
		//leftSide.addGestureListener(DragProcessor.class, new GameFieldHalfDragListener(blueCircle));
		//physicsContainer.addChild(0, leftSide);
		MTRectangle rightSide = new MTRectangle(
				app, PhysicsHelper.scaleDown(app.width/2f, scale), 
				PhysicsHelper.scaleDown(0, scale), PhysicsHelper.scaleDown(app.width, scale)
				, PhysicsHelper.scaleDown(app.height, scale));
		rightSide.setName("right Side");
		rightSide.setNoFill(true); //Make it invisible -> only used for dragging
		rightSide.setNoStroke(true);
		//rightSide.unregisterAllInputProcessors();
		//rightSide.removeAllGestureEventListeners(DragProcessor.class);
		//rightSide.registerInputProcessor(new DragProcessor(app));
		//rightSide.addGestureListener(DragProcessor.class, new GameFieldHalfDragListener(redCircle));
		//physicsContainer.addChild(0, rightSide);
		contenedor = new MTRectangle(
				app, PhysicsHelper.scaleDown(0, scale), 
				PhysicsHelper.scaleDown(0, scale), PhysicsHelper.scaleDown(app.width, scale)
				, PhysicsHelper.scaleDown(app.height, scale));
		contenedor.setName("left side");
		contenedor.setNoFill(true); //Make it invisible -> only used for dragging
		contenedor.setNoStroke(true);
		contenedor.unregisterAllInputProcessors();
		contenedor.removeAllGestureEventListeners();
		contenedor.registerInputProcessor(new MultipleDragProcessor(app));
		
		//contenedor.addGestureListener(MultipleDragProcessor.class, new GameFieldHalfDragListener());
		
		physicsContainer.addChild(contenedor);
		//physicsContainer.addGestureListener(DragProcessor.class, new GameFieldHalfDragListener());
		Vertex[] polyVertices = new Vertex[]{
				new Vertex(-10,0,0),
				new Vertex(10,0,0),
				new Vertex(10,50,0),
				new Vertex(60,50,0),
				new Vertex(60,70,0),
				new Vertex(10,70,0),
				
				new Vertex(10,120,0),
				new Vertex(-10,120,0),
				new Vertex(-10,70,0),
				new Vertex(-60,70,0),
				new Vertex(-60,50,0),
				new Vertex(-10,50,0),
				new Vertex(-10,0,0),
		};
		
		PhysicsPolygon physPoly = new PhysicsPolygon(polyVertices, new Vector3D(250,250), app, world, 1.0f, 0.3f, 0.4f, scale);
		MTColor polyCol = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
		physPoly.setFillColor(polyCol);
		physPoly.setStrokeColor(polyCol);
	
		//For an anti-aliased outline
		List<Vertex[]> contours = new ArrayList<Vertex[]>();
		contours.add(polyVertices);
		physPoly.setOutlineContours(contours);
		physPoly.setNoStroke(false);
		physicsContainer.addChild(physPoly);
		
		
		
		
		  Vertex[] vertices = new Vertex[]{
				  
				  
					new Vertex(5,0,0),
					new Vertex(10,0,0),
					new Vertex(10,50,0),
					new Vertex(60,50,0),
					new Vertex(60,70,0),
		  };
		   
		
		
		   Poligono prueba2 = new Poligono(vertices,  new Vector3D(app.width/2+100,app.height/2), app, world, 5.0f, 0.7f, 0.4f, scale);
		   physicsContainer.addChild(prueba2);
		    
		
		
		/*
		
        physicsContainer.addInputListener(new IMTInputEventListener() {
        	//@Override
        	public boolean processInputEvent(MTInputEvent inEvt){
        		if(inEvt instanceof AbstractCursorInputEvt){
        			AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inEvt;
        			if (posEvt.hasTarget() && posEvt.getTarget().equals(getCanvas())){
        				InputCursor m = posEvt.getCursor();
        		

        				Vector3D pos = new Vector3D(posEvt.getX(), posEvt.getY(), 0);
        				
        				
        				
        				
        				if(posEvt instanceof MTBlobInputEvt){
        				MTBlobInputEvt	blobevt =(MTBlobInputEvt) posEvt;
        			
        				System.out.println("sdsdfsdfsdf");
        				
        				if(blobevt.getId()!=blobevt.INPUT_STARTED){
        				
        				TuioPoint punto1=blobevt.getPuntos().get(0);
        				TuioPoint punto2=blobevt.getPuntos().get(1);
        				TuioPoint punto3=blobevt.getPuntos().get(2);
        				TuioPoint punto4=blobevt.getPuntos().get(3);
        				TuioPoint punto5=blobevt.getPuntos().get(4);
        				
        				
        				System.out.println("START");
        		
        				
        			}
        		}
        		
        		
        		
        		
        	
        	}
        		}
        		return false;
        	}
		});*/
		
		//Display Score UI
		MTComponent uiLayer = new MTComponent(mtApplication, new MTCamera(mtApplication));
		uiLayer.setDepthBufferDisabled(true);
		getCanvas().addChild(uiLayer);
		IFont font = FontManager.getInstance().createFont(mtApplication, "arial", 50, MTColor.WHITE);
		
		t1 = new MTTextArea(mtApplication, font);
		t1.setPickable(false);
		t1.setNoFill(true);
		t1.setNoStroke(true);
		t1.setPositionGlobal(new Vector3D(5,30,0));
		uiLayer.addChild(t1);
		
		t2 = new MTTextArea(mtApplication, font);
		t2.setPickable(false);
		t2.setNoFill(true);
		t2.setNoStroke(true);
		t2.setPositionGlobal(new Vector3D(mtApplication.width - 65 , 30,0));
		uiLayer.addChild(t2);
		this.updateScores();
		
		
		
		getCanvas().addInputListener(new IMTInputEventListener() {
			Body	 body = null;
	    	
			public boolean processInputEvent(MTInputEvent inEvt) {
				if (inEvt instanceof AbstractCursorInputEvt) {
					AbstractCursorInputEvt ce = (AbstractCursorInputEvt) inEvt;
					
					
					if(ce instanceof MTBlobInputEvt){
						MTBlobInputEvt	blobevt =(MTBlobInputEvt) ce;
						Vector3D to = new Vector3D(ce.getX(),ce.getY());
						PhysicsHelper.scaleDown(to, scale);
						
						
						try{
						MouseJoint mouseJoint;
						switch(blobevt.getId()){
						case AbstractCursorInputEvt.INPUT_STARTED:
							ArrayList<TuioPoint> puntos = blobevt.getPuntos();
							int npts = puntos.size();
							Vertex[] polyVertices = new Vertex[npts];
							System.out.println("START"+blobevt.getBlobId());
							for(int i = 0 ; i<npts; i ++){
								polyVertices[i]= new Vertex(puntos.get(i).xpos, puntos.get(i).ypos);
								
							
							}
							
						//	MTPolygon poly = new MTPolygon(getMTApplication(),polyVertices);
							//poly.setFillColor(new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255)));
							//MTTextArea textoid = new MTTextArea(app,blobevt.getX(),blobevt.getY(), 100 ,100);
						
						
							//poly.addChild(textoid);
							//PhysicsPolygon physPoly = new PhysicsPolygon(polyVertices, new Vector3D(blobevt.getX(),blobevt.getY()), app, world, 1.0f, 0.3f, 0.4f, scale);
							Poligono person = new Poligono(polyVertices, new Vector3D(to.x, to.y), app, world, 1.0f, 0.3f, 0.4f, scale);
							
							MTColor polyCol = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
							person.setFillColor(polyCol);
							person.setStrokeColor(new MTColor(255,0,0));
							
							
							//For an anti-aliased outline
				
							
							
							
							
							
							//person.setVertices(polyVertices);
							getCanvas().addChild(person);
							
							body=person.getBody();
							person.sendToFront();
							//Sale de su estado de sleep
							body.wakeUp();
							
							//body.setXForm(new Vec2(blobevt.getX(),  blobevt.getY()), body.getAngle());
							
							mouseJoint = PhysicsHelper.createDragJoint(world, body, to.x, to.y);
							person.setMJ(mouseJoint);
							
							
							//poly.setVertices(polyVertices);
							//getCanvas().addChild(poly);
							cursores.put(blobevt.getCursor(), person);
							System.out.println(polyVertices);
							
							break;
						case AbstractCursorInputEvt.INPUT_UPDATED:
							person = (Poligono)cursores.get(blobevt.getCursor());
							
							System.out.println("UPDATE"+blobevt.getBlobId());
							
							//MTTextArea temp = (MTTextArea)poly.getChildByIndex(0);
							//temp.setText(""+blobevt.getBlobId());
							
							
							ArrayList<TuioPoint>  temppuntos = blobevt.getPuntos();
							int tempnpts = temppuntos.size();
							polyVertices = new Vertex[tempnpts];
							for(int i = 0 ; i<tempnpts; i ++){
								polyVertices[i]= new Vertex(temppuntos.get(i).xpos, temppuntos.get(i).ypos);
							
							}
							
						
							
							person.setNoStroke(false);
							person.setStrokeColor(new MTColor(255,0,255));
							person.setFillColor(new MTColor(255,255,0));
							//physPoly.setUseDisplayList(true);
							//physPoly.getOutlineContours().clear();
							person.setVertices(polyVertices);
							//physPoly.getOutlineContours().add(polyVertices);
							
							////body=physPoly.getBody();
							//physPoly.sendToFront();
							////Sale de su estado de sleep
							//body.wakeUp();
							
							mouseJoint=person.getMJ();
							//mouseJoint = (MouseJoint) comp.getUserData(comp.getID());
							if (mouseJoint != null){
								
									mouseJoint.setTarget(new Vec2(to.x, to.y));	
						
							}
							
							
							//poly.setVertices(polyVertices);
						
							
					
							break;
						case AbstractCursorInputEvt.INPUT_ENDED:
							person = (Poligono)cursores.get(blobevt.getCursor());
							mouseJoint=person.getMJ();
							
							if(mouseJoint != null ){
								
								person.setMJ(null);
								
							}
							//mouseJoint = (MouseJoint) comp.getU
							
							getCanvas().removeChild(person);
							cursores.remove(blobevt.getCursor());
							person.removeFromParent();
							person.removeAllChildren();
							person.destroy();
							break;
						 default:
							  break;
						
						
						}
						}catch(Exception e ){
							System.err.println(e.getMessage());
							
						}
						
					}
						
				}
				return false;
			}
		});
		
		
	
    
		
		//Set up check for collisions between objects
		//this.addWorldContactListener(world);

		/*
		//Sound
		if (enableSound){
			minim = new Minim(mtApplication);
			wallHit = minim.loadSnippet(MT4jSettings.getInstance().getDataFolderPath() + "sound" + File.separator + "paddleBallHit.wav");
//			paddleBallClash = minim.loadSample(MT4jSettings.getInstance().getDataFolderPath() + "sound" + File.separator + "paddleBallHit.wav", 2048);
//			goalHit = minim.loadSnippet(MT4jSettings.getInstance().getDataFolderPath() + "sound" + File.separator + "goal.wav");
//			goalHit.play();
			paddleHit = minim.loadSnippet(MT4jSettings.getInstance().getDataFolderPath() + "sound" + File.separator + "wallHit.wav");
		}
		*/
	}
	
	
	
public class GameFieldHalfDragListener implements IGestureEventListener{
		
		PImage paddleTex;
		public GameFieldHalfDragListener(){
			 paddleTex = app.loadImage(imagesPath + "paddle.png");
				
			//if (comp.getUserData("box2d") == null){
				//throw new RuntimeException("GameFieldHalfDragListener has to be given a physics object!");
		//	}
		}
		
		public boolean processGestureEvent(MTGestureEvent ge) {
			DragEvent de = (DragEvent)ge;
			
			MTInputEvent iptEvt = (MTInputEvent)ge;
			if(iptEvt instanceof AbstractCursorInputEvt){
				AbstractCursorInputEvt ce = (AbstractCursorInputEvt)iptEvt;
				if(ce instanceof MTBlobInputEvt){
					
					MTBlobInputEvt blobevt = (MTBlobInputEvt)ce;
					System.out.println("es un blobevt!!!");
					
				}
				
				
			}
			
	       
			//PImage paddleTex = mtApplication.loadImage(imagesPath + "paddle.png");
	
			Body	 body = null;
			
			
			System.out.println(ActiveCursorPool.getInstance().toString());
			
			try{
				
				MouseJoint mouseJoint;
				Vector3D to = new Vector3D(de.getTo());
				//Un-scale position from mt4j to box2d
				PhysicsHelper.scaleDown(to, scale);
				switch (de.getId()) {
				case DragEvent.GESTURE_STARTED:
					
					
				
				Paddle	comp = new Paddle(app, de.getTo(), 10, world, 1.0f, 0.3f, 0.4f, scale);
				//comp2=comp;	
				// CTI BG hacemos transparente el paddle
				comp.setTexture(paddleTex);
					comp.setFillColor(new MTColor(50,255,50,50));
					comp.setNoStroke(true);
					comp.setName("red");
					comp.setPickable(false);
					// CTI BG
					//Asoviamos sodeslo un circulo con cada dragcursor 
				    cursores.put(de.getDragCursor(), comp);
					//this.comp = greenCircle;
					physicsContainer.addChild(comp);
				//body=	(Body)comp.getUserData("box2d");
					body=comp.getBody();
					// Hcaemos que este componente se vaya al ulitma de la lista de hijos de su padre para que se dinuje primero 
					comp.sendToFront();
					//Sale de su estado de sleep
					body.wakeUp();
					body.setXForm(new Vec2(to.x,  to.y), body.getAngle());
					mouseJoint = PhysicsHelper.createDragJoint(world, body, to.x, to.y);
					
					comp.setMJ(mouseJoint);
					//comp.setUserData(comp.getID(), mouseJoint);
					break;
				case DragEvent.GESTURE_UPDATED:
					
				comp=(Paddle)cursores.get(de.getDragCursor());
					mouseJoint=comp.getMJ();
					//mouseJoint = (MouseJoint) comp.getUserData(comp.getID());
					if (mouseJoint != null){
						
							mouseJoint.setTarget(new Vec2(to.x, to.y));	
				
					}
			
					break;
				case DragEvent.GESTURE_ENDED:
					comp=(Paddle)cursores.get(de.getDragCursor());
				
					mouseJoint=comp.getMJ();
					//mouseJoint = (MouseJoint) comp.getUserData(comp.getID());
					
					if (mouseJoint != null){
						comp.setMJ(null);
						//comp.setUserData(comp.getID(), null);
					
						physicsContainer.removeChild(comp);
						
						//temp.destroy();
						cursores.remove(de.getDragCursor());
						comp.destroy();
						//System.out.println("tamanio del hash "+cursorToLastDrawnPoint.size());
						//Only destroy the joint if it isnt already (go through joint list and check)
						/*for (Joint joint = world.getJointList(); joint != null; joint = joint.getNext()) {
							JointType type = joint.getType();
							switch (type) {
							case MOUSE_JOINT:
								MouseJoint mj = (MouseJoint)joint;
								if (body.equals(mj.getBody1()) || body.equals(mj.getBody2())){
									if (mj.equals(mouseJoint)) {
										world.destroyJoint(mj);
									}
								}
								break;
							default:
								break;
							}
						}*/
					}
					mouseJoint = null;
					break;
				default:
					break;
				}
			}catch (Exception e) {
				
				System.err.println(e.getMessage());
			}
			return false;
		}
	}
	
	


	
	public class Paddle extends PhysicsCircle{
		MouseJoint mj;
		public Paddle(PApplet applet, Vector3D centerPoint, float radius,
				World world, float density, float friction, float restitution, float worldScale) {
			super(applet, centerPoint, radius, world, density, friction, restitution, worldScale);
		} 
		@Override
		protected void bodyDefB4CreationCallback(BodyDef def) {
			super.bodyDefB4CreationCallback(def);
			def.fixedRotation = true;
			def.linearDamping = 0.5f;
		}
		public MouseJoint getMJ(){
			return this.mj;
			
			
		}
		public void setMJ(MouseJoint m){
			 this.mj=m;
			
			
		}
	
	}
	
	public class HockeyBall extends PhysicsCircle{
		public HockeyBall(PApplet applet, Vector3D centerPoint, float radius,
				World world, float density, float friction, float restitution, float worldScale) {
			super(applet, centerPoint, radius, world, density, friction, restitution, worldScale);
		} 
		
		@Override
		protected void circleDefB4CreationCallback(CircleDef def) {
			super.circleDefB4CreationCallback(def);
			def.radius = def.radius -5/scale;
		}
		@Override
		protected void bodyDefB4CreationCallback(BodyDef def) {
			super.bodyDefB4CreationCallback(def);
//			def.linearDamping = 0.15f;
			def.linearDamping = 0.25f;
			def.isBullet = true;
			def.angularDamping = 0.9f;
			
//			def.fixedRotation = true;
		}
	}
	
	
	private class HockeyGoal extends PhysicsRectangle {
		public HockeyGoal(Vector3D centerPosition, float width, float height,
				PApplet applet, World world, float density, float friction,float restitution, float scale) {
			super(centerPosition, width, height, applet, world, density, friction,restitution, scale);
		}
		
		@Override
		protected void bodyDefB4CreationCallback(BodyDef def) {
			def.isBullet = true;
			super.bodyDefB4CreationCallback(def);
		}

		@Override
		protected void polyDefB4CreationCallback(PolygonDef def) {
			super.polyDefB4CreationCallback(def);
			def.isSensor = true; //THIS AS SENSOR!
		}
	}
	
	
	
	
	private void addWorldContactListener(World world){
		world.setContactListener(new ContactListener() {
			public void result(ContactResult point) {
//				System.out.println("Result contact");
			}
			//@Override
			public void remove(ContactPoint point) {
//				System.out.println("remove contact");
			}
			//@Override
			public void persist(ContactPoint point) {
//				System.out.println("persist contact");
			}
			//@Override
			public void add(ContactPoint point) {
//				/*
				Shape shape1 = point.shape1;
				Shape shape2 = point.shape2;
				final Body body1 = shape1.getBody();
				final Body body2 = shape2.getBody();
				Object userData1 = body1.getUserData();
				Object userData2 = body2.getUserData();
				
				if (userData1 instanceof IPhysicsComponent  && userData2 instanceof IPhysicsComponent) { //Check for ball/star collision
					IPhysicsComponent physObj1 = (IPhysicsComponent) userData1;
					IPhysicsComponent physObj2 = (IPhysicsComponent) userData2;
//					System.out.println("Collided: " + mt4jObj1 + " with " + mt4jObj2);
					if (physObj1 instanceof MTComponent && physObj2 instanceof MTComponent) {
						MTComponent comp1 = (MTComponent) physObj1;
						MTComponent comp2 = (MTComponent) physObj2;

						//Check if one of the components is the BALL
						MTComponent ball = isHit("ball", comp1, comp2);
						final MTComponent theBall = ball;
						
						//Check if one of the components is the GOAL
						MTComponent goal1 = isHit("goal1", comp1, comp2);
						MTComponent goal2 = isHit("goal2", comp1, comp2);
						
						//Check if a puck was involved
						MTComponent bluePuck = isHit("blue", comp1, comp2);
						MTComponent redPuck = isHit("red", comp1, comp2);
						
						//Check if a border was hit
						MTComponent border = null;
						if (comp1.getName() != null && comp1.getName().startsWith("border")){
							border = comp1;
						}else if (comp2.getName() != null && comp2.getName().startsWith("border")){
							border = comp2;
						}
						
						if (ball != null){
							//CHECK IF BALL HIT A PADDLE
							if (enableSound && (bluePuck != null || redPuck != null)){
//								System.out.println("PUCK HIT BALL!");
								/*
								triggerSound(paddleHit);
								*/
							}
							
							
							//Check if BALL HIT A GOAL 
							if (goal1 != null || goal2 != null){
								//BALL HIT A GOAL
								if (goal1 != null){
									System.out.println("GOAL FOR PLAYER 2!");
									scorePlayer2++;
								}else if (goal2 != null){
									System.out.println("GOAL FOR PLAYER 1!");
									scorePlayer1++;
								}
								
								//Update scores
								updateScores();
								//Play goal sound
//								triggerSound(goalHit);
								
								if (scorePlayer1 >= 15 || scorePlayer2 >= 15){
									reset();
								}else{
								
								//Reset ball
								if (theBall.getUserData("resetted") == null){ //To make sure that we call destroy only once
									theBall.setUserData("resetted", true); 
									app.invokeLater(new Runnable() {
										public void run() {
											IPhysicsComponent a = (IPhysicsComponent)theBall;
											a.getBody().setXForm(new Vec2(getMTApplication().width/2f/scale, getMTApplication().height/2f/scale), a.getBody().getAngle());
//											a.getBody().setLinearVelocity(new Vec2(0,0));
											a.getBody().setLinearVelocity(new Vec2(ToolsMath.getRandom(-8, 8),ToolsMath.getRandom(-8, 8)));
											a.getBody().setAngularVelocity(0);
											theBall.setUserData("resetted", null); 
										}
									});
								}
								}
								
							}
							
							//If ball hit border Play sound
							if (enableSound && border != null){
								/*
								triggerSound(wallHit);
								*/
							}
						}
					}
				}else{ //if at lest one if the colliding bodies' userdata is not a physics shape
					
				}
//				*/
			}
		});
	}
	
	/*
	private void triggerSound(AudioSnippet snippet){
		if (!snippet.isPlaying()){
			snippet.pause();
			snippet.rewind();
			snippet.play();
		}
	}
	*/
	
	private MTComponent isHit(String componentName, MTComponent comp1, MTComponent comp2){
		MTComponent hitComp = null;
		if (comp1.getName() != null && comp1.getName().equalsIgnoreCase(componentName)){
			hitComp = comp1;
		}else if (comp2.getName() != null && comp2.getName().equalsIgnoreCase(componentName)){
			hitComp = comp2;
		}
		return hitComp;
	}
	
	private void updateScores(){
		t1.setText(Integer.toString(scorePlayer1));
		t2.setText(Integer.toString(scorePlayer2));
	}
	
	private void reset(){
		if (ball.getUserData("resetted") == null){ //To make sure that we call destroy only once
			ball.setUserData("resetted", true); 
			app.invokeLater(new Runnable() {
				public void run() {
					IPhysicsComponent a = (IPhysicsComponent)ball;
					a.getBody().setXForm(new Vec2(getMTApplication().width/2f/scale, getMTApplication().height/2f/scale), a.getBody().getAngle());
//					a.getBody().setLinearVelocity(new Vec2(0,0));
					a.getBody().setLinearVelocity(new Vec2(ToolsMath.getRandom(-8, 8),ToolsMath.getRandom(-8, 8)));
					a.getBody().setAngularVelocity(0);
					ball.setUserData("resetted", null); 
				}
			});
		}
		this.scorePlayer1 = 0;
		this.scorePlayer2 = 0;
		this.updateScores();
	}
	
	
	private void createScreenBorders(MTComponent parent){
		//Left border 
		float borderWidth = 50f;
		float borderHeight = app.height;
		Vector3D pos = new Vector3D(-(borderWidth/2f) , app.height/2f);
		PhysicsRectangle borderLeft = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderLeft.setName("borderLeft");
		parent.addChild(borderLeft);
		//Right border
		pos = new Vector3D(app.width + (borderWidth/2), app.height/2);
		PhysicsRectangle borderRight = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderRight.setName("borderRight");
		parent.addChild(borderRight);
		//Top border
		borderWidth = app.width;
		borderHeight = 50f;
		pos = new Vector3D(app.width/2, -(borderHeight/2));
		PhysicsRectangle borderTop = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderTop.setName("borderTop");
		parent.addChild(borderTop);
		//Bottom border
		pos = new Vector3D(app.width/2 , app.height + (borderHeight/2));
		PhysicsRectangle borderBottom = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderBottom.setName("borderBottom");
		parent.addChild(borderBottom);
	}
	

	public void onEnter() {
		getMTApplication().registerKeyEvent(this);
	}
	
	public void onLeave() {	
		getMTApplication().unregisterKeyEvent(this);
	}
	
	public void keyEvent(KeyEvent e){
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;
		switch (e.getKeyCode()){
		case KeyEvent.VK_SPACE:
			this.reset();
			break;
			default:
				break;
		}
	}

}
