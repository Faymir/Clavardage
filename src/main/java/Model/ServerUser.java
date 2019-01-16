package Model;

import org.json.JSONObject;

import java.io.Serializable;

public class ServerUser implements Comparable<ServerUser>, Serializable {
    private String ip = "";
    private boolean online = true;
    private String username = "";
    private String uniqueId= "";

    public ServerUser(String ip, boolean online, String username, String uniqueId) {
        this.ip = ip;
        this.online = online;
        this.username = username;
        this.uniqueId = uniqueId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }



    public static ServerUser fromJsonObject(JSONObject obj){
        return new ServerUser(obj.getString("ip"),
                obj.getBoolean("online"),
                obj.getString("username"),
                obj.getString("uniqueId"));
    }

    @Override
    public int compareTo(ServerUser serverUser) {
        return username.compareTo(serverUser.username);
    }

}
