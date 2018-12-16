package Model;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class User extends Observable implements Observer {

    protected String nickname;
    private int status;
    private Vector<Message> discussion;
    private int lastMessageIndex;

    public User(String uname){
        this.nickname = uname;
        status = 0;
        lastMessageIndex = 0;
        discussion = new Vector<>();
    }

    public User(String uname, Vector<Message> discussion, int lastMessageIndex){
        this(uname);
        this.lastMessageIndex = lastMessageIndex;
        this.discussion = discussion;
    }

    @Override
    public void update(Observable observable, Object o) {
        this.discussion.add((Message)o);
        setChanged();
        notifyObservers(lastMessageIndex);
    }



    public Vector<Message> getLastMessages(){
        if(lastMessageIndex > discussion.size()) {
            System.out.println("This Message should never appear: GET LAST MESSAGE METHOD OF USER CLASS");
            System.exit(discussion.size() - lastMessageIndex);
        }
        Vector<Message> res = new Vector<>();
        for(int i = lastMessageIndex; i< discussion.size(); i++)
            res.add(discussion.get(i));
        lastMessageIndex = discussion.size();
        return  res;
    }

    public int getNewMessageNumber(){
        return discussion.size() - lastMessageIndex;
    }


    public Vector<Message> getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Vector<Message> discussion) {
        this.discussion = discussion;
    }


    public String getNickname() {
        return nickname;
    }

    public void setUsername(String nickname) {
        this.nickname = nickname;
    }


    public void loadDiscussion(){

    }
}
