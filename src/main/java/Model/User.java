package Model;

import Network.UserChatListener;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Se met en ecoute par rapport à un UserListener pour savoir si il y a  des nouveaux messages
 *
 * notifie la friendViewController pour lui signaler de se mettre a jour par rapport à l'affichage d'une notification
 */


public class User extends Observable implements Observer {

    protected String nickname;
    private int status;
    private Vector<Message> discussion;
    private int lastMessageIndex;
    private boolean connected = true;

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
        if (observable.getClass() == UserChatListener.class) {
            this.discussion.add((Message) o);
            setChanged();
            notifyObservers(new Signal(Type.NEW_MESSAGE, getNewMessageNumber()));
        }
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
        if(discussion.size() > 1)
            return discussion.size() - lastMessageIndex - 1;
        else
            return 1;
    }


    public Vector<Message> getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Vector<Message> discussion) {
        this.discussion = discussion;
    }

    public void addMessage(Message m){
        discussion.add(m);
    }


    public String getNickname() {
        return nickname;
    }

    public void setUsername(String nickname) {
        this.nickname = nickname;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setLastMessageIndex(int lastMessageIndex) {
        this.lastMessageIndex = lastMessageIndex;
    }

    public int getLastMessageIndex() {
        return lastMessageIndex;
    }

    public void resetLastMessageIndex(){
        this.lastMessageIndex = discussion.size() - 1;
    }

    public void loadDiscussion(){

    }
}
