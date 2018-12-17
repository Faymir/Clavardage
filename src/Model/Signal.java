package Model;

public class Signal {
    public Type type;
    public String message;
    public int intValue;
    //public String from;

    public Signal(Type signalType, String content){
        this.message = content;
        this.type = signalType;
        intValue = 0;
    }
    public Signal(Type signalType, int value){
        this.message = null;
        this.type = signalType;
        intValue = value;
    }

//    public Signal(Type signalType, String from, String )
}
