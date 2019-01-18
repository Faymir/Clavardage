package Network;

public class ConnManagerFactory {

    public static ConnexionManager getConnectionManager(Class type) throws UnsupportedClassException{
        if (type == BroadcastConnexionManager.class)
            return new BroadcastConnexionManager();
        else if (type == LocalConnexionManager.class)
            return new LocalConnexionManager();
        else
            throw new UnsupportedClassException("This class is not supported byt this Factory");
    }
}
