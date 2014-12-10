package advanced.umleditor.logic;

public interface UndoInterface {
	public ObjetoUML clonar();
	
	public void restaurar( ObjetoUML objeto);
}
