package Network;

public class SharedObjects {
    private static SharedObjects ourInstance = new SharedObjects();


    public ConnexionManager connManager = null;
    public String serverIp = null;

    public static SharedObjects get() {
        return ourInstance;
    }

    private SharedObjects() {
    }
}
