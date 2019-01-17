package Network;

import Model.*;
import Security.Cryptography;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


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
 * Ok Reste a corriger le bug de contact par l'autre (différence de port en recption et en emission) => mettre a jour connectedUsers avec la nouvelle valeure du port
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


public class ConnexionManager extends Observable implements Runnable, Observer{

    public enum ManagerMode{
        TEST(1),
        BROADCAST(2);
        private int mode;
        private ManagerMode(int i){
            mode = i;
        }
    }

	private ServerSocket serverSocket = null;
	private String clientName = null;
	private String tmpClientName = null;
	private Vector<UserChatListener> friendList;
	private HashMap<String, Integer> connectedUsers;
    private HashMap<String, String> connectedUsers2;

	private static final int portStart = 10000;
	private static final int portEnd = 10005;
	private int server_port = 0;
    private ManagerMode mode = ManagerMode.TEST;
    private NetworkScanner networkScanner;
    private NetworkScanListener networkScanListener;
    private String uniqueID = UUID.randomUUID().toString();
	private boolean work;

	public ConnexionManager(ManagerMode mode){
		super();
        this.mode = mode;
		init();

	}

	private void init(){

		work = true;
		friendList = new Vector<>();
		connectedUsers = new HashMap<>();
        connectedUsers2 = new HashMap<>();
		clientName = "%%NONE%%";
        networkScanner = new NetworkScanner(uniqueID);
        networkScanner.addObserver(this);
        networkScanListener = null;
		int randomNum = 0;
		if(mode == ManagerMode.TEST) {
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
            //System.out.println("Scan finished");
        }
		else if(mode == ManagerMode.BROADCAST)
		    server_port = 11000;

	}

