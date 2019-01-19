package Controller;

import Model.Database;
import Model.Signal;
import Model.Type;
import Network.*;
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class ConnexionController implements Observer
{

    private FXMLLoader mainWindow;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField usernameTextEdit;

    @FXML
    private Label errorLabel;

    @FXML
    private RadioButton testRadioButton;

    @FXML
    private RadioButton broadcastRadioButton;

    @FXML
    private RadioButton serverRadioButton;

    public ConnexionController(FXMLLoader mainWindow){
        this.mainWindow = mainWindow;
    }

    @FXML
    public void initialize(){

        System.out.println("Initialize connexion window");
        testRadioButton.setSelected(false);
        broadcastRadioButton.setSelected(true);
        serverRadioButton.setSelected(false);
    }

    @FXML
    void cancelClicked(ActionEvent event) {

        progressBar.setProgress(0);
    }

    @FXML
    void usernameKeyReleased(KeyEvent event) throws UnsupportedClassException{
        if(event.getCode().getName().equals("Enter"))
            validate();
    }

    @FXML
    void validateClicked(ActionEvent event) throws UnsupportedClassException {
        validate();
    }

    @FXML
    void radioToogled(ActionEvent event){
        testRadioButton.setSelected(false);
        broadcastRadioButton.setSelected(false);
        serverRadioButton.setSelected(false);
        switch (((RadioButton)event.getSource()).getText()){
            case "Test":
                testRadioButton.setSelected(true);
                break;
            case "Broadcast":
                broadcastRadioButton.setSelected(true);
                break;
            case "Server":
                serverRadioButton.setSelected(true);
                break;
            default:
                break;
        }
        //System.out.println("event = [" + ((RadioButton)event.getSource()).getText() + "]");
    }

    private void validate() throws UnsupportedClassException {

        if (testRadioButton.isSelected())
            SharedObjects.get().connManager = ConnManagerFactory.getConnectionManager(LocalConnexionManager.class);
        else if (broadcastRadioButton.isSelected())
            SharedObjects.get().connManager = ConnManagerFactory.getConnectionManager(BroadcastConnexionManager.class);
        else if(serverRadioButton.isSelected())
            SharedObjects.get().connManager = ConnManagerFactory.getConnectionManager(ServerConnexionManager.class);

        String username = usernameTextEdit.getText();

        if(username.isEmpty())
            return;
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        usernameTextEdit.setDisable(true);
        SharedObjects.get().connManager.addObserver(this);
        SharedObjects.get().connManager.connect(username);
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass() == BroadcastConnexionManager.class
                || observable.getClass() == LocalConnexionManager.class
                || observable.getClass() == ServerConnexionManager.class) {
            usernameTextEdit.setDisable(false);
            Signal s = (Signal) o;
            if (s.type == Type.BAD_USERNAME) {
                System.out.println("s = [" + s.type + "]");
                Platform.runLater(
                        () -> {
                            progressBar.setProgress(0);
                            errorLabel.setStyle("-fx-text-fill: black; -fx-background-color: red;");
                            errorLabel.setText("This Username is already taken, choose an other one");
                        }
                );
            } else if (s.type == Type.GOOD_USERNAME) {
                System.out.println("s = [" + s.type + "]");
                String username = usernameTextEdit.getText();
                Database.getInstance().initUser(username);
                Platform.runLater(
                        () -> {
                            // Update UI here.
                            progressBar.setProgress(0);
                            errorLabel.setStyle("-fx-text-fill: black; -fx-background-color: #4cd137");
                            errorLabel.setText("Username is valid");
                        }
                );
                SharedObjects.get().connManager.setClientName(username);
                (new Thread(SharedObjects.get().connManager)).start();
                Stage stage = (Stage) (usernameTextEdit).getScene().getWindow();
                Platform.runLater(
                            () -> {
                                try {
                                    Scene scene = new Scene(mainWindow.load(),850,600);
                                    //scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
                                    stage.setScene(scene);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
            }
        }
    }
}
