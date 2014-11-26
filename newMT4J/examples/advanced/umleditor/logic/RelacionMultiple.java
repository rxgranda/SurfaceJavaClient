package advanced.umleditor.logic;

import org.mt4j.util.math.Vector3D;

public class RelacionMultiple extends ObjetoUML {
	
	private ObjetoUML objetoInicio1;
	private ObjetoUML objetoFin1;
	private ObjetoUML objetoInicio2;
	private ObjetoUML objetoFin2;
	private ObjetoUML objetoInicio3;
	private ObjetoUML objetoFin3;
	
	private Vector3D inicio1, inicio2, inicio3, fin, posicion;
	

	public RelacionMultiple(Usuario p) {
		super(p);
		this.setTipo(ObjetoUML.RELACION_MULTIPLE);
		
	}
	
	public void inicializarDimensiones(float inicioX, float inicioY, float finX ,float finY){
		setInicio(new Vector3D(inicioX,inicioY), 0);
		//estoy poniendo un numero de inicializacion incorrecto, para incializar, que no haga nada

	}
	

	private void setInicio(Vector3D vector3d, int vector) {
		switch (vector) {
		case 1:
			this.inicio1 = vector3d;
			break;
		case 2:
			this.inicio2 = vector3d;
		case 3:
			this.inicio3 = vector3d;
		default:
			System.out.println("INGRESO NUMERO DE VECTOR INCORRECTO, CONJUNTO VALIDO [1,2 O 3]");
			break;
		}
		
	}
	
	

}
