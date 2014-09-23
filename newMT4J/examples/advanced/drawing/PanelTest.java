package advanced.drawing;

import org.mt4j.MTApplication;
import org.mt4j.sceneManagement.AbstractScene;

public class PanelTest extends AbstractScene  {
	private MainDrawingScene drawingScene;

	public PanelTest(MTApplication mtApplication, String name,MainDrawingScene surf) {
		super(mtApplication, name);
		this.drawingScene=surf;
		// TODO Auto-generated constructor stub
	}

}
