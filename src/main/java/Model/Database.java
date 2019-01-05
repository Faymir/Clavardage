package Model;

import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.sql.*;
import java.util.Vector;

public class Database {
    private static final String defaultDbName = "database.sqlite";
    private static Database instance = null;
    private static String url = "";

    private Database(){
        File f = new File(System.getProperty("user.dir") + File.separator + defaultDbName);
        if(!f.exists() || f.isDirectory()) {
            createNewDatabase();
        }
        setDbName(defaultDbName);
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }
    public static void createNewDatabase() {

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createFriendsTable() {

        // SQL statement for creating a new table
        String sql =
                "CREATE TABLE IF NOT EXISTS friends (\n"
                + "	name VARCHAR(50) PRIMARY KEY,\n"
                + "	ip VARCHAR(50) NOT NULL,\n"
                + "	messages BLOB,\n"
                + " last_message_index int not null default 0\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectAll(){
        String sql = "SELECT name, ip, messages FROM friends";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("name") +  "\t" +
                        rs.getString("ip") + "\t" +
                        rs.getInt("last_message_index") + "\t" +
                        rs.getBytes("messages"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public User getUser(String name){
        String sql = "SELECT name, ip, messages, last_message_index "
                + "FROM friends WHERE name = ?";

        User u = null;
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,name);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                u = new User(name);
                byte[] data = rs.getBytes("messages");
                u.setDiscussion(SerializationUtils.deserialize(data));
                u.setLastMessageIndex(rs.getInt("last_message_index"));
                System.out.println(rs.getString("name") +  "\t" +
                        rs.getString("ip") + "\t" +
                        rs.getInt("last_message_index") + "\t" +
                        data.toString());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return u;
    }

    public Object getbyName(String username, String column){
        String sql = "SELECT " + column
                + " FROM friends WHERE name = ?";

        Object result = null;
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,username);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                System.out.println("username = [" + username + "], column = [" + column + "]");
                if (column.equalsIgnoreCase("ip"))
                    result = rs.getString("ip");
                else if (column.equalsIgnoreCase("last_message_index"))
                    result = rs.getInt("last_message_index");
                else if (column.equalsIgnoreCase("messages")){
                    System.out.println("is message");
                    byte[] blob = rs.getBytes("messages");
                    result = SerializationUtils.deserialize(blob);
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean checkExist(String friendName){
        String sql = "SELECT ROWID from friends where name = ?";

        Object result = null;
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,friendName);
            //
            ResultSet rs  = pstmt.executeQuery();
            if (rs.next())
                return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public void insert(User u) {
        String sql = "INSERT INTO friends(name, ip, messages, last_message_index) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getNickname());
            pstmt.setString(2, "");
            pstmt.setBytes(3, SerializationUtils.serialize(u.getDiscussion()));
            pstmt.setInt(4, u.getLastMessageIndex());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void setDbName(String name) {
        String current = System.getProperty("user.dir");
        Database.url = "jdbc:sqlite:" + current + File.separator + name;
    }

    public void update(String username, User user) {
        String sql = "UPDATE friends SET name = ?, ip = ?, messages = ?, last_message_index = ? WHERE name = ? ";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, "");
            pstmt.setBytes(3, SerializationUtils.serialize(user.getDiscussion()));
            pstmt.setInt(4, user.getLastMessageIndex());
            pstmt.setString(5, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}