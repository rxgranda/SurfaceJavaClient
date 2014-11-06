package advanced.umleditor.socketio;

import advanced.umleditor.logic.Relacion;


public class CardinalidadAdapter {
	private int id;
	private int idUsuario;
	private int cardinalidad;
	private boolean cardinalidadSwitch; //true= Cardinalidad Inicio de relacion, false= cardinalidad Fin deFin
	
	public CardinalidadAdapter(Relacion r, boolean cardinalidadSwitch, int idUsuario) {
		this.id=r.getId();
		this.idUsuario=idUsuario;
		if(cardinalidadSwitch)
			this.setCardinalidad(r.getCardinalidadInicio());
		else
			this.setCardinalidad(r.getCardinalidadFin());
		// TODO Auto-generated constructor stub
	}
	
	public CardinalidadAdapter() {
		
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	

	public int getCardinalidad() {
		return cardinalidad;
	}

	public void setCardinalidad(int cardinalidad) {
		this.cardinalidad = cardinalidad;
	}

	public boolean isCardinalidadSwitch() {
		return cardinalidadSwitch;
	}

	public void setCardinalidadSwitch(boolean cardinalidadSwitch) {
		this.cardinalidadSwitch = cardinalidadSwitch;
	}
	

}
