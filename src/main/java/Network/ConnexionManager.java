package Network;

import Model.Message;
import Model.Signal;
import Model.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;


/**
 *                                          Scénario
 * Démarrer serveur => scan et récupération des ports et des username uniquement pas de sockets
 *      => Username%&%scan
 * Envoie de Message
 *      => initConnection si première communication
 *      => sauvegarde des sockets
 *      => ajout de listeners au niveau des deux clients
 *      => envoie de message
 *      => reception au niveau de l'autre côté
 *      => traitment du message recu (à voir plus tard ***********************)
 *
 *
 *
 *
 */

/**
 *
 * Observe la NetworkScanListener
 *
 * notifie la MainController pour lui signaler de se mettre a jour par rapport à
 * 		=> l'arrivée d'un nouvel utilisateur
 * 		=> le démarrage d'une nouvelle discussion
 * 		=> la déconnexion d'un utlilisateur
 *
 * notifie un User pour lui signaler q'on vient de se connecter, déconnecter
 *
 * 	Permet aussi de se brancher a la UserListener pour ecouter l'arrivée de nouveaux messages
 */


public abstract class ConnexionManager<T> extends Observable implements Runnable, Observer{

    public enum ManagerMode{
        TEST(1),
        BROADCAST(2),
        SERVER(3);
        private int mode;
        ManagerMode(int i){
            mode = i;
        }
    }

    protected ServerSocket serverSocket = null;
    protected String clientName = null;
    protected Vector<UserChatListener> friendList;
    protected HashMap<String, T> connectedUsers;

    protected int server_port = 0;
    protected ManagerMode mode = null;
    protected boolean work;
    protected String serverUrl = null;
    protected String uniqueID = UUID.randomUUID().toString();

    public ConnexionManager(){
        super();
        init();
    }

    public ConnexionManager(ManagerMode mode){
        this();
        this.mode = mode;
    }

