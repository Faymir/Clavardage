package Model;

import java.net.Socket;

public class User {

    protected Socket socket;
    protected String nickname;
    private int status;

    public User(){
        nickname = "";
        socket = null;
    }
    public User(String uname, Socket usocket){
        this.nickname = uname;
        this.socket = usocket;
    }

    public User(String name){
        this.nickname = name;
    }


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getNickname() {
        return nickname;
    }

    public void setUsername(String nickname) {
        this.nickname = nickname;
    }



}
