package advanced.umleditor;

import srl.core.sketch.*;
import srl.recognition.IRecognitionResult;
import srl.recognition.paleo.PaleoConfig;
import srl.recognition.paleo.PaleoSketchRecognizer;


public class StartUMLEditor {
	
	public static void main(String args[]){
		/* 
	     Create the sketch. This would normally be done by collection points from user interaction
	    */
		
		  /*
	     By default, every shape is enabled when using the plain constructor
	    */
	    PaleoConfig config = new PaleoConfig();

	    /*
	     Or you can pass a list of shape Options to specify which to enable. All other shapes will be disabled.
	    */
	    config = new PaleoConfig(PaleoConfig.Option.Line, PaleoConfig.Option.Circle, PaleoConfig.Option.Polyline);

	    /*
	     Or you can use one of several predefined recognition sets.
	    */
	    config = PaleoConfig.allOn();
	    config = PaleoConfig.basicPrimsOnly();
		
	    Sketch sketch = new Sketch();

	    Stroke stroke = new Stroke();
	    for(int i=0; i<20; i++){
	      stroke.addPoint(new Point(i,i));
	    }

	    sketch.add(stroke); 

	    /*
	     Run basic shape recognition on the first stroke of the sketch (the one we just created)
	     This should result in a best shape label of "Line"
	     
	     
	    */
	    
	    
	    PaleoSketchRecognizer recognizer = new PaleoSketchRecognizer(config);
	    IRecognitionResult result = recognizer.recognize(stroke);

	    //if(result.getBestShape().label.equalsLowerCase("line"))
	    System.out.println(result.getBestShape().getNBestList());
	    System.out.println(result.getBestShape().getInterpretation().label); 
	    //System.out.println("Correctly recognized as a line");
		
	}
	
	
}
