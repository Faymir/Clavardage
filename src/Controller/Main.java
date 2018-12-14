package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader root = new FXMLLoader(getClass().getResource("../View/template.fxml"));

        MainController mainController = new MainController();
        UsersList usersList = new UsersList();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
        String timeStamp = dateFormat.format(Calendar.getInstance().getTime());
        System.out.println(timeStamp);


        Callback<Class<?>, Object> controllerFactory = type -> {
            if (type == MainController.class) {
                return mainController ;
            } else {
                // default behavior for controllerFactory:
                try {
                    return type.newInstance();
                } catch (Exception exc) {
                    exc.printStackTrace();
                    throw new RuntimeException(exc); // fatal, just bail...
                }
            }
        };

        root.setControllerFactory(controllerFactory);

        primaryStage.setTitle("Clavardage");
        primaryStage.setScene(new Scene(root.load(),800,600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
