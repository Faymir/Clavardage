import Controller.ConnexionController;
import Controller.MainController;
import Model.FileLoader;
import Model.ServerUser;
import Model.Type;
import Network.ConnexionManager;
import Network.GetNetworkAddress;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

public class App extends Application {
    private ConnexionManager connexionManager;
    private Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        //Classes Init
        connexionManager = new ConnexionManager(ConnexionManager.ManagerMode.BROADCAST);
        primaryStage = stage;
        //UI INIT
        FXMLLoader root = new FXMLLoader(FileLoader.getInstance().get("template.fxml"));
        MainController mainController = new MainController(connexionManager, stage);
        root.setController(mainController);

        FXMLLoader connexionWidget = new FXMLLoader(FileLoader.getInstance().get("connexionWidget.fxml"));
        ConnexionController connexionController = new ConnexionController(connexionManager, root);


        Callback<Class<?>, Object> controllerFactory = type -> {
            if (type == MainController.class) {
                return mainController ;
            } else if(type == ConnexionController.class) {
                return connexionController;
            }else {
                // default behavior for controllerFactory:
                try {
                    return type.newInstance();
                } catch (Exception exc) {
                    exc.printStackTrace();
                    throw new RuntimeException(exc); // fatal, just bail...
                }
            }
        };

        connexionWidget.setController(connexionController);
        Parent p = null;
        try{
            p = connexionWidget.load();
        }
        catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        Scene connScene = new Scene(p,492,285);


        primaryStage.setTitle("Clavardage");
        primaryStage.setOnCloseRequest(windowEvent -> {
            System.out.println("Close request");
            mainController.saveData();
            //connexionManager.setWork(false);
            Platform.exit();
        });
        primaryStage.setScene(connScene);
//        primaryStage.setScene(new Scene(root.load(),800,600));
        primaryStage.show();


    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        connexionManager.setWork(false);
        connexionManager.sendDisconnect();
        connexionManager = null;
        primaryStage.close();
        primaryStage = null;
        System.exit(0);
        // Save file
    }
    public static void main(String[] args) throws Exception{
        launch(args);
//        URL server = new URL("http://localhost:8080/myapp/subscribe?username=Faya2");
//        URLConnection yc = server.openConnection();
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(
//                        yc.getInputStream()));
//        String inputLine;
//        StringBuilder result = new StringBuilder();
//        while ((inputLine = in.readLine()) != null) {
//            result.append(inputLine);
//        }
//
//        System.out.println(result);
//        in.close();
//
//
//        JSONObject obj = new JSONObject(result.toString());
//        String response = obj.get("Type").toString();
////        String cstr = "rO0ABXNyABBqYXZhLnV0aWwuVmVjdG9y2Zd9W4A7rwEDAANJABFjYXBhY2l0eUluY3JlbWVudEkADGVsZW1lbnRDb3VudFsAC2VsZW1lbnREYXRhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwAAAAAAAAAAJ1cgATW0xqYXZhLmxhbmcuT2JqZWN0O5DOWJ8QcylsAgAAeHAAAAAKc3IAGmZyLmZheW1pci5Nb2RlbC5TZXJ2ZXJVc2VyO9WqMvOHxB0CAARaAAZvbmxpbmVMAAJpcHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wACHVuaXF1ZUlkcQB+AAZMAAh1c2VybmFtZXEAfgAGeHABdAAJMTI3LjAuMC4xdAAkMjA1NTIyYzMtN2I0Zi00NzRlLThjN2QtNTM0YjJjNzk5YmVjdAAERmF5YXNxAH4ABQF0AAkxMjcuMC4wLjF0ACQwMmFiMDIyZC0yNWY5LTRiOWMtOTY1My05MTEyNThmMGJlNzV0AAVGYXlhMnBwcHBwcHBweA==";
////        String sstr = "rO0ABXNyABBqYXZhLnV0aWwuVmVjdG9y2Zd9W4A7rwEDAANJABFjYXBhY2l0eUluY3JlbWVudEkADGVsZW1lbnRDb3VudFsAC2VsZW1lbnREYXRhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwAAAAAAAAAAJ1cgATW0xqYXZhLmxhbmcuT2JqZWN0O5DOWJ8QcylsAgAAeHAAAAAKc3IAGmZyLmZheW1pci5Nb2RlbC5TZXJ2ZXJVc2VyO9WqMvOHxB0CAARaAAZvbmxpbmVMAAJpcHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wACHVuaXF1ZUlkcQB+AAZMAAh1c2VybmFtZXEAfgAGeHABdAAJMTI3LjAuMC4xdAAkMjA1NTIyYzMtN2I0Zi00NzRlLThjN2QtNTM0YjJjNzk5YmVjdAAERmF5YXNxAH4ABQF0AAkxMjcuMC4wLjF0ACQwMmFiMDIyZC0yNWY5LTRiOWMtOTY1My05MTEyNThmMGJlNzV0AAVGYXlhMnBwcHBwcHBweA==";
////        System.out.println("Compare = [" + cstr.equals(sstr) + "]");
//        if(response.equals(Type.GOOD_USERNAME.toString())){
//            JSONArray users = obj.getJSONArray("users");
//            Vector<ServerUser> serverUsers = new Vector<>();
//            for (int i = 0; i < users.length(); i++) {
//                users.getJSONObject(i);
//                ServerUser u = ServerUser.fromJsonObject(users.getJSONObject(i));
//                if(u.getUsername().equalsIgnoreCase("Faya2"))
//                    System.out.println("my uniqueId = [" + u.getUniqueId() + "]");
//                else
//                    serverUsers.add(u);
//            }
//        }
//
//        System.out.println("Type = " + obj.get("Type").toString().equals(Type.BAD_USERNAME.toString()));
//        //System.out.println("Mac = [" + GetNetworkAddress.GetAddress("mac") + "]");;
//        System.exit(0);
    }
}
