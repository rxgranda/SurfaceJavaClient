package advanced.umleditor;

public class UMLDataSaver implements Runnable {
	private long intervaloGuardado;
	private static final String USER_DEFAULT_DIRECTORY = System.getProperty( "user.home" );
	public static final int RAW_ACTION=1;
	public static final int GRAPHICAL_ACTION=2;

	public UMLDataSaver(long intervaloGuardado){
		this.intervaloGuardado=intervaloGuardado;
	}
	
	
	public synchronized void agregarAccion(final int tipoAccion){
		switch (tipoAccion) {
		case RAW_ACTION:
			
			break;
		case GRAPHICAL_ACTION:
			
			break;

		default:
			break;
		}
		
	}
	
	public synchronized void guardar(String directorio){
		
	}

	@Override
	public void run() {
		while(true){
			try {						
				guardar(USER_DEFAULT_DIRECTORY);
				Thread.sleep(this.intervaloGuardado);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error al guardar el archivo");
				e.printStackTrace();
			}
		}
		
	}
}
