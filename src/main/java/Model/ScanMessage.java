package Model;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class ScanMessage implements Serializable {
    public enum ScanType implements Serializable{
        DISCOVER("DISCOVERING NETWORK"),
        RETURN_INFORMATION("Return clients information to new user with is version number"),
        UPDATE_INFORMATION("Return new user information"),
        DISCONNECT("This user Disconnected"),
        CHANGE_USER_NAME("This user changed his username");

        private String msg;

        private ScanType(String msg){
            this.msg = msg;
        }
    }

    public ScanType type;
    public HashMap<String, String> clients;
    public int newUserVersion;
    public String newUsername;
    public String ip;
    public String uniqueID;
    public String oldUsername;

    public ScanMessage(ScanType type) {
        this.type = type;
        clients = new HashMap<>();
        newUserVersion = 0;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("255.255.255.255"), 30000);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ScanMessage(ScanType type, String newUsername) {
        this(type);
        this.newUsername = newUsername;
    }

    public ScanMessage(ScanType type, HashMap<String, String> clients, int newUserVersion) {
        this(type);
        this.clients = clients;
        this.newUserVersion = newUserVersion;
    }

    public ScanMessage(ScanType type, int newUserVersion, String newUsername) {
        this(type);
        this.newUserVersion = newUserVersion;
        this.newUsername = newUsername;
    }
}
