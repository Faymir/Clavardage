package Network;

import Model.ScanMessage;
import Model.Signal;
import Model.Type;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

public class BroadcastConnexionManager extends ConnexionManager<String> {
    protected String tmpClientName = null;
    protected NetworkScanner networkScanner;
    protected NetworkScanListener networkScanListener;

    public BroadcastConnexionManager() {
        super(ManagerMode.BROADCAST);
    }

    @Override
    protected void init(){
        super.init();
        networkScanner = new NetworkScanner(uniqueID);
        networkScanner.addObserver(this);
        networkScanListener = null;
        server_port = 11000;

    }

    @Override
    protected void sendUpdateInformation(String str){
        ScanMessage msg = new ScanMessage(ScanMessage.ScanType.UPDATE_INFORMATION,networkScanListener.getVersionNumber(),clientName);
        msg.uniqueID = uniqueID;
        try {
            NetworkScanner.broadcastToAll(SerializationUtils.serialize(msg));
            System.out.println("\n\nI AM THE NEW RESPONDER OF BROADCAST MESSAGES [" + networkScanListener.getVersionNumber() + "]\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleInitConnexion(String uname, Socket socket) {
        this.connectedUsers.put(uname, socket.getInetAddress().getAddress().toString());
    }

    @Override
    protected void handleConnected(String uname, Socket socket) {}

    @Override
    protected void handleDisconnect(String uname, Socket socket, String msg) {}

    @Override
    protected void scanUsers() {
        System.out.println("\t\tScan Users");
        try {
            networkScanner.scanNetwork();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Socket newSocket(String value) throws IOException {
        return new Socket(value, 11000);
    }

    @Override
    public void connect(String nickname) {
        tmpClientName = nickname;
        try {
            networkScanner = new NetworkScanner(uniqueID);
            networkScanner.addObserver(this);
            networkScanner.scanNetwork();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if(this.networkScanListener != null && !work){
            ScanMessage msg = new ScanMessage(ScanMessage.ScanType.DISCONNECT,networkScanListener.getVersionNumber(),clientName);
            msg.uniqueID = uniqueID;
            try {
                NetworkScanner.broadcastToAll(SerializationUtils.serialize(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        super.update(observable, o);
        if(observable.getClass() == NetworkScanner.class){
            //recu message de type Return Information
            ScanMessage msg = (ScanMessage) o;

            if(msg.uniqueID.equals(this.uniqueID) || !msg.clients.containsKey(tmpClientName)) {
                this.connectedUsers = (HashMap<String, String>) msg.clients.clone();
                if(msg.newUsername!=null && !this.tmpClientName.equals(msg.newUsername))
                    this.connectedUsers.put(msg.newUsername, msg.ip);
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
                connectedUsers.put(msg.newUsername, msg.ip);
                printUsers();
                setChanged();
                notifyObservers(new Signal(Type.CONNECT, msg.newUsername));
            }
            if (msg.type == ScanMessage.ScanType.DISCONNECT){
                connectedUsers.remove(msg.newUsername);
                friendList.removeIf( friend -> friend.getNickname().equals(msg.newUsername) );
                printUsers();
                setChanged();
                notifyObservers(new Signal(Type.DISCONNECT, msg.newUsername));
            }
        }
    }
}
