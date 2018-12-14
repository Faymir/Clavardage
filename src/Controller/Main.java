package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import Network.ConnexionManager;

public class Main extends Application {
    
	
	/*Scanner sc = new Scanner(System.in);
    int randomNum = ThreadLocalRandom.current().nextInt(0, 1000);
    ConnexionManager server = new ConnexionManager();
    String username = "";
    boolean bool = true;
    while(bool) {
        System.out.print("\nEnter your username: ");
        username = sc.nextLine();
        bool = server.isUsed(username);
        if(bool)
            System.out.println("This Username is already taken, choose an other one");
    }
    System.out.println("Username is valide");
    server.setClientName(username);
    server.start();
    String msg = "";

    while(msg != "exit\n"){
        msg = sc.nextLine();
        if(msg.contains(":")) {
            String uname = msg.split(":")[0];
            msg = msg.split(":")[1];
            if(!server.sendMessage(uname, msg))
                System.out.println( uname + " Not Sended");
            //server.printUsers();
        }
    }
    sc.close(); */

	@Override
    public void start(Stage primaryStage) throws Exception {
		
		
		
        FXMLLoader root = new FXMLLoader(getClass().getResource("../View/template.fxml"));
        
        MainController mainController = new MainController();
        UsersList usersList = new UsersList();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
        String timeStamp = dateFormat.format(Calendar.getInstance().getTime());


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
