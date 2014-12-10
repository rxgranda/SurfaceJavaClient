package advanced.umleditor.impl;

import org.mt4j.util.math.Vector3D;

public class HaloHelper {


	private Vector3D hoverInicio;
	private Vector3D hoverFin;
	private boolean hoverFueraHalo=false;
	public HaloHelper(){
		hoverInicio=new Vector3D();
		hoverFin=new Vector3D();
		setHoverFueraHalo(false);
	}
	
	
	
	public Vector3D getHoverInicio() {
		return hoverInicio;
	}
	public void setHoverInicio(Vector3D hoverInicio) {
		this.hoverInicio = hoverInicio;
	}
	public Vector3D getHoverFin() {
		return hoverFin;
	}
	public void setHoverFin(Vector3D hoverFin) {
		this.hoverFin = hoverFin;
	}



	public boolean isHoverFueraHalo() {
		return hoverFueraHalo;
	}



	public void setHoverFueraHalo(boolean hoverFueraHalo) {
		this.hoverFueraHalo = hoverFueraHalo;
	}
}