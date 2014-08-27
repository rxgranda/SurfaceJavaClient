package advanced.physics.scenes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
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
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import TUIO.TuioPoint;
import advanced.physics.physicsShapes.IPhysicsComponent;
import advanced.physics.physicsShapes.PhysicsCircle;
import advanced.physics.physicsShapes.PhysicsPolygon;
import advanced.physics.physicsShapes.Poligono;
import advanced.physics.util.PhysicsHelper;
import advanced.physics.util.UpdatePhysicsAction;

import java.util.Collections;
import processing.core.*;
//import processing.opengl.PGraphicsOpenGL;



public class Nuevo extends AbstractScene{
	
	private float timeStep = 1.0f / 60.0f;
	private int constraintIterations = 10;
	private  MTComponent physicsContainer; 
	private float scale = 20;
	private MTApplication app;
	private World world;
	private HashMap< InputCursor, MTComponent> cursores;
	//For fishes animation
	//CTI se redujo el numero de fishes a 15 

	//AbstractMTApplication is an instance of PApplet
	public Nuevo(MTApplication  mtapp){
		super(mtapp,"Coward Fishes");
		
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println("Scene only usable when using the OpenGL renderer! - See settings.txt");
        	return;
        }
		else
			System.out.println("Using OpenGL renderer");		
		
		this.app = mtapp;
		this.cursores = new HashMap<InputCursor, MTComponent>();
		float worldOffset = 10; 
		//Physics world dimensions
		AABB worldAABB = new AABB(new Vec2(-worldOffset, -worldOffset), new Vec2((app.width)/scale + worldOffset, (app.height)/scale + worldOffset));
		Vec2 gravity = new Vec2(0, 0);
		boolean sleep = true;
		this.world = new World(worldAABB, gravity, sleep);
		//Update the positions of the components according the the physics simulation each frame
		this.registerPreDrawAction(new UpdatePhysicsAction(world, timeStep, constraintIterations, scale));
		