    @Override
    public void run() {
        while (work){
            try {
                Socket socket = serverSocket.accept();
                BufferedReader entreeDepuisClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = entreeDepuisClient.readLine();
                this.handleMessage(msg, socket);

            }
            catch (ConnectException e) {
                System.out.println("except");//e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void init(){

        work = true;
        friendList = new Vector<>();
        connectedUsers = new HashMap<>();
        clientName = "%%NONE%%";
    }

    protected abstract void sendUpdateInformation(String str);

    protected abstract void sendUserNameChanged(String newUsername); //Update Local information and send new username to others

    public boolean changeUsername(String newUsername){
        if(connectedUsers.containsKey(newUsername))
            return false;

        System.out.println("************* set client name");
        sendUserNameChanged(newUsername);
        this.clientName = newUsername;
        printUsers();
        System.out.println("************* END set client name");
        return true;
    }

    public boolean isUsernameExist(String uname){
        return connectedUsers.containsKey(uname) || clientName.equals(uname);
    }

    protected void handleMessage(String msg, Socket socket){

        if(msg == null || msg.isEmpty()){
            System.out.println("Nothing handled");
        }
        else if (msg.contains("%&%")){
            String uname = msg.split( "%&%")[0];
            String order = msg.split("%&%")[1];

            switch (order){
                case "initConnection": {
                    handleInitConnexion(uname, socket);
                    UserChatListener u = new UserChatListener(uname, socket);
                    this.friendList.add(u);
                    (new Thread(u)).start();
                    setChanged();
                    notifyObservers(new Signal(Type.INIT_CHAT, uname));
                    printUsers();
                }
                break;
                case "scan":
                    sendMessage(socket,clientName);
                    setChanged();
                    notifyObservers(new Signal(Type.SCAN, socket.getInetAddress().getHostAddress()));
                    break;
                case "connected":
                    handleConnected(uname, socket);
                    break;
                case "disconnect":
                    handleDisconnect(uname, socket, msg);
                    setChanged();
                    notifyObservers(new Signal(Type.DISCONNECT, uname));
                    printUsers();
                    break;
                case "newUsername":
                    String oldUname = uname.split("---")[0], newUname = uname.split("---")[1];
                    T port  = this.connectedUsers.remove(oldUname);
                    this.connectedUsers.put(newUname, port);
                    getFriend(oldUname).setNickname(oldUname);
                    System.out.println("Username changed from [" + oldUname + "] to [" + newUname + "]");
                    setChanged();
                    notifyObservers(new Signal(Type.USERNAME_CHANGED, oldUname, newUname));
                    printUsers();
                    break;
                default:
                    break;
            }
        }
    }

    protected abstract void handleConnected(String uname, Socket socket);
    protected abstract void handleDisconnect(String uname, Socket socket, String msg);
    protected abstract void handleInitConnexion(String uname, Socket socket);

    protected boolean isChattingWith(String uname) {
        for (UserChatListener u : friendList) {
            if (u.getNickname().equals(uname))
                return true;
        }
        return false;
    }

    public void printUsers(){
        Set<Map.Entry<String, T>> setHm = connectedUsers.entrySet();

        for (Map.Entry<String, T> e : setHm) {
            System.out.println("username = [" + e.getKey() + "], value = [" + e.getValue() + "]");
        }
        System.out.println("=====================");
    }

    protected UserChatListener getFriend(String username){
        for (UserChatListener user: friendList) {
            if(user.getNickname().compareTo(username) == 0)
                return user;
        }
        return null;
    }

    protected void sendMessage(Socket socket, String msg){
        if(socket != null && msg != null){

            PrintWriter sortieVersClient = null;
            try {
                sortieVersClient = new PrintWriter(socket.getOutputStream());
                sortieVersClient.println(msg);
                sortieVersClient.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            System.out.println("CANNOT SEND MESSAGE socket = [" + socket + "], msg = [" + msg + "]");
    }

    protected abstract void scanUsers();

    public boolean sendMessage(Message m){
        PrintWriter sortieVersClient = null;
        initChat(m.getReceiver());
        UserChatListener u = getFriend(m.getReceiver());
        if(u != null){
            try {
//                System.out.println("getPort() = [" + u.getSocket().getPort() + "], getLocalPort = [" + u.getSocket().getLocalPort() + "]");
                sortieVersClient = new PrintWriter(u.getSocket().getOutputStream());
//                Cryptography crypt = new Cryptography(Database.getPrivateKey(), Database.getPublicKey());
//                System.out.println("encrypted msg = [" + crypt.encryptToString(m.getMessage()) + "]");
                sortieVersClient.println(m.getMessage());
                sortieVersClient.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("username = [" + m.getSender() + "], str = [" + m.getMessage() + "], size= [" + friendList.size() + "]\n");
            printUsers();
        }
        return false;
    }

    public boolean initChat(String uname){
        T value = null;
        value = connectedUsers.get(uname);
        if(value != null && !isChattingWith(uname)){
            Socket s = null;
            try {
                s = newSocket(value);
                UserChatListener u = new UserChatListener(uname,s);
                sendMessage(s,clientName + "%&%" + "initConnection");
                (new Thread(u)).start();
                this.friendList.add(u);
                u.addObserver(this);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    protected abstract Socket newSocket(T value) throws IOException;

    public void sendUpdateToFriends(String msg){
        for (UserChatListener chatListener : friendList) {
            sendMessage(chatListener.getSocket(), msg);
        }
    }

    public abstract void connect(String nickname);

    public boolean getWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
        if (!work)
            disconnect();
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        try {
            System.out.println("server_port = [" + server_port + "]");
            serverSocket = new ServerSocket(server_port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.clientName = clientName;
        sendUpdateInformation(clientName+"%&%"+"connected");
        printUsers();
    }

    public Vector<UserChatListener> getFriendList() {
        return friendList;
    }

    public Vector<String> getConnectedUsersName(){
        Vector<String> res = new Vector<>();
        Set<Map.Entry<String, T>> setHm = connectedUsers.entrySet();
        for (Map.Entry<String, T> e : setHm) {
            res.add(e.getKey());
        }
        return  res;
    }

    public abstract void disconnect();

    public void sendDisconnect(){
        for (UserChatListener l: friendList) {
            l.setWorking(false);
        }
        disconnect();
    }

    public void addIncomingMessageListener(String uname,Observer observer){
        UserChatListener l = getFriend(uname);
        if(l != null){
            l.addObserver(observer);
        }
    }

    @Override
    public void update(Observable observable, Object o) {}

    public ManagerMode getMode() {
        return mode;
    }

    public void setMode(ManagerMode mode) {
        this.mode = mode;
        init();
    }

    public void setServerUrl(String serverUrl) throws MalformedURLException {
        this.serverUrl = serverUrl;
    }
}