package Network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import Model.Message;
import Model.Signal;
import Model.Type;


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
 * notifie la MainController pour lui signaler de se mettre a jour par rapport à
 * 		=> l'arrivée d'un nouvel utilisateur
 * 		=> le démarrage d'une nouvelle discussion
 * 		=> la déconnexion d'un utlilisateur
 *
 * 	Permet aussi de se brancher a la UserListener pour ecouter l'arrivée de nouveaux messages
 */


public class ConnexionManager extends Observable implements Runnable{
	private ServerSocket serverSocket = null;
	private String clientName = null;
	private Vector<UserChatListener> friendList;
	private Map<String, Integer> connectedUsers;

	private static final int portStart = 10000;
	private static final int portEnd = 11000;
	private int server_port = 0;

	private boolean work;

	public ConnexionManager(){
		super();
		init();

	}

	private void init(){

		work = true;
		friendList = new Vector<>();
		connectedUsers = new HashMap<>();
		clientName = "%%NONE%%";
		int randomNum = 0;
		while(serverSocket == null){
			try {
				randomNum = ThreadLocalRandom.current().nextInt(portStart, portEnd);
				serverSocket = new ServerSocket(randomNum);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		server_port = randomNum;
		scanUsers();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Scan finished");

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
				System.out.println("Sended!!!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("CANNOT SEND MESSAGE socket = [" + socket + "], msg = [" + msg + "]");
	}

	public boolean initChat(String uname){
		Integer port = null;
		port = connectedUsers.get(uname);
		if(port != null && !isChattingWith(uname)){
			Socket s = null;
			try {

				s = new Socket("localhost", port);
				UserChatListener u = new UserChatListener(uname,s);
				sendMessage(s,clientName + "%&%" + "initConnection");
                (new Thread(u)).start();
				this.friendList.add(u);
                setChanged();
                notifyObservers(new Signal(Type.INIT_CHAT, uname));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void handleMessage(String msg, Socket socket){
		if(msg == null || msg.isEmpty()){
			System.out.println("Nothing handled");
		}
		else if (msg.contains("%&%")){
			String uname = msg.split( "%&%")[0];
			String order = msg.split("%&%")[1];

			switch (order){
				case "initConnection": {
					this.connectedUsers.put(uname,socket.getPort());
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
					this.connectedUsers.remove(uname);
					UserChatListener u = getFriend(uname);
					if(u != null){
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

                    setChanged();
                    notifyObservers(new Signal(Type.DISCONNECT, uname));
					printUsers();
					break;
				case "connected":
//                    System.out.println("getPort() = [" + socket.getRemoteSocketAddress().toString() + "], getLocalPort = [" + socket.getLocalPort() + "]");
					this.connectedUsers.put(uname,socket.getPort());
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					scanUsers();

                    setChanged();
                    notifyObservers(new Signal(Type.CONNECT, uname));
					printUsers();
					break;
				default:
					break;
			}
		}
	}

	public void scanUsers(){
		for (int i = portStart; i < portEnd; i++) {

			if(i!= server_port && !connectedUsers.containsValue(i)){
				try {
					Socket socket = new Socket("localhost",i);
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

				}
				catch (SocketTimeoutException e){
					System.out.println("timeout");
				}
				catch (ConnectException e) {
//                    System.out.println("not opened");//e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
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

	public boolean isUsed(String nickname) {
		scanUsers();

		boolean isUsed = connectedUsers.containsKey(nickname);
		return isUsed;
	}

	public boolean getWork() {
		return work;
	}

	public void setWork(boolean work) {
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
		try {
			serverSocket = new ServerSocket(server_port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.clientName = clientName;


		this.sendUpdateToConnected(clientName+"%&%"+"connected");
		printUsers();
	}

	public void printUsers(){
		Set<Map.Entry<String, Integer>> setHm = connectedUsers.entrySet();
		Iterator<Map.Entry<String, Integer>> it = setHm.iterator();

		while(it.hasNext()){

			Map.Entry<String, Integer> e = it.next();

			System.out.println("username = [" + e.getKey() + "], port = [" + e.getValue() + "]");

		}
	}

    public Vector<UserChatListener> getFriendList() {
        return friendList;
    }

    public Vector<String> getConnectedUsersName(){
	    Vector<String> res = new Vector<>();
        Set<Map.Entry<String, Integer>> setHm = connectedUsers.entrySet();
        Iterator<Map.Entry<String, Integer>> it = setHm.iterator();

        while(it.hasNext()){

            Map.Entry<String, Integer> e = it.next();
            res.add(e.getKey());
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
	    if(uname != null){
	        l.addObserver(observer);
        }
    }
}