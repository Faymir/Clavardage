package Model;

public class Signal {
    public Type type;
    public String message;
    public String newUsername;
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

    public Signal(Type signalType, String oldUsername, String newUsername){
        this(signalType, oldUsername);
        this.newUsername = newUsername;
    }

//    public fr.faymir.Model.Signal(fr.faymir.Model.Type signalType, String from, String )
}
