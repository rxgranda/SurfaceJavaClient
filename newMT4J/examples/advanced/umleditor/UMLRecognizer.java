package advanced.umleditor;

import java.util.Arrays;

import advanced.umleditor.logic.ObjetoUML;
import srl.core.sketch.Point;
import srl.core.sketch.Stroke;
import srl.recognition.IRecognitionResult;
import srl.recognition.paleo.PaleoConfig;
import srl.recognition.paleo.PaleoSketchRecognizer;
//import sun.org.mozilla.javascript.internal.ast.CatchClause;

public class UMLRecognizer {

	private Stroke stroke;
	private PaleoSketchRecognizer recognizer;
	private boolean modo_edicion=true;

	public UMLRecognizer(){		
		PaleoConfig config = new PaleoConfig();

		config = new PaleoConfig(PaleoConfig.Option.Line,PaleoConfig.Option.Polyline,PaleoConfig.Option.Wave, PaleoConfig.Option.Rectangle, PaleoConfig.Option.Diamond);		

		recognizer = new PaleoSketchRecognizer(config);
		stroke = new Stroke();	    		
	}
	public void addPoint(float x,float y){		
		stroke.addPoint(new Point(x,y));
	}

	public void reiniciar(){
		// Para iniciar un nuevo trazo
		stroke = new Stroke();	
		edit_mode();
	}
	public int  recognize(){
		try{
			if(stroke !=null){
				if(stroke.getLength()>3){
					IRecognitionResult result = recognizer.recognize(stroke);	

					//if(result.getBestShape().label.equalsLowerCase("line"))
					if(result.getBestShape() != null&&stroke.getPoints().size()>40&&isEditMode()){
						System.out.println(result.getBestShape().getInterpretation().label);
						String shapeLabel=result.getBestShape().getInterpretation().label;						
						if(shapeLabel.equals("Rectangle"))					
							return ObjetoUML.ENTIDAD;

						else if(shapeLabel.equals("Line")){	
							if(modo_edicion)

								return ObjetoUML.RELACION;
							else
								return ObjetoUML.DELETE_GESTURE;	
						}else if(shapeLabel.contains("Polyline")){
							//String str = new String(shapeLabel);      
							//str = str.replaceAll("[^0-9]+", " ");
							//Arrays.asList(str.trim().split(" "));
							//int numPolyLine=Integer.parseInt(str.trim().split(" ")[0]);
							//if (numPolyLine>3){ // Si es un polyline mayor a 4 para evitar el borrado involuntario					 
								if(modo_edicion)
									return ObjetoUML.RELACION;
								else
									return ObjetoUML.DELETE_GESTURE;								
							//}
							//if(numPolyLine>1){
								//if(modo_edicion)
							//		return ObjetoUML.RELACION;
								//else
									//return ObjetoUML.DELETE_GESTURE;	
							//}
							
							//return ObjetoUML.INVALIDO;
						}else if(shapeLabel.equals("Diamond")){
							//System.out.println("RETORNA RELACION MULTIPLE");	
							if(modo_edicion)
								return ObjetoUML.RELACION_MULTIPLE;
							else
								return ObjetoUML.DELETE_GESTURE;
							
						}else if((shapeLabel.contains("Wave"))){
							if(!isEditMode())
								return ObjetoUML.DELETE_GESTURE;	
							System.out.println("No retorno delete "+isEditMode());
						}
					}
				}
			}
			stroke=new Stroke();	
			if(!isEditMode())
				return ObjetoUML.DELETE_GESTURE;	
			else
				return ObjetoUML.INVALIDO;
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return ObjetoUML.INVALIDO;
		
	}
	
	public void delete_mode(){
		System.out.println("Cambiar a modo borrar");
		modo_edicion=false;
	}
	public void edit_mode(){
		System.out.println("Cambiar a modo edicion");
		modo_edicion=true;
	}
	
	public boolean isEditMode(){
		return modo_edicion;
	}
	
}
