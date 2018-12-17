package Model;

public enum Type{
    SCAN("Server Scan"),
    CONNECT("New User Connected"),
    DISCONNECT("A user Disconnected"),
    INIT_CHAT("Init Chat with User"),
    NEW_MESSAGE("You received a new message"),
    SHOW_DISCUSSION("Show discussion with this user in the webview window");
    private String text;
    Type(String text){
        this.text = text;
    }
}