	@Override
	public void run() {
		while (work){
			try {
				Socket socket = serverSocket.accept();
				BufferedReader entreeDepuisClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String msg = entreeDepuisClient.readLine();
				System.out.println("Message: {" + msg + "}");
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
				System.out.println("Sended!!!");
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

	public void sendMessage(Socket socket, String msg){
		if(socket != null && msg != null){

			PrintWriter sortieVersClient = null;
			try {
				sortieVersClient = new PrintWriter(socket.getOutputStream());
				sortieVersClient.println(msg);
				sortieVersClient.flush();
				System.out.println("Sended (socket)!!!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("CANNOT SEND MESSAGE socket = [" + socket + "], msg = [" + msg + "]");
	}

    public boolean initChat(String uname){
        Integer port = null;
        String ip = null;
        port = connectedUsers.get(uname);
        ip = connectedUsers2.get(uname);
        if((port != null || ip!=null) && !isChattingWith(uname)){
            Socket s = null;
            try {
                if(mode == ManagerMode.TEST)
                    s = new Socket("localhost", port);
                else
                    s = new Socket(ip, 11000);

                UserChatListener u = new UserChatListener(uname,s);
                sendMessage(s,clientName + "%&%" + "initConnection");
                (new Thread(u)).start();
                this.friendList.add(u);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

	private void handleMessage(String msg, Socket socket){

        System.out.println("\t\t************* Deb handleMessage");
		if(msg == null || msg.isEmpty()){
			System.out.println("Nothing handled");
		}
		else if (msg.contains("%&%")){
			String uname = msg.split( "%&%")[0];
			String order = msg.split("%&%")[1];

			switch (order){
				case "initConnection": {
				    if(mode == ManagerMode.TEST)
                        this.connectedUsers.put(uname,socket.getPort());
                    else if(mode == ManagerMode.BROADCAST)
                        this.connectedUsers2.put(uname, socket.getInetAddress().getAddress().toString());

					UserChatListener u = new UserChatListener(uname, socket);
                    (new Thread(u)).start();
					this.friendList.add(u);
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
				case "disconnect":
                    if (mode == ManagerMode.TEST) {
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
                        setChanged();
                        notifyObservers(new Signal(Type.DISCONNECT, uname));
                        printUsers();
					break;
				case "connected":
                    if (mode == ManagerMode.TEST) {
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
					break;
				default:
					break;
			}
		}


        System.out.println("\t\t************* END handle Message");
	}

	public void scanUsers(){
        System.out.println("\t\tScan Users");
        if (mode == ManagerMode.TEST) {
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
//					System.out.println("Port " + i + " user number: " + (connectedUsers.size() + 1));
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
        else if(mode == ManagerMode.BROADCAST){
            try {
                networkScanner.scanNetwork();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	public void sendUpdateToFriends(String msg){
		for (UserChatListener chatListener : friendList) {
			sendMessage(chatListener.getSocket(), msg);
		}
	}

	public void sendUpdateToConnected(String msg){

		Set<Map.Entry<String, Integer>> setHm = connectedUsers.entrySet();
		Iterator<Map.Entry<String, Integer>> it = setHm.iterator();

		while(it.hasNext()){

            Map.Entry<String, Integer> e = it.next();
            UserChatListener u = getFriend(e.getKey());
            try {
                if(u == null){
                        Socket s = new Socket("localhost",e.getValue());
                        sendMessage(s,msg);
                }
                else
                    sendMessage(u.getSocket(),msg);
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
		}
	}

	private boolean isChattingWith(String uname) {
		for (UserChatListener u : friendList) {
			if (u.getNickname().equals(uname))
				return true;
		}
		return false;
	}

	public void isUsed(String nickname) {
	    if(mode == ManagerMode.TEST) {
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
        else if(mode == ManagerMode.BROADCAST){
            tmpClientName = nickname;
            try {
                networkScanner = new NetworkScanner(uniqueID);
                networkScanner.addObserver(this);
                networkScanner.scanNetwork();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

	}

	public boolean getWork() {
		return work;
	}

	public void setWork(boolean work) {
		if(this.networkScanListener != null){
            ScanMessage msg = new ScanMessage(ScanMessage.ScanType.DISCONNECT,networkScanListener.getVersionNumber(),clientName);
            msg.uniqueID = uniqueID;
            try {
                NetworkScanner.broadcastToAll(SerializationUtils.serialize(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.work = work;
	}

	public UserChatListener getFriend(String username){
		for (UserChatListener user: friendList) {
			if(user.getNickname().compareTo(username) == 0)
				return user;
		}
		return null;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
        System.out.println("\t\t************* set client name");
		try {
            System.out.println("server_port = [" + server_port + "]");
			serverSocket = new ServerSocket(server_port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.clientName = clientName;

        if(mode == ManagerMode.TEST)
		    this.sendUpdateToConnected(clientName+"%&%"+"connected");
        else if(mode == ManagerMode.BROADCAST){
            sendUpdateInformation();
        }
		printUsers();

        System.out.println("\t\t************* END set client name");
	}

	private void sendUpdateInformation(){
        ScanMessage msg = new ScanMessage(ScanMessage.ScanType.UPDATE_INFORMATION,networkScanListener.getVersionNumber(),clientName);
        msg.uniqueID = uniqueID;
        try {
            NetworkScanner.broadcastToAll(SerializationUtils.serialize(msg));
            System.out.println("\n\nI AM THE NEW RESPONDER OF BROADCAST MESSAGES [" + networkScanListener.getVersionNumber() + "]\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void printUsers(){
        if (mode == ManagerMode.TEST) {
            Set<Map.Entry<String, Integer>> setHm = connectedUsers.entrySet();
            Iterator<Map.Entry<String, Integer>> it = setHm.iterator();

            while (it.hasNext()) {

                Map.Entry<String, Integer> e = it.next();

                System.out.println("username = [" + e.getKey() + "], port = [" + e.getValue() + "]");

            }
        }
        else if(mode == ManagerMode.BROADCAST){
            Set<Map.Entry<String, String>> setHm = connectedUsers2.entrySet();
            Iterator<Map.Entry<String, String>> it = setHm.iterator();

            while (it.hasNext()) {

                Map.Entry<String, String> e = it.next();

                System.out.println("username = [" + e.getKey() + "], ip = [" + e.getValue() + "]");

            }
        }
	}

    public Vector<UserChatListener> getFriendList() {
        return friendList;
    }

    public Vector<String> getConnectedUsersName(){
	    Vector<String> res = new Vector<>();
        if (mode == ManagerMode.TEST) {
            Set<Map.Entry<String, Integer>> setHm = connectedUsers.entrySet();
            Iterator<Map.Entry<String, Integer>> it = setHm.iterator();

            while(it.hasNext()){

                Map.Entry<String, Integer> e = it.next();
                res.add(e.getKey());
            }
        }
        else if (mode == ManagerMode.BROADCAST){
            Set<Map.Entry<String, String>> setHm = connectedUsers2.entrySet();
            Iterator<Map.Entry<String, String>> it = setHm.iterator();

            while(it.hasNext()){

                Map.Entry<String, String> e = it.next();
                res.add(e.getKey());
            }
        }
        return  res;
    }

    public void sendDisconnect(){
        for (UserChatListener l: friendList) {
            l.setWorking(false);
        }
	    sendUpdateToConnected(clientName+"%&%"+"disconnect");
    }

    public void addIncomingMessageListener(String uname,Observer observer){
	    UserChatListener l = getFriend(uname);
	    if(l != null){
	        l.addObserver(observer);
        }
    }

    @Override
    public void update(Observable observable, Object o) {

        if(observable.getClass() == NetworkScanner.class){
            //recu message de type Return Information
            ScanMessage msg = (ScanMessage) o;

            if(msg.uniqueID.equals(this.uniqueID) || !msg.clients.containsKey(tmpClientName)) {
                this.connectedUsers2 = (HashMap<String, String>) msg.clients.clone();
                if(msg.newUsername!=null && !this.tmpClientName.equals(msg.newUsername))
                    this.connectedUsers2.put(msg.newUsername, msg.ip);
                networkScanListener = new NetworkScanListener(tmpClientName, msg.clients, msg.newUserVersion, msg.newUserVersion, uniqueID);
                networkScanListener.listen();
                networkScanListener.addObserver(this);
                setChanged();
                notifyObservers(new Signal(Type.GOOD_USERNAME, ""));
            }
            else{
                setChanged();
                notifyObservers(new Signal(Type.BAD_USERNAME, ""));
            }
        }
        else if(observable.getClass() == NetworkScanListener.class){
            ScanMessage msg = (ScanMessage) o;
            if(msg.type == ScanMessage.ScanType.UPDATE_INFORMATION){
                connectedUsers2.put(msg.newUsername, msg.ip);
                printUsers();
                setChanged();
                notifyObservers(new Signal(Type.CONNECT, msg.newUsername));
            }
            if (msg.type == ScanMessage.ScanType.DISCONNECT){
                connectedUsers2.remove(msg.newUsername);
                friendList.removeIf( friend -> friend.nickname.equals(msg.newUsername) );
                printUsers();
                setChanged();
                notifyObservers(new Signal(Type.DISCONNECT, msg.newUsername));
            }
        }
    }

    public ManagerMode getMode() {
        return mode;
    }

    public void setMode(ManagerMode mode) {
        this.mode = mode;
        init();
    }
}