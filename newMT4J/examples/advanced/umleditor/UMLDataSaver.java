package advanced.umleditor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mt4j.MTApplication;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.Vector3D;

import advanced.drawing.MainDrawingScene;
import advanced.umleditor.impl.HaloHelper;
import advanced.umleditor.impl.ObjetoUMLGraph;
import advanced.umleditor.impl.Relacion_Impl;
import advanced.umleditor.logic.Entidad;
import advanced.umleditor.logic.ObjetoUML;
import advanced.umleditor.logic.Relacion;
import advanced.umleditor.logic.Usuario;

public class UMLDataSaver implements Runnable {
	
	public static final int AGREGAR_OBJETO_ACTION=1;
	public static final int EDITAR_OBJETO_ACTION=2;
	public static final int BORRAR_OBJETO_ACTION=3;
	public static final int MOVER_OBJETO_ACTION=4;
	
	public static final String USER_KEYWORD="usuario";
	public static final String AGREGAR_OBJETO_KEYWORD="objeto";
	private static int ACCION_INDEX=0;

	private static  String USER_BACKUP_DIRECTORY;
	public static final int RAW_ACTION=1;
	public static final int GRAPHICAL_ACTION=2;
	
	private static Map jsonMap;	
	public static String DEFAULT_FILE_NAME_BACKUP="";
	public static String DEFAULT_FILE_EXTENSION_BACKUP=".scti";
	private static Map metadata;	
	private static ArrayList datos;
	


	
	public UMLDataSaver(Map<Integer, Usuario> listaUsuarios, int appWidth, int appHeigth){
		USER_BACKUP_DIRECTORY=MT4jSettings.directorioBackup ;		
		Calendar cal = Calendar.getInstance();    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
    	
		jsonMap=new LinkedHashMap();	
		metadata=new LinkedHashMap();	
		datos=new ArrayList();	
		
		ArrayList usuariosMapList=new ArrayList();
		Set<Integer> keys=listaUsuarios.keySet();
		Map defaultUser=new LinkedHashMap();
		//Map defaultUserContainer=new LinkedHashMap();


		
		jsonMap.put("metadata",metadata);		
		metadata.put("usuarios", usuariosMapList);
		metadata.put("display-width", appWidth);
		metadata.put("display-heigth", appHeigth);
		jsonMap.put("datos",datos);							
					
		for (Integer key:keys){
			Usuario user=listaUsuarios.get(key);
			Map usuarioMap=new LinkedHashMap();
		//	Map usuarioContainer=new LinkedHashMap();
			usuarioMap.put("id", user.getIdPluma());
			usuarioMap.put("id_pluma", user.getIdPluma());
			usuarioMap.put("nombres", user.getNombres());
			usuarioMap.put("canal_SocketIO", user.getCanal());
			//usuarioContainer.put(USER_KEYWORD, usuarioMap);
			usuariosMapList.add( usuarioMap);
			DEFAULT_FILE_NAME_BACKUP+=user.getNombres().trim()+"_";
		}
		DEFAULT_FILE_NAME_BACKUP+=sdf.format(cal.getTime())+DEFAULT_FILE_EXTENSION_BACKUP;
		defaultUser.put("id",new Integer(Usuario.ID_DEFAULT_USER));
		defaultUser.put("id_pluma", Usuario.ID_DEFAULT_USER);
		defaultUser.put("nombres", Usuario.NOMBRE_DEFAULT_USER);
		defaultUser.put("canal_SocketIO", Usuario.CANAL_DEFAULT_USER);
		//defaultUserContainer.put(USER_KEYWORD, defaultUser);
		usuariosMapList.add( defaultUser);
						
		
	}
	
	
	public static synchronized void agregarAccion(final int tipoAccion, ObjetoUML objeto, Usuario user){
		Calendar cal = Calendar.getInstance();
		
		JSONArray coordenadasPosicion,coordenadasInicio, coordenadasFin, atributos;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String time=sdf.format(cal.getTime());
    	LinkedHashMap objetoContainer=new LinkedHashMap();
		LinkedHashMap objetoMap=new LinkedHashMap();
		
		
		switch (tipoAccion) {
		case AGREGAR_OBJETO_ACTION:	
			
			objetoMap.put("id", objeto.getId());
			switch (objeto.getTipo()) {
			case ObjetoUML.ENTIDAD:
				user.agregarCreacionEntidades();
				objetoMap.put("tipo", "Entidad");
				if(objeto instanceof Entidad){
					LinkedHashMap mapaAtributos=new LinkedHashMap();
					objetoMap.put("nombre", ((Entidad)objeto).getNombre());
					atributos = new JSONArray();
					int index=0;
					for(String atributo:((Entidad)objeto).getAtributos()){
						JSONObject atributoJSON =new JSONObject();
						atributoJSON.put("indice", index);
						atributoJSON.put("nombre_atributo",atributo);
						atributos.add(atributoJSON);
						index++;
					}
					
					objetoMap.put("atributos",atributos );
					
				}
				coordenadasPosicion = new JSONArray();
				coordenadasPosicion.add(objeto.getPosicion().x);
				coordenadasPosicion.add(objeto.getPosicion().y);
				coordenadasPosicion.add(objeto.getPosicion().z);
				coordenadasPosicion.add(objeto.getPosicion().w);
				objetoMap.put("posicion", coordenadasPosicion);
				
				break;
			
			case ObjetoUML.RELACION:
				user.agregarCreacionRelacion();
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
					objetoMap.put("cardinalidadFin", cardinalidadFin);
					
					coordenadasInicio = new JSONArray();
					coordenadasInicio.add(relacion.getInicio().x);
					coordenadasInicio.add(relacion.getInicio().y);
					coordenadasInicio.add(relacion.getInicio().z);
					coordenadasInicio.add(relacion.getInicio().w);

					coordenadasFin = new JSONArray();
					coordenadasFin.add(relacion.getFin().x);
					coordenadasFin.add(relacion.getFin().y);
					coordenadasFin.add(relacion.getFin().z);
					coordenadasFin.add(relacion.getFin().w);

					
					objetoMap.put("posicionInicio", coordenadasInicio);
					objetoMap.put("posicionFin", coordenadasFin);
					
					
					objetoMap.put("objetoInicio", relacion.getObjetoInicio().getId());
					objetoMap.put("objetoFin", relacion.getObjetoFin().getId());
					objetoMap.put("textoInicio", relacion.getLabelTextoInicio());
					objetoMap.put("textoFin",  relacion.getLabelTextoFin());
				}																
				break;
			default:
				break;
			}
			
				
			
			//objetoMap.put("creacion", objeto.getTiempoCreacion());
			objetoMap.put("width", objeto.getWidth());
			objetoMap.put("height", objeto.getHeight());
			objetoMap.put("visible", objeto.isVisible());


			//objetoContainer.put("EDITAR_OBJETO", objetoMap);
			
			objetoContainer.put("accion", "CREAR_OBJETO");
			objetoContainer.put("propiedades", objetoMap);
			
			
			break;
		case EDITAR_OBJETO_ACTION:
			
			objetoMap.put("id", objeto.getId());
			switch (objeto.getTipo()) {
			case ObjetoUML.ENTIDAD:
				user.agregarEdicionEntidades();
				objetoMap.put("tipo", "Entidad");
				if(objeto instanceof Entidad){
					LinkedHashMap mapaAtributos=new LinkedHashMap();
					objetoMap.put("nombre", ((Entidad)objeto).getNombre());
					atributos = new JSONArray();
					int index=0;
					for(String atributo:((Entidad)objeto).getAtributos()){
						JSONObject atributoJSON =new JSONObject();
						atributoJSON.put("indice", index);
						atributoJSON.put("nombre_atributo",atributo);
						atributos.add(atributoJSON);
						index++;
					}
					
					objetoMap.put("atributos",atributos );				
				}
				coordenadasPosicion = new JSONArray();
				coordenadasPosicion.add(objeto.getPosicion().x);
				coordenadasPosicion.add(objeto.getPosicion().y);
				coordenadasPosicion.add(objeto.getPosicion().z);
				coordenadasPosicion.add(objeto.getPosicion().w);
				objetoMap.put("posicion", coordenadasPosicion);
				break;
			case ObjetoUML.RELACION:
				user.agregarEdicionRelacion();
				objetoMap.put("tipo", "Relacion");
				if(objeto instanceof Relacion){
					Relacion relacion=((Relacion)objeto);
					String cardinalidadInicio="",cardinalidadFin="";
				//	System.out.println("Cardinalidad Inicio"+ relacion.getCardinalidadInicio() + " Cardinalidadad Fin:" + relacion.getCardinalidadInicio());
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
					
					coordenadasInicio = new JSONArray();
					
					coordenadasInicio.add(relacion.getInicio().x);
					coordenadasInicio.add(relacion.getInicio().y);
					coordenadasInicio.add(relacion.getInicio().z);
					coordenadasInicio.add(relacion.getInicio().w);

					coordenadasFin = new JSONArray();
					
					coordenadasFin.add(relacion.getFin().x);
					coordenadasFin.add(relacion.getFin().y);
					coordenadasFin.add(relacion.getFin().z);
					coordenadasFin.add(relacion.getFin().w);

					
					objetoMap.put("posicionInicio", coordenadasInicio);
					objetoMap.put("posicionFin", coordenadasFin);
					
					objetoMap.put("objetoInicio", relacion.getObjetoInicio().getId());
					objetoMap.put("objetoFin", relacion.getObjetoFin().getId());
					objetoMap.put("textoInicio", relacion.getLabelTextoInicio());
					objetoMap.put("textoFin",  relacion.getLabelTextoFin());
						
					
					
					
				}


			
				break;
			default:
				
				
				break;
			}
		objetoMap.put("width", objeto.getWidth());
		objetoMap.put("height", objeto.getHeight());
		objetoMap.put("visible", objeto.isVisible());
		objetoContainer.put("accion", "EDITAR_OBJETO");		
		objetoContainer.put("propiedades", objetoMap);
		
		break;
		case BORRAR_OBJETO_ACTION:
			
		//	System.out.println("GUARDANDO BORRADO");
			objetoMap.put("id", objeto.getId());
			
			switch (objeto.getTipo()) {
			
			case ObjetoUML.ENTIDAD:
				user.agregarElimininacionEntidades();
				objetoMap.put("tipo", "Entidad");
				if(objeto instanceof Entidad){
					LinkedHashMap mapaAtributos=new LinkedHashMap();
					objetoMap.put("nombre", ((Entidad)objeto).getNombre());
					atributos = new JSONArray();
					int index=0;
					for(String atributo:((Entidad)objeto).getAtributos()){
						JSONObject atributoJSON =new JSONObject();
						atributoJSON.put("indice", index);
						atributoJSON.put("nombre_atributo",atributo);
						atributos.add(atributoJSON);
						index++;
					}
					
					objetoMap.put("atributos",atributos );
					coordenadasPosicion = new JSONArray();
					coordenadasPosicion.add(objeto.getPosicion().x);
					coordenadasPosicion.add(objeto.getPosicion().y);
					coordenadasPosicion.add(objeto.getPosicion().z);
					coordenadasPosicion.add(objeto.getPosicion().w);
					objetoMap.put("posicion", coordenadasPosicion);
					
					
				}

				
				
				
				break;
			case ObjetoUML.RELACION:
				user.agregarEliminacionRelacion();
				objetoMap.put("tipo", "Relacion");
				if(objeto instanceof Relacion){
					Relacion relacion=((Relacion)objeto);
					String cardinalidadInicio="",cardinalidadFin="";
				//	System.out.println("Cardinalidad Inicio"+ relacion.getCardinalidadInicio() + " Cardinalidadad Fin:" + relacion.getCardinalidadInicio());
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
					
					coordenadasInicio = new JSONArray();
					coordenadasInicio.add(relacion.getInicio().x);
					coordenadasInicio.add(relacion.getInicio().y);
					coordenadasInicio.add(relacion.getInicio().z);
					coordenadasInicio.add(relacion.getInicio().w);

					coordenadasFin = new JSONArray();
					coordenadasFin.add(relacion.getFin().x);
					coordenadasFin.add(relacion.getFin().y);
					coordenadasFin.add(relacion.getFin().z);
					coordenadasFin.add(relacion.getFin().w);

					
					objetoMap.put("posicionInicio", coordenadasInicio);
					objetoMap.put("posicionFin", coordenadasFin);
					
					objetoMap.put("objetoInicio", relacion.getObjetoInicio().getId());
					objetoMap.put("objetoFin", relacion.getObjetoFin().getId());
					objetoMap.put("textoInicio", relacion.getLabelTextoInicio());
					objetoMap.put("textoFin",  relacion.getLabelTextoFin());
						
					
					
					
				}
			
							
				break;
			default:
				break;
				
			}
			objetoMap.put("width", objeto.getWidth());
			objetoMap.put("height", objeto.getHeight());
			objetoMap.put("visible", objeto.isVisible());
			objetoContainer.put("accion", "BORRAR_OBJETO");
			objetoContainer.put("propiedades", objetoMap);
				
			break;
		case MOVER_OBJETO_ACTION:
			objetoMap.put("id", objeto.getId());
			switch (objeto.getTipo()) {
			
			case ObjetoUML.ENTIDAD:
				
				objetoMap.put("tipo", "Entidad");
				if(objeto instanceof Entidad){					
					coordenadasPosicion = new JSONArray();
					coordenadasPosicion.add(objeto.getPosicion().x);
					coordenadasPosicion.add(objeto.getPosicion().y);
					coordenadasPosicion.add(objeto.getPosicion().z);
					coordenadasPosicion.add(objeto.getPosicion().w);
					objetoMap.put("posicion", coordenadasPosicion);		
					
					//objeto.getFigura().obtenerDatos(ObjetoUMLGraph.RELACIONES_INICIO_KEYWORD);
					JSONArray listaRelaciones = new JSONArray();
					
					LinkedList listaInicio=objeto.getFigura().obtenerDatos(ObjetoUMLGraph.RELACIONES_INICIO_KEYWORD);
					if(listaInicio!=null){
						for(Object o:listaInicio){
							if(o instanceof ObjetoUMLGraph){
								//((Relacion)objeto)
								Relacion objeto_relacion=(Relacion) ((Relacion_Impl)o).getObjetoUML();
								//objeto_relacion.setPosicion(objeto_relacion.getPosicion().getAdded(de.getFrom().getSubtracted(de.getTo())));
								LinkedHashMap relacion=new LinkedHashMap();
								relacion.put("id",objeto_relacion.getId());
								JSONArray coordenadasInicioRel = new JSONArray();
								JSONArray coordenadasFinRel = new JSONArray();
								
								coordenadasInicioRel.add(objeto_relacion.getInicio().x);
								coordenadasInicioRel.add(objeto_relacion.getInicio().y);
								coordenadasInicioRel.add(objeto_relacion.getInicio().z);
								coordenadasInicioRel.add(objeto_relacion.getInicio().w);
								relacion.put("posicionInicio", coordenadasInicioRel);		
								
								
								
								coordenadasFinRel.add(objeto_relacion.getFin().x);
								coordenadasFinRel.add(objeto_relacion.getFin().y);
								coordenadasFinRel.add(objeto_relacion.getFin().z);
								coordenadasFinRel.add(objeto_relacion.getFin().w);
								
								relacion.put("posicionFin", coordenadasFinRel);
								listaRelaciones.add(relacion);
								
							}	
						}
					}
	
					LinkedList listaFin=objeto.getFigura().obtenerDatos(ObjetoUMLGraph.RELACIONES_FIN_KEYWORD);
					if(listaFin!=null){
						for(Object o:listaFin){
							if(o instanceof ObjetoUMLGraph){
								//((Relacion)objeto)
								Relacion objeto_relacion=(Relacion) ((Relacion_Impl)o).getObjetoUML();
								//objeto_relacion.setPosicion(objeto_relacion.getPosicion().getAdded(de.getFrom().getSubtracted(de.getTo())));
								LinkedHashMap relacion=new LinkedHashMap();
								relacion.put("id",objeto_relacion.getId());
								JSONArray coordenadasInicioRel = new JSONArray();
								JSONArray coordenadasFinRel = new JSONArray();
								
								coordenadasInicioRel.add(objeto_relacion.getInicio().x);
								coordenadasInicioRel.add(objeto_relacion.getInicio().y);
								coordenadasInicioRel.add(objeto_relacion.getInicio().z);
								coordenadasInicioRel.add(objeto_relacion.getInicio().w);
								relacion.put("posicionInicio", coordenadasInicioRel);		
								
								
								
								coordenadasFinRel.add(objeto_relacion.getFin().x);
								coordenadasFinRel.add(objeto_relacion.getFin().y);
								coordenadasFinRel.add(objeto_relacion.getFin().z);
								coordenadasFinRel.add(objeto_relacion.getFin().w);
								
								relacion.put("posicionFin", coordenadasFinRel);
								listaRelaciones.add(relacion);								
							}	
						}
					}
					objetoMap.put("relaciones",listaRelaciones );
					
				}												
				break;
				default:
					break;
			}
			
			
			objetoMap.put("width", objeto.getWidth());
			objetoMap.put("height", objeto.getHeight());
			objetoMap.put("visible", objeto.isVisible());
			objetoContainer.put("accion", "MOVER_OBJETO");
			objetoContainer.put("propiedades", objetoMap);
			
			break;
		default:
			break;
		}
		
			
			objetoContainer.put("tiempo", time);
			objetoContainer.put("usuario", user.getIdPluma());			
			datos.add(objetoContainer);
		String jsonText = JSONValue.toJSONString(jsonMap);
		System.out.println(jsonText);
		int a=0;
	}
	
	public static  void escribirArchivo(String nombreArchivo){
		System.out.println("Escribiendo "+ nombreArchivo);
		PrintWriter writer;
		try {
			writer = new PrintWriter(nombreArchivo, "UTF-8");
			String copy=new String (JSONValue.toJSONString(jsonMap));
			writer.write(copy);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Map getJsonMap() {
		return jsonMap;
	}


	public static void setJsonMap(Map jsonMap) {
		UMLDataSaver.jsonMap = jsonMap;
	}
	
	public static boolean guardar(){
		System.out.println("Iniciar Guardado....");
		JFrame parentFrame = new JFrame();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");
		
		int userSelection = fileChooser.showSaveDialog(parentFrame);
	//BRUNO
	if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			System.out.println("Save as file: " + fileToSave.getAbsolutePath());

			String dir_archivo=fileToSave.getAbsolutePath()+DEFAULT_FILE_EXTENSION_BACKUP;

		//
		//parentFrame.toFront();
		//parentFrame.setAlwaysOnTop(true);
		//
			
		System.out.println("Guardando...");
		
		escribirArchivo(dir_archivo);
		String resumen=new String("RESUMEN DEL TRABAJO");
		
		
		Set<Integer> keys=MainDrawingScene.getListaUsuarios().keySet();
		for (Integer key:keys){
			//System.out.println("Usuariossss:   "+key);
			// Proyecto
			Usuario user=MainDrawingScene.getListaUsuarios().get(key);
			resumen+="\n**  Usuario: "+user.getNombres().toUpperCase()+"  **\n\t     Total Entidades creadas="+user.getCreacionesEntidades()+"\n\t     Total Edicion de Entidades ="+user.getEdicionesEntidades()+"\n\t     Total Entidades Eliminadas="+user.getEliminacionesEntidades()+"\n\t     Total Relaciones creadas="+user.getCreacionesRelaciones()+"\n\t     Total Edicion Relaciones ="+user.getEdicionesRelaciones()+"\n\t     Total Relaciones Eliminadas="+user.getEliminacionesRelaciones();

		
		}
		resumen+="";
		
		JOptionPane.showMessageDialog(null,resumen ,"Resumen",  JOptionPane.INFORMATION_MESSAGE);
	}
	/*	//String jsonText = JSONValue.toJSONString(jsonMap);

		// Escribo el String en archivo .json
			BufferedWriter writer = null;
			try {
						writer = new BufferedWriter( new FileWriter(dir_archivo));
						writer.write( jsonText);
						System.out.println("Guardado archivo .json");
			} catch (IOException e) {
						// TODO Auto-generated catch block
				System.out.println("Error");
						e.printStackTrace();
			}finally
			{
				System.out.println("Error2");
						try
					    {
					        if ( writer != null)
					        writer.close( );
					    }
					    catch ( IOException e)
					    {
					    }
			}
		
		
	//	}
		return true;
		*/
		return true;
	}

	@Override
	public void run() {
		while(true){
			try {			
				Thread.sleep(MT4jSettings.tiempoBackup);
				escribirArchivo(USER_BACKUP_DIRECTORY+MTApplication.separator +DEFAULT_FILE_NAME_BACKUP);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error al guardar el archivo");
				e.printStackTrace();
			}
		}
		
	}
}
