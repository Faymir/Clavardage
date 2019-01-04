package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Observable;

import Model.Message;
import Model.User;

public class UserChatListener extends Observable implements Runnable {
    protected Socket socket;
    protected String nickname;
    private String lastMessage;
    private boolean asNewMessage;
    private boolean working = true;
    BufferedReader inputFromFriend = null;
    private boolean connected = true;

    public UserChatListener(User u){
        this.nickname = u.getNickname();

    }
    public UserChatListener(String uname, Socket s){
        this.nickname = uname;
        this.socket = s;
    }

    @Override
    public void run() {
        while(working){
            try {
                inputFromFriend = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("before reading: [" + nickname + "]");
                String msg = inputFromFriend.readLine();
                if(msg != null)
                    notifyListeners(msg);
                else {
                    connected = false;
                    working = false;
                }
            } 
            catch(SocketException e) {
                System.out.println("Connexion fermée pendant l'écoute");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if(socket.isClosed()) {
                working = false;
                System.out.println("Closed Socket");
            }
        }
        System.out.println("Stopped Listening User: [" + nickname + "] maube connection lost");
    }

    private void notifyListeners(String msg){
        lastMessage = msg;
        asNewMessage = true;
        System.out.println("Received [" + lastMessage + "] from ip : [" + socket.getInetAddress().getHostAddress() + "] from [" + this.nickname + "] on port: [" + socket.getLocalPort() + "] from port: [" + socket.getPort()  + "]");
        Message m = new Message(nickname, Calendar.getInstance().getTime(),msg,null);
        setChanged();
        notifyObservers(m);
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean work) {
        this.working = work;
    }

    public String getLastMessage() {
        asNewMessage = false;
        return lastMessage;
    }


    public Socket getSocket() {
        return socket;
    }

    public String getNickname() {
        return nickname;
    }
}
