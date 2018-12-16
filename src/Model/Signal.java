package Model;

public class Signal {
    public Type type;
    public String message;

    public Signal(Type signalType, String content){
        this.message = content;
        this.type = signalType;
    }
}
