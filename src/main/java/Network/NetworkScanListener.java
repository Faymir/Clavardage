package Network;

import Model.ScanMessage;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class NetworkScanListener extends Observable implements Observer {
    private BroadcastReceiver receiver;
    private HashMap<String, String> connectedUsers;
    private Vector<Integer> versions;
    private int versionNumber;
    private int lastVersion;
    private String uname;
    private String uniqueID;

    public NetworkScanListener(String username, String uiid){
        uniqueID = uiid;
        this.uname = username;
        versionNumber = 0;
        lastVersion = 0;
        receiver = new BroadcastReceiver(uniqueID);
        receiver.addObserver(this);
        connectedUsers = new HashMap<>();
        versions = new Vector<>();
    }

    public NetworkScanListener(String username, HashMap<String, String> connectedUsers, String uiid){
        this(username,uiid);
        this.connectedUsers = (HashMap<String, String>) connectedUsers.clone();
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("255.255.255.255"), 30000);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connectedUsers.put(username, ip);
    }


    public NetworkScanListener(String username, HashMap<String, String> connectedUsers, int versionNumber, int lastVersion, String uiid){
        this(username, connectedUsers, uiid);
        this.versionNumber = versionNumber;
        this.lastVersion = lastVersion;
    }

    public void listen(){
        (new Thread(receiver)).start();
    }

    public void stopListening(){
        this.receiver.stop();
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass() == BroadcastReceiver.class){
            handleScanMsg((ScanMessage) o);
        }
    }

    public void handleScanMsg(ScanMessage msg){
        System.out.println("msg ip = [" + msg.ip + "]");
        switch (msg.type){
            case DISCOVER:
                if(versionNumber == lastVersion){


                    if(!connectedUsers.containsKey(uname))
                        connectedUsers.put(uname,receiver.getOwnIp());

                    Set<Map.Entry<String, String>> setHm = connectedUsers.entrySet();
                    Iterator<Map.Entry<String, String>> it = setHm.iterator();

                    while (it.hasNext()) {

                        Map.Entry<String, String> e = it.next();

                        System.out.println("listener username = [" + e.getKey() + "], ip = [" + e.getValue() + "]");

                    }
                    ScanMessage message = new ScanMessage(
                            ScanMessage.ScanType.RETURN_INFORMATION,
                            connectedUsers,
                            lastVersion + 1);
                    message.uniqueID = uniqueID;
                    System.out.println("\n\nI WAS THE RESPONDER OF BROADCAST MESSAGES [" + lastVersion + "]\n\n");
                    System.out.println("return info " + connectedUsers.size() + " to ip[ " + msg.ip + "]");
                    try {
                        NetworkScanner.broadcast(SerializationUtils.serialize(message), InetAddress.getByName(msg.ip));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case RETURN_INFORMATION:
                System.out.println("THIS MESSAGE SHOULD NEVER SHOW(NetworkScanner Handle Msg): msg = [" + msg + "]");
                break;
            case UPDATE_INFORMATION:
                connectedUsers.put(msg.newUsername, msg.ip);
                lastVersion = msg.newUserVersion;
//                HashMap<String, String> second = (HashMap<String, String>) connectedUsers.clone();
//                second.remove(uname);
                if(!msg.uniqueID.equals(uniqueID)) {
                    versions.add(msg.newUserVersion);
                    setChanged();
                    notifyObservers(msg);
                }
                break;
            case DISCONNECT:
                versions.removeIf( a -> a == msg.newUserVersion);
                boolean responder = false;

                if(versions.size() > 0){
                    int max = Collections.max(versions);
                    if (versionNumber > max)
                        responder = true;
                }
                else
                    responder = true;

                if (responder){
                    System.out.println("\n\n\n\t\t I SHOULD BE THE NEW RESPONDER \n\n");
                    this.versionNumber = lastVersion;
                }
                connectedUsers.remove(msg.newUsername);
                if(!msg.uniqueID.equals(uniqueID)) {
                    setChanged();
                    notifyObservers(msg);
                }
                break;
            default:
                System.out.println("THIS MESSAGE SHOULD NEVER SHOW(NetworkScanner Handle Msg default switch): msg = [" + msg + "]");
                break;
        }
    }

    public HashMap<String, String> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(HashMap<String, String> connectedUsers) {
        this.connectedUsers = connectedUsers;
        try {
            connectedUsers.put(uname, InetAddress.getLocalHost().getAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public int getVersionNumber() {
        return versionNumber;
    }
}
