package advanced.umleditor;

import java.util.Arrays;

import advanced.umleditor.logic.ObjetoUML;
import srl.core.sketch.Point;
import srl.core.sketch.Stroke;
import srl.recognition.IRecognitionResult;
import srl.recognition.paleo.PaleoConfig;
import srl.recognition.paleo.PaleoSketchRecognizer;
import sun.org.mozilla.javascript.internal.ast.CatchClause;

public class UMLRecognizer {

	private Stroke stroke;
	private PaleoSketchRecognizer recognizer;

	public UMLRecognizer(){		
		PaleoConfig config = new PaleoConfig();
		config = new PaleoConfig(PaleoConfig.Option.Line,PaleoConfig.Option.Polyline, PaleoConfig.Option.Circle,PaleoConfig.Option.Wave, PaleoConfig.Option.Rectangle);		
		recognizer = new PaleoSketchRecognizer(config);
		stroke = new Stroke();	    		
	}
	public void addPoint(float x,float y){		
		stroke.addPoint(new Point(x,y));
	}

	public void reiniciar(){
		// Para iniciar un nuevo trazo
		stroke = new Stroke();	
	}
	public int  recognize(){
		try{
			if(stroke !=null){
				if(stroke.getLength()>3){
					IRecognitionResult result = recognizer.recognize(stroke);	

					//if(result.getBestShape().label.equalsLowerCase("line"))
					if(result.getBestShape() != null&&stroke.getPoints().size()>40){
						System.out.println(result.getBestShape().getInterpretation().label);
						String shapeLabel=result.getBestShape().getInterpretation().label;						
						if(shapeLabel.equals("Rectangle"))					
							return ObjetoUML.ENTIDAD;
						else if(shapeLabel.equals("Line"))	
							return ObjetoUML.RELACION;
						else if(shapeLabel.contains("Polyline")){
							String str = new String(shapeLabel);      
							str = str.replaceAll("[^0-9]+", " ");
							//Arrays.asList(str.trim().split(" "));
							if (Integer.parseInt(str.trim().split(" ")[0])>4) // Si es un polyline mayor a 4 para evitar el borrado involuntario					 
								return ObjetoUML.DELETE_GESTURE;
							return ObjetoUML.INVALIDO;
						}else if((shapeLabel.contains("Wave"))){
							return ObjetoUML.DELETE_GESTURE;
						}
					}
				}
			}
			stroke=new Stroke();
			return ObjetoUML.INVALIDO;
		}catch (Exception e){
			e.printStackTrace();
		}
		return ObjetoUML.INVALIDO;	
	}
}
