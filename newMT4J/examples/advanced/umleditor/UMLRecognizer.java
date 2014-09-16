package advanced.umleditor;

import advanced.umleditor.logic.ObjetoUML;
import srl.core.sketch.Point;
import srl.core.sketch.Stroke;
import srl.recognition.IRecognitionResult;
import srl.recognition.paleo.PaleoConfig;
import srl.recognition.paleo.PaleoSketchRecognizer;

public class UMLRecognizer {
	
	private Stroke stroke;
	private PaleoSketchRecognizer recognizer;
	
	public UMLRecognizer(){		
		PaleoConfig config = new PaleoConfig();
		config = new PaleoConfig(PaleoConfig.Option.Line,PaleoConfig.Option.Polyline, PaleoConfig.Option.Circle,PaleoConfig.Option.Arrow, PaleoConfig.Option.Rectangle);		
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
		IRecognitionResult result = recognizer.recognize(stroke);	
	    //if(result.getBestShape().label.equalsLowerCase("line"))
		if(result.getBestShape() != null){
			System.out.println(result.getBestShape().getInterpretation().label);
			String shapeLabel=result.getBestShape().getInterpretation().label;						
			if(shapeLabel.equals("Rectangle"))					
				return ObjetoUML.ENTIDAD;
			else if(shapeLabel.equals("Line"))	
				return ObjetoUML.RELACION;
		}
		stroke=new Stroke();
		return ObjetoUML.INVALIDO;
				
	}
}
