package advanced.umleditor.impl;

import java.util.LinkedList;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.util.MTColor;

import advanced.umleditor.logic.ObjetoUML;

public interface ObjetoUMLGraph {
	public static final MTColor azul=new MTColor(76, 96, 245);
	public static final MTColor rojo=new MTColor(240, 0, 0);
	public static final MTColor headerColor=new MTColor(2,196,238);//new MTColor(45,137,239);

	public static final MTColor bodyColor=new MTColor(220,220,220);
	public static final MTColor resizeButtonColor=new MTColor(255,255,255,0);
	public static final MTColor selectedObject = new MTColor(253,205,161);
	public static final MTColor nonselectedObject =new MTColor(255,255,255);
	public static final MTColor haloSelected =new MTColor(200,200,200);// //new MTColor(148,214,247,10);
	public static final MTColor haloDeSelected =new MTColor(200,200,200);// //new MTColor(255,255,255); 
	
	public static final int haloWidth=27;
	public static final String RELACIONES_INICIO_KEYWORD = "relaciones-inicio";
	public static final String RELACIONES_FIN_KEYWORD = "relaciones-fin";
	public static final String COMPONENTES_VISITADOS_KEYWORD = "visitados";
	public static final String ENTIDADES_KEYWORD = "entidad";

	

	public void guardarDatos(String keyword,Object datos );
	public LinkedList obtenerDatos(String keyword);



	public MTComponent getFigura();
	public MTComponent getHalo();
	public void setTitulo(String texto);
	public String getTitulo(String texto);
	public void setAtributo(String texto);
	public String getAtributo(String texto);
	
	public ObjetoUML getObjetoUML();
	public void setObjetoUML(ObjetoUML objeto);
}
