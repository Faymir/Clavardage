import java.io.IOException;

import Controller.ConnexionController;
import Controller.MainController;
import Model.FileLoader;
import Network.SharedObjects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        //Classes Init
        primaryStage = stage;
        //UI INIT

        System.out.println("App user.dir = [" + System.getProperty("user.dir") + "]");
        System.out.println("App user.dir = [" + getClass().getResource("resources/template.fxml") + "]");
        FXMLLoader root = new FXMLLoader(FileLoader.getInstance().get("template.fxml"));
        MainController mainController = new MainController(stage);
        root.setController(mainController);

        FXMLLoader connexionWidget = new FXMLLoader(FileLoader.getInstance().get("connexionWidget.fxml"));
        ConnexionController connexionController = new ConnexionController(root);

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
        if (SharedObjects.get().connManager != null){
            SharedObjects.get().connManager.setWork(false);
            SharedObjects.get().connManager.sendDisconnect();
            SharedObjects.get().connManager = null;
        }
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