		physicsContainer = new MTComponent(app);
		physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		this.getCanvas().addChild(physicsContainer);
	


	}
	
	@Override
	public void init(){
		System.out.println("Starting scene");
		
		//this.size(900, 450, OPENGL);    // publish size
		//this.size(640, 480, OPENGL);      // blog size
	    getMTApplication().smooth();
	    getMTApplication().background(0);
	    getMTApplication().frameRate(25);
	  
  	//this.registerGlobalInputProcessor(new CursorTracer(getMTApplication(), this));
     // getCanvas().registerInputProcessor(new CursorTracer(getMTApplication(), this));
  
		//Scale the physics container. Physics calculations work best when the dimensions are small (about 0.1 - 10 units)
		//So we make the display of the container bigger and add in turn make our physics object smaller
	//	physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		//this.getCanvas().addChild(physicsContainer);
		//this.innerShadow = getMTApplication().loadImage("data/pond.png");
			  
		// load and init skin array images
	    
	    

	    
	  
	  Vertex[] vertices = new Vertex[]{
			  
			  
				new Vertex(5,0,0),
				new Vertex(10,0,0),
				new Vertex(10,50,0),
				new Vertex(60,50,0),
				new Vertex(60,70,0),
	  };
	   
	  
	  
		PersonPolygon persona= new PersonPolygon(app, new Vector3D(300,300),vertices, world, 1.0f, 0.3f, 0.4f, scale);
		persona.setFillColor(new MTColor(255,0,0));
	    physicsContainer.addChild(persona);
	   Body  body=persona.getBody();
	    persona.sendToFront();
		//Sale de su estado de sleep

	    
	    MTPolygon prueba = new MTPolygon(app, vertices);
		prueba.setFillColor(new MTColor(255,255,0));
	    this.getCanvas().addChild(prueba);
	    
	   PhysicsCircle ball = new PhysicsCircle(app, new Vector3D(app.width/2,app.height/2), 20.0f, world, 1.0f, 0.3f, 0.4f, scale);
	   prueba.setFillColor(new MTColor(255,255,0));
	   
	   physicsContainer.addChild(ball);
	    
	    
	   Poligono prueba2 = new Poligono(vertices,  new Vector3D(app.width/2+100,app.height/2), app, world, 1.0f, 0.3f, 0.4f, scale);
	   physicsContainer.addChild(prueba2);
	    
	    
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
							MTPolygon pol = new MTPolygon(app, polyVertices);
							physicsContainer.addChild(pol);
		
			
							
							//poly.setVertices(polyVertices);
							getCanvas().addChild(pol);
							cursores.put(blobevt.getCursor(), pol);
							System.out.println(polyVertices);
							
							break;
						case AbstractCursorInputEvt.INPUT_UPDATED:
							pol = (MTPolygon)cursores.get(blobevt.getCursor());
							
							System.out.println("UPDATE"+blobevt.getBlobId());
							
							//MTTextArea temp = (MTTextArea)poly.getChildByIndex(0);
							//temp.setText(""+blobevt.getBlobId());
							
							
							ArrayList<TuioPoint>  temppuntos = blobevt.getPuntos();
							int tempnpts = temppuntos.size();
							polyVertices = new Vertex[tempnpts];
							for(int i = 0 ; i<tempnpts; i ++){
								polyVertices[i]= new Vertex(temppuntos.get(i).xpos, temppuntos.get(i).ypos);
							
							}
							
							
						
							
						
							
							pol.setNoStroke(false);
							pol.setStrokeColor(new MTColor(255,0,255));
							pol.setFillColor(new MTColor(255,255,0));
							//physPoly.setUseDisplayList(true);
							//physPoly.getOutlineContours().clear();
							pol.setVertices(polyVertices);
							//physPoly.getOutlineContours().add(polyVertices);
							
			
							
					
							break;
						case AbstractCursorInputEvt.INPUT_ENDED:
							pol = (MTPolygon)cursores.get(blobevt.getCursor());
					
					
							//mouseJoint = (MouseJoint) comp.getU
							
							physicsContainer.removeChild(pol);
							cursores.remove(blobevt.getCursor());
							pol.removeFromParent();
							pol.removeAllChildren();
							pol.destroy();
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
		
		
	}
    
	
	

	@Override
	//Calling each time, similar to draw() in Processing
	public void drawAndUpdate(PGraphics graphics, long timeDelta) {
		super.drawAndUpdate(graphics, timeDelta);
		//getMTApplication().background(0);
		//getMTApplication().image(this.rocks, 0, 0);

		
	}
	
	
	// increments number of koi by 1
	

	
	@Override
	public void shutDown(){
		
	}
	
	  public class PersonPolygon extends MTPolygon implements IPhysicsComponent{
		
			private float angle;
			private World world;
			private Body body;
			private float density;
			private float friction;
			private float restituion;
			
			
		public PersonPolygon(MTApplication aplication, Vector3D centro, Vertex[] vertices, World world, float density, float friction, float restitution, float worldScale){
			super(aplication, vertices);
			this.angle = 0;
			this.world = world;
			this.density = density;
			this.friction = friction;
			this.restituion = restitution;



			PolygonDef pd = new PolygonDef();
	    	if (density != 0.0f){
	    		pd.density 		= density;
	    		pd.friction 	= friction;
	    		pd.restitution 	= restituion;
			}
	    	
	    	
			Vertex.scaleVectorArray(vertices, Vector3D.ZERO_VECTOR, 1f/worldScale, 1f/worldScale, 1);
			centro.scaleLocal(1f/worldScale);
			
	    	
	    	
			//Create polygon body
			BodyDef dymBodyDef = new BodyDef();
//			dymBodyDef.position = new Vec2(position.x /worldScale, position.y /worldScale);
			dymBodyDef.position = new Vec2(centro.x , centro.y );
			this.bodyDefB4CreationCallback(dymBodyDef);
			this.body = world.createBody(dymBodyDef);
			this.setVertices(vertices);
			


		}

		private void polyDefB4CreationCallback(PolygonDef polyDef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Body getBody() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setCenterRotation(float angle) {
			// TODO Auto-generated method stub
			
		}
		
		protected void bodyDefB4CreationCallback(BodyDef def){
			
		}
		//@Override
		protected void destroyComponent() {
			Object o = this.getUserData("box2d");
			if (o != null && o instanceof Body){ 
				Body box2dBody = (Body)o;
				boolean exists = false;
				for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
					if (body.equals(this.body))
						exists = true;//Delete later to avoid concurrent modification
				}
				if (exists)
					box2dBody.getWorld().destroyBody(box2dBody);
			}
			super.destroyComponent();
		}
		
		
		
		
}

	

}

