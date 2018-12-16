package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import Model.User;

public class UserChatListener extends Thread {
    protected Socket socket;
    protected String nickname;
    private String lastMessage;
    private boolean asNewMessage;
    private boolean working = true;
    BufferedReader inputFromFriend = null;
    private boolean connected = true;

    public UserChatListener(User u){
        this.nickname = u.getNickname();
        this.socket = u.getSocket();

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
                String msg = inputFromFriend.readLine();
                if(msg != null){
                    lastMessage = msg;
                    asNewMessage = true;
                    System.out.println("Received [" + lastMessage + "] from User: [" + this.nickname + "] on port: [" + socket.getLocalPort() + "] from port: [" + socket.getPort()  + "]");
                }
                else {
                    connected = false;
                    working = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(socket.isClosed())
                working = false;
        }
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
