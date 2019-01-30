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
            mainController.saveData();
            Platform.exit();
        });
        primaryStage.setScene(connScene);
        primaryStage.show();


    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        if (SharedObjects.get().connManager != null){
            SharedObjects.get().connManager.setWork(false);
            SharedObjects.get().connManager = null;
        }
        primaryStage.close();
        primaryStage = null;
        System.exit(0);
        // Save file
    }
    public static void main(String[] args) throws Exception{
        launch(args);
    }
}
