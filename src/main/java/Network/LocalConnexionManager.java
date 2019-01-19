package Network;

import Model.Signal;
import Model.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LocalConnexionManager extends ConnexionManager<Integer> {

    protected static final int portStart = 10000;
    protected static final int portEnd = 10005;

    public LocalConnexionManager(){
        super(ManagerMode.TEST);
    }

    @Override
    protected void init() {
        super.init();
        int randomNum = 0;
        while (serverSocket == null) {
            try {
                randomNum = ThreadLocalRandom.current().nextInt(portStart, portEnd);
                serverSocket = new ServerSocket(randomNum);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        server_port = randomNum;
        //scanUsers();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendUpdateInformation(String str){
        Set<Map.Entry<String, Integer>> setHm = connectedUsers.entrySet();
        for (Map.Entry<String, Integer> e : setHm) {
            UserChatListener u = getFriend(e.getKey());
            try {
                if (u == null) {
                    Socket s = new Socket("localhost", e.getValue());
                    sendMessage(s, str);
                } else
                    sendMessage(u.getSocket(), str);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void handleInitConnexion(String uname, Socket socket) {
        this.connectedUsers.put(uname,socket.getPort());
    }

    @Override
    protected void handleConnected(String uname, Socket socket) {
//                    System.out.println("getPort() = [" + socket.getRemoteSocketAddress().toString() + "], getLocalPort = [" + socket.getLocalPort() + "]");
        this.connectedUsers.put(uname, socket.getPort());
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanUsers();
        printUsers();
        setChanged();
        notifyObservers(new Signal(Type.CONNECT, uname));
    }

    @Override
    protected void handleDisconnect(String uname, Socket socket, String msg) {
        this.connectedUsers.remove(uname);
        UserChatListener u = getFriend(uname);
        if (u != null) {
            u.setWorking(false);
            try {
                u.getSocket().close();
            } catch (IOException e) {
                System.out.println("Error when disconnecting: msg = [" + msg + "], socket = [" + socket + "]");
                e.printStackTrace();
            }
            this.friendList.remove(u);
            u = null;
        }
        System.out.println("User [" + uname + "] disconnected!!");
    }

    @Override
    protected void scanUsers() {
        long a = System.currentTimeMillis();
        for (int i = portStart; i < portEnd; i++) {
            if (i != server_port && !connectedUsers.containsValue(i)) {
                try {
                    Socket socket = new Socket("localhost", i);
                    socket.setSoTimeout(50);
                    BufferedReader entreeDepuisClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter sortieVersClient = new PrintWriter(socket.getOutputStream());
                    sortieVersClient.println(" " + "%&%" + "scan");
                    sortieVersClient.flush();
                    String username = entreeDepuisClient.readLine();
                    System.out.println("new user: " + username);
                    //User u = new User(username, socket);

                    this.connectedUsers.put(username, i);
                    entreeDepuisClient.close();
                    sortieVersClient.close();

                } catch (SocketTimeoutException e) {
                    System.out.println("timeout");
                } catch (ConnectException e) {
                    System.out.println("not opened");//e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("time = " + (System.currentTimeMillis() - a));
        printUsers();
        System.out.println("\t\tEnd Scan");
    }

    @Override
    protected Socket newSocket(Integer value) throws IOException {
        return new Socket("localhost", value);
    }

    @Override
    public void connect(String nickname) {
        scanUsers();

        boolean isUsed = connectedUsers.containsKey(nickname);
        setChanged();
        if(isUsed){
            notifyObservers(new Signal(Type.BAD_USERNAME, ""));
        }
        else{
            notifyObservers(new Signal(Type.GOOD_USERNAME, ""));
            printUsers();
        }
    }


    @Override
    public void disconnect() {
        sendUpdateInformation(clientName+"%&%"+"disconnect");
    }
}
