package advanced.drawing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.mt4j.MTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.components.visibleComponents.widgets.MTColorPicker;
import org.mt4j.components.visibleComponents.widgets.MTSceneTexture;
import org.mt4j.components.visibleComponents.widgets.MTSlider;
import org.mt4j.components.visibleComponents.widgets.MTTextField;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GLFBO;

import advanced.umleditor.chat.TextoLabel;
import advanced.umleditor.logic.Usuario;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;

import processing.core.PImage;

public class MainDrawingScene extends AbstractScene {
	private MTApplication pa;	
	private MTEllipse pencilBrush;// Dibujar Trazos
	private MTEllipse pencilBrush2; // Borrar trazos
	private DrawSurfaceScene drawingScene;
	private MTRectangle container;	
	private static MTTextField txtUsuarios;
	static MTRectangle login;
	private String imagesPath = "advanced" + MTApplication.separator + "drawing" + MTApplication.separator + "data" + MTApplication.separator + "images" + MTApplication.separator;		
	
	public static final MTColor loginColorDisabled=new MTColor(200,200,200);//new MTColor(45,137,239);
	public static final MTColor loginColor=new MTColor(45,137,239);//new MTColor(2,196,238);
	public static final MTColor backgroundColor=new MTColor(43,87,151);
	/*public static final MTColor loginColor=new MTColor(2,196,238);//new MTColor(45,137,239);
	public static final MTColor backgroundColor=new MTColor(0,171,169);
	 * */

	public static final MTColor blanco=new MTColor(255,255,255);
	public static final MTColor negro=new MTColor(0,0,0);
	
	
	public static SocketIOServer server;
	SocketIONamespace loginListener;
	private static Map<Integer, Usuario> listaUsuarios = new HashMap<Integer, Usuario>();

	
	
	static MTSceneTexture sceneTexture ;
	
