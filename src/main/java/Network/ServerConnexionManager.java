package Network;

import java.io.IOException;
import java.net.Socket;

public class ServerConnexionManager extends ConnexionManager<String> {
    public ServerConnexionManager() {
        super(ManagerMode.SERVER);
    }

    @Override
    protected void sendUpdateInformation(String str) {

    }

    @Override
    protected void handleConnected(String uname, Socket socket) {

    }

    @Override
    protected void handleDisconnect(String uname, Socket socket, String msg) {

    }

    @Override
    protected void handleInitConnexion(String uname, Socket socket) {

    }

    @Override
    protected void scanUsers() {

    }

    @Override
    protected Socket newSocket(String value) throws IOException {
        return null;
    }

    @Override
    public void connect(String nickname) {

    }
}
