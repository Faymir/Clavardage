package Model;

public enum Type{
    SCAN("Server Scan"),
    CONNECT("New User Connected"),
    DISCONNECT("A user Disconnected"),
    INIT_CHAT("Init Chat with User");
    private String text;
    Type(String text){
        this.text = text;
    }
}
