package advanced.umleditor.logic;

public class Usuario {
	private String nombres;
	private int idPluma;
	private String canal;
	private int estado;
	private String uuid;

	
	public Usuario() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Usuario(int idPluma,String nombres, String canal, int estado, String uuid ) {
		super();
		this.nombres = nombres;
		this.idPluma = idPluma;
		this.canal=canal;
		this.estado=estado;
		this.uuid=uuid;
		
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public int getIdPluma() {
		return idPluma;
	}

	public void setIdPluma(int idPluma) {
		this.idPluma = idPluma;
	}

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
