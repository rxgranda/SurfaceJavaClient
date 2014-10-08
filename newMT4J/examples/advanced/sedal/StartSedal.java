package advanced.sedal;


import org.mt4j.MTApplication;

public class StartSedal extends MTApplication {

	public static void main(String[] args) {
		initialize();

		
	}
 
	@Override
	public void startUp() {
		addScene(new SedalScene(this, "Hello World Scene"));
	}
 
}	