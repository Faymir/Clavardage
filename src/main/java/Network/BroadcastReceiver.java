package Network;

import Model.ScanMessage;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.*;
import java.util.Observable;

/**
 * Observed only by NetworkScanner
 */
public class BroadcastReceiver extends Observable implements Runnable {
    protected DatagramSocket socket = null;
    protected byte[] buf = new byte[1024*1024];
    private ScanMessage msg;
    private boolean work = true;
    private boolean isForScanner = false;
    private String uniqueID;
    private String ownIp;


    public BroadcastReceiver(boolean isForScanner, String uiid){
        this(uiid);
        this.isForScanner = isForScanner;
    }

    public BroadcastReceiver(String uiid){
        uniqueID = uiid;
    }

    @Override
    public void run() {

        try {
            socket = new DatagramSocket(40446);
            if(isForScanner)
                socket.setSoTimeout(2000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (work) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            String ownAddress ="";
            try {

                socket.receive(packet);
                ownAddress = InetAddress.getLocalHost().getAddress().toString();
                msg = SerializationUtils.deserialize(packet.getData());

                if (msg.uniqueID.equals(uniqueID)) {
                    System.out.println("received own message ip = [" + msg.ip + "]  msg.type = ["+ msg.type.toString() + "]");
                    ownIp = msg.ip;
                }
                else {

//                    System.out.println("own id = [" + uniqueID + "]  msg.id = ["+ msg.uniqueID + "]");
//                    System.out.println("else ip = [" + msg.ip + "]  own = ["+ uniqueID + "]");

                    String received = msg.type.toString();
                    System.out.println("Broadcast Received [" + received + "] from [" + packet.getAddress().getHostAddress() + "]");
                    if(isForScanner){
                        this.stop();
                        socket.close();
                        socket = null;
                    }
                    msg.ip = packet.getAddress().getHostAddress();
                    setChanged();
                    notifyObservers(msg);
                }
            } catch (SocketTimeoutException e) {
                //Si c'est le premier utilisateur
                if(isForScanner) {
                    ScanMessage m = new ScanMessage(ScanMessage.ScanType.RETURN_INFORMATION);
                    m.uniqueID = uniqueID;
                    System.out.println("Datagram timeout ip = [" + m.ip + "]  id = [" + m.uniqueID + "]");
                    ownIp = m.ip;
                    socket.close();
                    socket = null;
                    this.stop();
                    setChanged();
                    notifyObservers(m);
                }
                else
                    System.out.println("Scanner datagram socket timeout");
            }  catch (IOException e) {
                e.printStackTrace();
                if(isForScanner){
                    this.stop();
                    socket.close();
                    socket = null;
                }
            }
        }
        if (socket != null)
            socket.close();
        System.out.println("Stopped listening. Thread ["+ Thread.currentThread().getName() + "] stopping");
    }

    public boolean isWorking() {
        return work;
    }

    public void setWorking(boolean work) {
        this.work = work;
    }

    public void stop(){
        this.work = false;
    }

    public void killSocket(){
        stop();
        if(this.socket != null)
            this.socket.close();
        socket = null;
    }

    public String getOwnIp() {
        return ownIp;
    }
}
