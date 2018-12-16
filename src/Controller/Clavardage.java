package Controller;

import Network.ConnexionManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Clavardage extends Application {
    private ConnexionManager connexionManager;
    private Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        //Classes Init
        connexionManager = new ConnexionManager();
        primaryStage = stage;
        //UI INIT

        FXMLLoader root = new FXMLLoader(getClass().getResource("../View/template.fxml"));
        MainController mainController = new MainController(connexionManager);
        root.setController(mainController);

        FXMLLoader connexionWidget = new FXMLLoader(getClass().getResource("../View/connexionWidget.fxml"));
        ConnexionController connexionController = new ConnexionController(connexionManager, root);
        UsersList usersList = new UsersList();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
        String timeStamp = dateFormat.format(Calendar.getInstance().getTime());
        System.out.println(timeStamp);


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

        connexionWidget.setControllerFactory(controllerFactory);
        Scene connScene = new Scene(connexionWidget.load(),492,285);


        primaryStage.setTitle("Clavardage");
        primaryStage.setOnCloseRequest(windowEvent -> {
            System.out.println("Close request");
            connexionManager.setWork(false);
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
        System.out.println("Working: " + connexionManager.getWork());
        connexionManager = null;
        primaryStage.close();
        primaryStage = null;
        System.exit(0);
        // Save file
    }
    public static void main(String[] args) {
        launch(args);
    }
}
