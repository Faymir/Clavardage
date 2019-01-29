package Model;

public enum Type{
    SCAN("Server Scan"),
    CONNECT("New Model.User Connected"),
    DISCONNECT("A user Disconnected"),
    INIT_CHAT("Init Chat with Model.User"),
    NEW_MESSAGE("You received a new message"),
    SHOW_DISCUSSION("Show discussion with this user in the webview window"),
    GOOD_USERNAME("This username is available"),
    BAD_USERNAME("This username is already taken"),
    USERNAME_CHANGED("This user changed his username");
    private String text;
    Type(String text){
        this.text = text;
    }
}