		public MainDrawingScene(MTApplication mtApplication, String name) {
		super(mtApplication, name);				
		this.pa = mtApplication;
		if (!(MT4jSettings.getInstance().isOpenGlMode() && GLFBO.isSupported(pa))){
			System.err.println("Drawing example can only be run in OpenGL mode on a gfx card supporting the GL_EXT_framebuffer_object extension!");
			return;
		}
		this.registerGlobalInputProcessor(new CursorTracer(pa, this));

		//PImage image = mtApplication.loadImage(imagesPath + "login2.png"); 
		//final MTBackgroundImage backgroundImage = new MTBackgroundImage(pa, image, false); 
		//this.getCanvas().addChild(backgroundImage);
		final MTRectangle background=new MTRectangle( pa.width, pa.height, pa);
		background.setFillColor(backgroundColor);
		background.setPickable(false);
		this.getCanvas().addChild(background);

		login=new MTRectangle(pa.width/2-100,pa.height/2+100,0, 200, 100, pa);
		login.setFillColor(loginColorDisabled);
		//login.setFillColor(loginColor);
		login.setStrokeColor(blanco);
		login.setEnabled(false);
		login.setNoStroke(true);
		this.getCanvas().addChild(login);

		IFont loginFont=FontManager.getInstance().createFont(pa, "SourceSansPro-Semibold.otf", 40, new MTColor(255,255,255),true);
		IFont txtFont=FontManager.getInstance().createFont(pa, "SourceSansPro-Light.otf", 22, new MTColor(255,255,255),true);

		final MTTextField texto = new MTTextField(pa.width/2-90,pa.height/2+120,200,200,loginFont, pa);
		texto.setText("Start App");
		texto.setFontColor(blanco);
		texto.setPickable(false);
		texto.setNoFill(true);
		texto.setNoStroke(true);
		login.addChild(texto);

		txtUsuarios = new MTTextField(pa.width/2-93,pa.height/2+50,200,200,txtFont, pa);
		txtUsuarios.setText("Usuarios Activos: "+ listaUsuarios.size());
		txtUsuarios.setFontColor(blanco);
		txtUsuarios.setPickable(false);
		txtUsuarios.setNoFill(true);
		txtUsuarios.setNoStroke(true);
		this.getCanvas().addChild(txtUsuarios);

		login.unregisterAllInputProcessors(); //Remove the default drag, rotate and scale gestures first
		login.registerInputProcessor(new TapProcessor(pa));
		login.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent)ge;
				IMTComponent3D target = te.getTargetComponent();
				if (target instanceof MTRectangle) {
					MTRectangle rectangle = (MTRectangle) target;
					switch (te.getTapID()) {
					case TapEvent.BUTTON_DOWN:
						System.out.println("Button down state on " + target);
						rectangle.setFillColor(negro);
						break;
					case TapEvent.BUTTON_UP:
						System.out.println("Button up state on " + target);
						rectangle.setFillColor(loginColor);
						texto.setFontColor(blanco);
						break;
					case TapEvent.BUTTON_CLICKED:
						System.out.println("Button clicked state on " + target);
						rectangle.setFillColor(negro);
						cargarLienzo();								
						getCanvas().removeChild(login);
						getCanvas().removeChild(background);
						break;
					default:
						break;
					}
				}
				return false;
			}
		});

		//new ServerThread("").start();  //Habilitar SoketIOServer
		
		
		//Configuarcion para el socket
		Configuration config = new Configuration();
        config.setHostname("localhost");
        
        config.setPort(3323);	        
        //inizialimos el servidor de socket        
        server = new SocketIOServer(config);	
        
        loginListener = server.addNamespace("/login");	        	        
        loginListener.addEventListener("loginevent", Usuario.class, new DataListener<Usuario>() {
			@Override
			public void onData(SocketIOClient arg0, Usuario arg1,
					AckRequest arg2) throws Exception {					
					System.out.println("recibido:  "+arg1.getIdPluma()+" "+ arg1.getNombres());
					Usuario user=agregarUsuario(arg1);					
					loginListener.getClient(arg0.getSessionId()).sendEvent("confirmevent",user);																		
			}
        });        	
		server.start();				
       
		mtApplication.frame.addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent e){
	        	server.stop();
	        	System.out.println("CLOSING!!!");
	        }
	    });

	}


	public void cargarLienzo(){
		//Create window frame
		MTRoundRectangle frame = new MTRoundRectangle(pa,-0, -0, 0, pa.width+0, pa.height+0,25, 25);
		frame.setSizeXYGlobal(pa.width-10, pa.height-10);
		this.getCanvas().addChild(frame);
		//Container Superficie donde se guardan todas las figuras que se han reconocido
		container = new MTRectangle(0,0,pa.width, pa.height , pa);
		container.setFillColor(new MTColor(255,255,255,255));

		//Create the scene in which we actually draw
		drawingScene = new DrawSurfaceScene(pa, "DrawSurface Scene", container,server,listaUsuarios);        
		drawingScene.setClear(false);

		//Create texture brush
		PImage brushImage = getMTApplication().loadImage(imagesPath + "brush1.png");

		//Create pencil brush
		pencilBrush = new MTEllipse(pa, new Vector3D(brushImage.width/2f,brushImage.height/2f,0), brushImage.width/2f, brushImage.width/2f, 60);
		pencilBrush.setPickable(false);
		pencilBrush.setNoFill(false);
		pencilBrush.setNoStroke(true);
		pencilBrush.setDrawSmooth(true);
		pencilBrush.setStrokeColor(new MTColor(0, 0, 0, 255));
		pencilBrush.setFillColor(new MTColor(255, 255, 255, 255));


		pencilBrush2 = new MTEllipse(pa, new Vector3D(brushImage.width/2f,brushImage.height/2f,0), brushImage.width/2f, brushImage.width/2f, 60);
		pencilBrush2.setPickable(false);
		pencilBrush2.setNoFill(false);
		pencilBrush2.setNoStroke(false);
		pencilBrush2.setDrawSmooth(true);
		pencilBrush2.setStrokeColor(new MTColor(255, 255, 255, 255));
		pencilBrush2.setFillColor(new MTColor(255, 255, 255, 255));
		drawingScene.setBrush2(pencilBrush2);

		//Set texture brush as default
		drawingScene.setBrush(pencilBrush);
		//Create the frame/window that displays the drawing scene through a FBO
		//final MTSceneTexture sceneWindow = new MTSceneTexture(0,0, pa, drawingScene);
		//We have to create a fullscreen fbo in order to save the image uncompressed
		sceneTexture = new MTSceneTexture(pa,0, -0, pa.width+0, pa.height+0, drawingScene);
		sceneTexture.getFbo().clear(true, 255, 255, 255, 0, true);
		sceneTexture.setStrokeColor(new MTColor(155,0,0));

		//Add the scene texture as a child of the background rectangle so the scene texture is drawn in front
		container.addChild(sceneTexture);
		frame.addChild(container);
		 //
	}

	public void onEnter() {

	}

	public void onLeave() {	

	}

	@Override
	public boolean destroy() {
		server.stop(); System.out.println("OUTTTT");
		boolean destroyed = super.destroy();

		if (destroyed){
			if(drawingScene!=null)
				drawingScene.destroy(); //Destroy the scene manually since it isnt destroyed in the MTSceneTexture atm!
		}
		return destroyed;
	}



	public boolean guardar(){
		this.drawingScene.guardar();
		return true;
	}
	public static void  clear(){
		sceneTexture.getFbo().clear(true, 255, 255, 255, 0, true);

	}
	public static  synchronized Usuario  agregarUsuario(Usuario user){
		if(listaUsuarios.containsKey(user.getIdPluma())){
			user=listaUsuarios.get(user.getIdPluma());
			user.setEstado(0);	
			return user;
		}else{
			listaUsuarios.put(user.getIdPluma(), user);
			txtUsuarios.setText("Usuarios Activos: "+ listaUsuarios.size());
			user.setEstado(1);
			user.setCanal("canal"+user.getIdPluma());
			if(listaUsuarios.size()>0){				
				login.setFillColor(loginColor);			
				login.setEnabled(true);
				
			}
			return user;
		}
		
	}
}
