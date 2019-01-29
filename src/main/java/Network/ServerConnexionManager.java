package Network;

import Model.ServerUser;
import Model.Signal;
import Model.Type;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.MalformedParametersException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * subscribe 	=> to the server
 * publish		=> their status
 * notify		=> in case of any changes
 *
 */

public class ServerConnexionManager extends ConnexionManager<String> {

    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private int unreachableServer = -1;

    public ServerConnexionManager() {
        super(ManagerMode.SERVER);
        serverUrl = "http://node6669-clavadage.jcloud.ik-server.com/webapi/";
        server_port = 11000;
        clientName = null;
    }

    @Override
    protected void sendUpdateInformation(String str) {

    }

    @Override
    protected void sendUserNameChanged(String newUsername) {
        //TODO: change Username
    }

    @Override
    protected void handleConnected(String uname, Socket socket) {

    }

    @Override
    protected void handleDisconnect(String uname, Socket socket, String msg) {

    }

    @Override
    protected void handleInitConnexion(String uname, Socket socket) {
        this.connectedUsers.put(uname, socket.getInetAddress().getAddress().toString());
    }

    @Override
    protected void scanUsers() {

    }

    @Override
    protected Socket newSocket(String value) throws IOException {
        return new Socket(value, 11000);
    }

    @Override
    public void connect(String nickname) {

        JSONObject response = getResponse("subscribe", "username", nickname);
        String responseType = response.get("Type").toString();

        
        if(responseType.equals(Type.GOOD_USERNAME.toString())){
        	System.out.println("my uniqueId = [" + response.getString("uniqueId") + "]");
            this.uniqueID = response.getString("uniqueId");
            JSONArray users = response.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                ServerUser u = ServerUser.fromJsonObject(users.getJSONObject(i));
                if(!u.getUsername().equals(nickname) && u.isOnline())
                    connectedUsers.put(u.getUsername(), u.getIp());
            }
            ses.scheduleWithFixedDelay(this::publish,0, 2000, TimeUnit.MILLISECONDS);
            setChanged();
            notifyObservers(new Signal(Type.GOOD_USERNAME, ""));
        }
        else if(responseType.equals(Type.BAD_USERNAME.toString())){
            //ses.scheduleWithFixedDelay(this::publish,0, 2000, TimeUnit.MILLISECONDS);
            setChanged();
            notifyObservers(new Signal(Type.BAD_USERNAME, ""));
        }
    }

    private void publish(){
        if(clientName == null)
            return;
        JSONObject response = null;
        try {
            JSONObject obj = new JSONObject();
            obj.put("uniqueId", this.uniqueID);
            response = this.post("publish", obj.toString());
        }
        catch (java.net.ConnectException e){
            System.out.println("Server is unreachable, please contact Clavardage Application Administrator");

            if(unreachableServer == 10){
                for (UserChatListener u: friendList){
                    setChanged();
                    notifyObservers(new Signal(Type.DISCONNECT, u.getNickname()));
                }
            }
            unreachableServer++;
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (unreachableServer > 0){
            unreachableServer = 0;
            System.out.println("Server is Back Online");
        }
        JSONArray users = response.getJSONArray("users");
        if (users == null){
            System.out.println("Error when trying to publish: no data returned!");
            return;
        }
        for (int i = 0; i < users.length(); i++) {
            users.getJSONObject(i);
            ServerUser u = ServerUser.fromJsonObject(users.getJSONObject(i));
            if(!u.getUsername().equalsIgnoreCase(clientName)){ //Si username != clientName =>  c'est un ami
                //TODO: add support of client ip address changing (we suppose here that the friend ip doesn't change
                if(connectedUsers.containsKey(u.getUsername())){
                    if(!u.isOnline()){
                        connectedUsers.remove(u.getUsername());
                        friendList.removeIf( friend -> friend.getNickname().equals(u.getUsername()) );
                        printUsers();
                        setChanged();
                        notifyObservers(new Signal(Type.DISCONNECT, u.getUsername()));
                    }
                }
                else if(u.isOnline() && !u.getUsername().equals(clientName)){
                    connectedUsers.put(u.getUsername(), u.getIp());
                    printUsers();
                    setChanged();
                    notifyObservers(new Signal(Type.CONNECT, u.getUsername()));
                }
            }
        }
    }

    private JSONObject getResponse(String url, String param, String value){
        JSONObject response = null;
        try {
            response = this.get(url, new String[]{param}, new String[]{value});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private JSONObject get(String url, String[] params, String[] values) throws MalformedParametersException, IOException {
        StringBuilder sb = new StringBuilder();
        if (params.length != values.length)
            throw new MalformedParametersException("params and values must have the same size");

        sb.append(serverUrl).append(url);
        if(params.length != 0) {
            sb.append("?");
            for (int i = 0; i < params.length; i++) {
                if(!params[i].isEmpty()){
                    if (i == 0)
                        sb.append(params[i]).append("=").append(values[i]);
                    else
                        sb.append("&").append(params[i]).append("=").append(values[i]);
                }
            }
        }

        URL server = new URL(sb.toString());
        URLConnection conn = server.openConnection();
        String inputLine = getInputString(conn.getInputStream(), "get");
        JSONObject obj =new JSONObject(inputLine);
        return obj;
    }

    private JSONObject post(String uri, String data) throws IOException {
        URL server = new URL(serverUrl + uri);
        URLConnection con = server.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);
        byte[] out = data.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
        // Do something with http.getInputStream()
        String inputLine = getInputString(http.getInputStream(), "post");
        if (inputLine.contains("error"))
            System.out.println("Error = [" + inputLine + "]");
        return new JSONObject(inputLine);
    }

    private String getInputString(InputStream inputStream, String request) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder result = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
        }

        if(!"post".equals(request))
            System.out.println(request + ":\t" + result);
        in.close();
        return result.toString();
    }

    @Override
    public void disconnect() {

    }
}
