package advanced.physics;

import org.mt4j.MTApplication;

import advanced.physics.scenes.Nuevo;
import advanced.physics.scenes.PhysicsScene;

public class StartNuevo extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize();
	}
	
	@Override
	public void startUp() {
		addScene(new Nuevo(this));
	}

}
