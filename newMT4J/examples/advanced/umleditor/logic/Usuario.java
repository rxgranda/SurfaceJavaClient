package advanced.umleditor.logic;

public class Usuario {
	public static final int ID_DEFAULT_USER=-1;
	public static final String NOMBRE_DEFAULT_USER="default";
	public static final String CANAL_DEFAULT_USER="canal1";

	private String nombres;
	private int idPluma;
	private String canal;
	private int estado;

	private int creacionesEntidades;
	private int eliminacionesEntidades;
	private int edicionesEntidades;
	
	private int creacionesRelaciones;
	private int eliminacionesRelaciones;
	private int edicionesRelaciones;
	
	public Usuario() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Usuario(int idPluma,String nombres, String canal, int estado ) {
		super();
		this.nombres = nombres;
		this.idPluma = idPluma;
		this.canal=canal;
		this.estado=estado;
		
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

	public int getCreacionesEntidades() {
		return creacionesEntidades;
	}

	public void agregarCreacionEntidades() {
		this.creacionesEntidades ++;
	}

	public int getEliminacionesEntidades() {
		return eliminacionesEntidades;
	}

	public void agregarElimininacionEntidades() {
		this.eliminacionesEntidades++;
	}

	public int getEdicionesEntidades() {
		return edicionesEntidades;
	}

	public void agregarEdicionEntidades() {
		this.edicionesEntidades++;
	}

	public int getCreacionesRelaciones() {
		return creacionesRelaciones;
	}

	public void agregarCreacionRelacion() {
		this.creacionesRelaciones ++;
	}

	public int getEliminacionesRelaciones() {
		return eliminacionesRelaciones;
	}

	public void agregarEliminacionRelacion() {
		this.eliminacionesRelaciones ++;
	}

	public int getEdicionesRelaciones() {
		return edicionesRelaciones;
	}

	public void agregarEdicionRelacion() {
		this.edicionesRelaciones ++;
	}


}
