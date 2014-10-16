package advanced.umleditor.chat;
import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.mt4j.MTApplication;
import org.mt4j.components.PickResult;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.MTTextField;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import processing.core.PApplet;
import processing.core.PImage;
import sun.awt.image.PixelConverter.Bgrx;
 
public class SedalScene extends AbstractScene {
	PImage discover_bg_im1,discover_bg_im2,discover_bg_im3;
	PImage aux1_img,aux2_img,aux3_img;
	PImage fg_im1,fg_im2,fg_im3;
	MTEllipse aux ;
	MTEllipse ellipse_paso1,ellipse_paso2,ellipse_paso3;
	public static SocketIOServer server;

	public SedalScene(final MTApplication mtApplication, String name) {
		super(mtApplication, name);
		
		IFont fontArial = FontManager.getInstance().createFont(mtApplication, "arial.ttf", 
				50, 	//Font size
				new MTColor(255,255,255));	//Font color
		//**************************************************************************************88
			//Configuarcion para el socket
			Configuration config = new Configuration();
	        config.setHostname("localhost");
	        config.setPort(3322);

	        
	        //inizialimos el servidor de socket
	        
	        server = new SocketIOServer(config);

	        
	        //AQUI anadimos el listener para que pueda recibir el mensaje desde la aplicacion web
	        // en el mensaje recibimos 2 cosas:
	        //1. el nuevo label
	        //2. el id del objeto al que vamos a cambiar el texto
	        
	        final SocketIONamespace chat1namespace = server.addNamespace("/chat1");
	        final SocketIONamespace chat2namespace = server.addNamespace("/chat2");

	        
	        chat1namespace.addEventListener("settextlabel", TextoLabel.class, new DataListener<TextoLabel>() {

				@Override
				public void onData(SocketIOClient arg0, TextoLabel arg1,
						AckRequest arg2) throws Exception {

					System.out.println(arg0.getSessionId());
						int idobj = arg1.getObjectID();
						MTEllipse target = (MTEllipse)getCanvas().getChildbyID(idobj);
						MTTextField texto =  (MTTextField)target.getChildByIndex(0);
						texto.setText(arg1.getMessage());
					
				}
	        });
	        chat2namespace.addEventListener("settextlabel", TextoLabel.class, new DataListener<TextoLabel>() {

				@Override
				public void onData(SocketIOClient arg0, TextoLabel arg1,
						AckRequest arg2) throws Exception {

					System.out.println(arg0.getSessionId());
						int idobj = arg1.getObjectID();
						MTEllipse target = (MTEllipse)getCanvas().getChildbyID(idobj);
						MTTextField texto =  (MTTextField)target.getChildByIndex(0);
						texto.setText(arg1.getMessage());
					
				}
	        });
			
	        //corremos el servidor de socket
	        server.start();
	       //***********************************************************************************************
	        //try {
				//Thread.sleep(Integer.MAX_VALUE);
			//} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			//}


		//******************************************************************************************
 
		//Disable frustum culling for this scene - optional
		this.getCanvas().setFrustumCulling(false);
		//Set the background color
		this.setClearColor(new MTColor(255, 255, 255, 255));
		String imagesPath = "advanced" + MTApplication.separator + "sedal" + MTApplication.separator + "data" + MTApplication.separator + "images" + MTApplication.separator;
		
		 fg_im1 = getMTApplication().loadImage(imagesPath + "fg_1.png");	
		 discover_bg_im1 = getMTApplication().loadImage(imagesPath + "bg_1.png");
		 aux1_img = getMTApplication().loadImage(imagesPath + "aux_1.png");
		 
		 

		
		ellipse_paso1 = new MTEllipse(mtApplication, new Vector3D((int)(mtApplication.width*0.25),(int)(mtApplication.height- mtApplication.height*0.25),0), 60, 60);
		ellipse_paso2 = new MTEllipse(mtApplication, new Vector3D((int)(mtApplication.width -mtApplication.width*0.25),(int)(mtApplication.height- mtApplication.height*0.50),0), 60, 60);
		ellipse_paso3 = new MTEllipse(mtApplication, new Vector3D((int)(mtApplication.width*0.25),(int)(mtApplication.height*0.25),0), 60, 60);
		
		MTTextField textField1 = new MTTextField(ellipse_paso1.getCenterPointGlobal().x,ellipse_paso1.getCenterPointGlobal().y -10,200,200, fontArial,mtApplication);		
		textField1.setNoStroke(true);
		textField1.setFillColor(new MTColor(255,0,0));
		textField1.setText("Hello World!");
		
		MTTextField textField2 = new MTTextField(ellipse_paso2.getCenterPointGlobal().x,ellipse_paso2.getCenterPointGlobal().y -10,200,200, fontArial,mtApplication); 
		textField2.setNoStroke(true);
		textField2.setFillColor(new MTColor(255,0,0));	
		textField2.setText("Hello World!");
		
		MTTextField textField3 = new MTTextField(ellipse_paso3.getCenterPointGlobal().x,ellipse_paso3.getCenterPointGlobal().y -10,200,200, fontArial,mtApplication); 	
		textField3.setNoStroke(true);
		textField3.setFillColor(new MTColor(255,0,0));
		textField3.setText("Hello World!");
		ellipse_paso1.addChild(textField1); 
		ellipse_paso2.addChild(textField2);
		ellipse_paso3.addChild(textField3);
		 
		ellipse_paso1.setTexture(fg_im1);
		ellipse_paso2.setTexture(fg_im1);
		ellipse_paso3.setTexture(fg_im1);
		
		aux = null; 

		ellipse_paso1.unregisterAllInputProcessors();
		ellipse_paso2.unregisterAllInputProcessors();
		ellipse_paso3.unregisterAllInputProcessors();//Remove the default drag, rotate and scale gestures first

		this.getCanvas().addInputListener(new IMTInputEventListener() {
			public boolean processInputEvent(MTInputEvent inEvt){
				if(inEvt instanceof AbstractCursorInputEvt){
					final AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inEvt;
					final InputCursor m = posEvt.getCursor();
//					System.out.println("PrevPos: " + prevPos);
					IMTComponent3D result = (IMTComponent3D) (getCanvas().getComponentAt((int)m.getPosition().x,(int)m.getPosition().y));
				
						
						

						String objectname = result.getClass().getName();
						System.out.println("NAME : "+ objectname);
						if(objectname.equals("org.mt4j.components.visibleComponents.shapes.MTEllipse")){
							
							MTEllipse target = (MTEllipse)result;
							int ellipseID = target.getID();
							
							if (posEvt.getId() != AbstractCursorInputEvt.INPUT_ENDED){
							
								target.setTexture(discover_bg_im1);
								
								if (posEvt.getId() == AbstractCursorInputEvt.INPUT_STARTED){

									//para comunicarnos con la aplicacion web nevesitamos enviar objetos, por eso 
									//se ha definido la clase Messagesurface que tiene informacion del ID, dle obejto 
									// y el segundo parametro define un estado
									// 1 si esta listo para editar - hace que se habilite el textinput
									// 0 si ya no queremos editar - have que se deshabilite el textinput
									MessageSurface mss = new MessageSurface(ellipseID, 1); 
									//server.getRoomOperations("").s;
									if(ellipse_paso1==result){
										System.out.println("Elipse 1");
										chat1namespace.getBroadcastOperations().sendEvent("readyfortext",mss);
									}else {
										System.out.println("Elipse !=1");
										chat2namespace.getBroadcastOperations().sendEvent("readyfortext",mss);
									}
									
									
								}
				
						}else{
								target.setTexture(fg_im1);
								MessageSurface mss = new MessageSurface(ellipseID, 0);
								if(ellipse_paso1==result){
									System.out.println("Elipse 1");
									chat1namespace.getBroadcastOperations().sendEvent("readyfortext",mss);
								}else {
									System.out.println("Elipse !=1");
									chat2namespace.getBroadcastOperations().sendEvent("readyfortext",mss);
								}
							
						}						
		
					}

				}
				return false;
			}
		});
		
		this.getCanvas().addChild(ellipse_paso1);
		this.getCanvas().addChild(ellipse_paso2);
		this.getCanvas().addChild(ellipse_paso3);
		
		

		
		//ADD CLOSING BEHAVIOR TO THE WINDOW SO WE CAN CLOSE THE THREAD SOCKETIO	


		mtApplication.frame.addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent e){
	        	server.stop();
	        	System.out.println("CLOSING!!!");
	        }
	    });
	
	}
 
	public void init() { }
 
	public void shutDown() {
        
        
		
	}
	

 
}