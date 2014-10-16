package advanced.umleditor.chat;

public class MessageSurface {

    private int objetoID;
    private int ready;

    public MessageSurface() {
    }

    public MessageSurface(int objetoID, int ready) {
    	super();
        this.objetoID = objetoID;
        this.ready = ready;
    }

    public int getObjetoID() {
        return objetoID;
    }

    public int getReady() {
        return ready;
    }
    
    public void setReady(int ready) {
        this.ready = ready;
    }

    public void setObjetoID(int objetoID) {
        this.objetoID = objetoID;
    }

}