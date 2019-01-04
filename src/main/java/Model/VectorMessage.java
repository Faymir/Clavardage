package Model;

import java.io.Serializable;
import java.util.Vector;

public class VectorMessage implements Serializable {
    public Vector<Message> discussion;

    public VectorMessage(Vector<Message> discussion){
        this.discussion = discussion;
    }
}
