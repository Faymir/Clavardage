package Controller;

import Model.Database;
import Model.Signal;
import Model.Type;
import Network.ConnexionManager;
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

    private ConnexionManager connManager;
    private FXMLLoader mainWindow;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField usernameTextEdit;

    @FXML
    private CheckBox modeCheckBox;

    @FXML
    private Label errorLabel;

    public ConnexionController(ConnexionManager connexionManager, FXMLLoader mainWindow){
        connManager = connexionManager;
        this.mainWindow = mainWindow;
    }

    @FXML
    public void initialize(){

        System.out.println("Initialize connexion window");
        this.modeCheckBox.setSelected(true);
    }

    @FXML
    void cancelClicked(ActionEvent event) {

        progressBar.setProgress(0);
    }

    @FXML
    void usernameKeyReleased(KeyEvent event){
        if(event.getCode().getName().equals("Enter"))
            validate();
    }

    @FXML
    void validateClicked(ActionEvent event) {
        validate();
    }
    @FXML
    void checkBoxClicked(ActionEvent event) {
        connManager.setMode((modeCheckBox.isSelected())? ConnexionManager.ManagerMode.BROADCAST: ConnexionManager.ManagerMode.TEST);
        System.out.println("event = [" + event + "] value = [" + modeCheckBox.isSelected() + "] mode = [" + connManager.getMode() + "]");
    }

    private void validate(){
        String username = usernameTextEdit.getText();

        if(username.isEmpty())
            return;
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        usernameTextEdit.setDisable(true);
        connManager.addObserver(this);
        connManager.isUsed(username);
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass() == ConnexionManager.class) {
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
                connManager.setClientName(username);
                (new Thread(connManager)).start();
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
