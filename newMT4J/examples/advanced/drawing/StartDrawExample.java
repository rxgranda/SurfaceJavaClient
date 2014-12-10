package advanced.drawing;

import java.awt.event.KeyEvent;

import org.mt4j.MTApplication;

import advanced.umleditor.SessionLoader;
import advanced.umleditor.UMLDataSaver;

public class StartDrawExample extends MTApplication{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String args[]){
		initialize();
	}
	
	//@Override
	public void startUp(){
		
		this.addScene(new MainDrawingScene(this, "Main drawing scene"));
//		DrawingScene scene = new DrawingScene(this, "scene");
//		scene.setClear(false);
//		this.addScene(scene);
//		this.frameRate(50);
	}
	/*
	 @Override
	    protected void handleKeyEvent(KeyEvent e) {
		 System.out.println("hola");
	        char key = e.getKeyChar();
	        if (e.getID() == KeyEvent.KEY_PRESSED) {
	              if (key =='s') {
	                  UMLDataSaver.guardar();
	              }
	              if (key =='l') {
	                //  System.out.println(SessionLoader.verifySessionJSON("C:/Users/admin/Documents/prueba.scti"));
	              }
	              else{
	                  
	              }
	        }
	    } 
	*/
}



