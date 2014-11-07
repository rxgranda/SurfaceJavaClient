package advanced.umleditor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONValue;
import org.mt4j.util.MT4jSettings;

import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.Usuario;

public class UMLDataSaver implements Runnable {
	
	public static final int AGREGAR_OBJETO_ACTION=1;
	public static final int EDITAR_OBJETO_ACTION=2;
	public static final int BORRAR_OBJETO_ACTION=3;
	public static final int MOVER_OBJETO_ACTION=4;
	public static final int RESIZE_OBJETO_ACTION=5;
	
	public static final String USER_KEYWORD="usuario";
	public static final String AGREGAR_OBJETO_KEYWORD="objeto";
	private static int ACCION_INDEX=0;

	private static  String USER_BACKUP_DIRECTORY;
	public static final int RAW_ACTION=1;
	public static final int GRAPHICAL_ACTION=2;
	
	private static Map jsonMap;	
	private static Map metadata;	
	private static ArrayList datos;

	
	public UMLDataSaver(Map<Integer, Usuario> listaUsuarios, int appWidth, int appHeigth){
		USER_BACKUP_DIRECTORY=MT4jSettings.directorioBackup ;
		jsonMap=new LinkedHashMap();	
		metadata=new LinkedHashMap();	
		datos=new ArrayList();	
		
		ArrayList usuariosMapList=new ArrayList();
		Set<Integer> keys=listaUsuarios.keySet();
		Map defaultUser=new LinkedHashMap();

		
		jsonMap.put("metadata",metadata);		
		metadata.put("usuarios", usuariosMapList);
		metadata.put("display-width", appWidth);
		metadata.put("display-heigth", appHeigth);
		jsonMap.put("datos",datos);							
					
		for (Integer key:keys){
			Usuario user=listaUsuarios.get(key);
			Map usuarioMap=new LinkedHashMap();
			Map usuarioContainer=new LinkedHashMap();
			usuarioMap.put("id", user.getIdPluma());
			usuarioMap.put("id_pluma", user.getIdPluma());
			usuarioMap.put("nombres", user.getNombres());
			usuarioMap.put("canal_SocketIO", user.getCanal());
			usuarioContainer.put(USER_KEYWORD, usuarioMap);
			usuariosMapList.add( usuarioContainer);
		}
		defaultUser.put("id",new Integer(Usuario.ID_DEFAULT_USER));
		defaultUser.put("id_pluma", Usuario.ID_DEFAULT_USER);
		defaultUser.put("nombres", Usuario.NOMBRE_DEFAULT_USER);
		defaultUser.put("canal_SocketIO", Usuario.CANAL_DEFAULT_USER);
		usuariosMapList.add( defaultUser);
						
		
	}
	
	
	public static synchronized void agregarAccion(final int tipoAccion, ObjetoUML objeto){
		switch (tipoAccion) {
		case AGREGAR_OBJETO_ACTION:
			LinkedHashMap objetoContainer=new LinkedHashMap();
			LinkedHashMap objetoMap=new LinkedHashMap();
			switch (objeto.getTipo()) {
			case ObjetoUML.ENTIDAD:
				objetoMap.put("tipo", "Entidad");
				if(objeto instanceof Entidad){
					LinkedHashMap mapaAtributos=new LinkedHashMap();
					objetoMap.put("nombre", ((Entidad)objeto).getNombre());
					int index=0;
					for(String atributo:((Entidad)objeto).getAtributos()){
						mapaAtributos.put(index, atributo);
						index++;
					}
					
					objetoMap.put("atributos",mapaAtributos );
					
				}
				break;
			
			case ObjetoUML.RELACION:
				objetoMap.put("tipo", "Relacion");
				if(objeto instanceof Relacion){
					Relacion relacion=((Relacion)objeto);
					String cardinalidadInicio="",cardinalidadFin="";
					
					switch (relacion.getCardinalidadInicio()) {
					case Relacion.CARDINALIDAD_CERO_MUCHOS:
						cardinalidadInicio="CARDINALIDAD_CERO_MUCHOS";
						break;
					case Relacion.CARDINALIDAD_CERO_UNO:
						cardinalidadInicio="CARDINALIDAD_CERO_UNO";
						break;
					case Relacion.CARDINALIDAD_MUCHOS:
						cardinalidadInicio="CARDINALIDAD_MUCHOS";					
						break;
					case Relacion.CARDINALIDAD_UNO:
						cardinalidadInicio="CARDINALIDAD_UNO";
						break;
					case Relacion.CARDINALIDAD_UNO_MUCHOS:
						cardinalidadInicio="CARDINALIDAD_UNO_MUCHOS";
						break;					
					default:
						break;
					}
					switch (relacion.getCardinalidadFin()) {
					case Relacion.CARDINALIDAD_CERO_MUCHOS:
						cardinalidadFin="CARDINALIDAD_CERO_MUCHOS";
						break;
					case Relacion.CARDINALIDAD_CERO_UNO:
						cardinalidadFin="CARDINALIDAD_CERO_UNO";
						break;
					case Relacion.CARDINALIDAD_MUCHOS:
						cardinalidadFin="CARDINALIDAD_MUCHOS";					
						break;
					case Relacion.CARDINALIDAD_UNO:
						cardinalidadFin="CARDINALIDAD_UNO";
						break;
					case Relacion.CARDINALIDAD_UNO_MUCHOS:
						cardinalidadFin="CARDINALIDAD_UNO_MUCHOS";
						break;					
					default:
						break;
					}
					objetoMap.put("cardinalidadInicio", cardinalidadInicio);
					objetoMap.put("cardinalidadFIN", cardinalidadFin);
					objetoMap.put("posicionInicio", relacion.getInicio());
					objetoMap.put("posicionFin", relacion.getFin());
					objetoMap.put("objetoInicio", relacion.getObjetoInicio().getId());
					objetoMap.put("objetoFin", relacion.getObjetoFin().getId());								
				}																
				break;
			default:
				break;
			}
			
				
			objetoMap.put("id", objeto.getId());
			objetoMap.put("timestamp", objeto.getTiempoCreacion());
			objetoMap.put("width", objeto.getWidth());
			objetoMap.put("height", objeto.getHeight());
			objetoMap.put("posicion", objeto.getPosicion());
			objetoContainer.put("objeto", objetoMap);		
			datos.add(objetoContainer);
			
			break;
		case EDITAR_OBJETO_ACTION:
			
			break;

		default:
			break;
		}
		
		
		String jsonText = JSONValue.toJSONString(jsonMap);
		System.out.print(jsonText);
		int a=0;
	}
	
	public static synchronized void guardar(String directorio){
		
	}

	@Override
	public void run() {
		while(true){
			try {						
				guardar(USER_BACKUP_DIRECTORY);
				Thread.sleep(MT4jSettings.tiempoBackup);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error al guardar el archivo");
				e.printStackTrace();
			}
		}
		
	}
}
