import Controller.ConnexionController;
import Controller.MainController;
import Model.FileLoader;
import Network.ConnexionManager;
import Network.GetNetworkAddress;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.NetworkInterface;

public class App extends Application {
    private ConnexionManager connexionManager;
    private Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        //Classes Init
        connexionManager = new ConnexionManager(ConnexionManager.ManagerMode.BROADCAST);
        primaryStage = stage;
        //UI INIT
        System.out.println("stage = [" + FileLoader.getInstance().get("template.fxml") + "]");
        FXMLLoader root = new FXMLLoader(FileLoader.getInstance().get("template.fxml"));
        MainController mainController = new MainController(connexionManager);
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
    public static void main(String[] args) {
        launch(args);

        //System.out.println("Mac = [" + GetNetworkAddress.GetAddress("mac") + "]");;
    }
}
