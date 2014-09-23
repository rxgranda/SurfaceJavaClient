package advanced.umleditor.impl;

import org.mt4j.components.MTComponent;
import org.mt4j.util.MTColor;

public interface ObjetoUMLGraph {
	public static MTColor azul=new MTColor(76, 96, 245);
	public static MTColor headerColor=new MTColor(90,119,248);
	public static final MTColor selectedObject = new MTColor(253,205,161);
	public static final MTColor nonselectedObject = new MTColor(255,255,255);

	public MTComponent getFigura();
	public void setTitulo(String texto);
	public String getTitulo(String texto);
	public void setAtributo(String texto);
	public String getAtributo(String texto);
}
