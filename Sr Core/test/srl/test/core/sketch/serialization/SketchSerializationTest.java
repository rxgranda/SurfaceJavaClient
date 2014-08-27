package srl.test.core.sketch.serialization;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openawt.BasicStroke;
import org.openawt.Color;
import org.openawt.svg.Style;

import srl.core.sketch.Point;
import srl.core.sketch.SComponent;
import srl.core.sketch.SContainer;
import srl.core.sketch.Shape;
import srl.core.sketch.Sketch;
import srl.core.sketch.Stroke;


public class SketchSerializationTest {
	Sketch sketch;
	@Before
	public void setUp() {
		sketch = new Sketch();
		Stroke stroke = new Stroke();
		stroke.addPoint(new Point(0,0,1));
		stroke.addPoint(new Point(10,10,2));
		stroke.addPoint(new Point(15,12,3L,.5));
		stroke.setStyle(new Style().setStrokeWidth(3f).setStroke(Color.RED));
		sketch.add(stroke);
		Shape shape = new Shape();
		shape.setLabel("A Circle");
		shape.add(stroke);
		sketch.add(shape);
		sketch.setAttribute("Title", "Sketch 1");
	}
	@After
	public void tearDown() throws Exception {
	}
	
	//@Test
	public void testJSONSerialize() throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		sketch.serializeJSON(out);
		
		System.out.println(out.toString());
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Sketch sketch2 = Sketch.deserializeJSON(in);
		assertTrue("JSON Sketch serialization did not end up equal",sketch2.equalsByContent(sketch));
		
	}
	
	
	@Test
	public void testXMLCycleSerialize() throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		sketch.serializeXML(out);
		
		//System.out.println(out.toString());
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Sketch sketch2 = Sketch.deserializeXML(in);
		assertTrue("XML Cycle Sketch serialization did not end up equal",sketch2.equalsByContent(sketch));
		
	}

	@Test
	public void testXMLFlatSerialize() throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		sketch.serializeFlatXML(out);
		
		System.out.println(out.toString());
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Sketch sketch2 = Sketch.deserializeXML(in);
		assertTrue("XML Cycle Sketch serialization did not end up equal",sketch2.equalsByContent(sketch));
		
	}
	
	@Test
	public void testSketchCanvasSerialize() throws Exception{
		sketch.serializeSVG(new File("test.sketch.svg"));
	}
}
