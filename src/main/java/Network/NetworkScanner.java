package Network;

import Model.ScanMessage;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class NetworkScanner extends Observable implements Observer{

    private BroadcastReceiver receiver;
    private String uniqueID ;

    public NetworkScanner(String uiid){
        uniqueID = uiid;
        receiver = new BroadcastReceiver(true,uniqueID);
        receiver.addObserver(this);
    }

    public void scanNetwork() throws IOException {
        List<InetAddress> list = listAllBroadcastAddresses();
        ScanMessage msg = new ScanMessage(ScanMessage.ScanType.DISCOVER);
        msg.uniqueID = uniqueID;
        (new Thread(receiver)).start();
        byte[] data = SerializationUtils.serialize(msg);
        for (InetAddress addr : list) {
            broadcast(data,addr);
        }
    }

    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }

    public static void broadcast(byte [] buffer, InetAddress address) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 40446);
        socket.send(packet);
        socket.close();
    }

    public static void broadcastToAll(byte [] data) throws IOException {
        List<InetAddress> list = listAllBroadcastAddresses();
        for (InetAddress addr : list) {
            broadcast(data,addr);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass() == BroadcastReceiver.class){
            ScanMessage msg = (ScanMessage) o;
            if(msg.type != ScanMessage.ScanType.RETURN_INFORMATION) {
                System.out.println("WRONG MESSAGE RECEIVED FROM OTHERS, STILL WAITING FOR RESPONSE");
                System.out.println("msg.type = [" + msg.type + "], o = [" + msg + "]");
                return;
            }
            this.receiver.stop();
            setChanged();
            notifyObservers(o);
        }
    }

    public void stopAll(){
        receiver.stop();
        receiver.killSocket();
    }
}
