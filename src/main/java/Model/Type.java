package Model;

public enum Type{
    SCAN("Server Scan"),
    CONNECT("New User Connected"),
    DISCONNECT("A user Disconnected"),
    INIT_CHAT("Init Chat with User"),
    NEW_MESSAGE("You received a new message"),
    SHOW_DISCUSSION("Show discussion with this user in the webview window"),
    GOOD_USERNAME("This username is available"),
    BAD_USERNAME("This username is already taken");
    private String text;
    Type(String text){
        this.text = text;
    }
}
