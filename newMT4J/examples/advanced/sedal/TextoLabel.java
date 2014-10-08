package advanced.sedal;

public class TextoLabel {

    private String message;
    private int objectID;

    public TextoLabel() {
    }

    public TextoLabel(String message, int objectID) {
       
        this.objectID = objectID;
        this.message = message;
    }

    public int getObjectID() {
        return objectID;
    }

    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

}