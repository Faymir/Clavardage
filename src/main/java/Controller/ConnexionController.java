package Controller;

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
                Platform.runLater(
                        () -> {
                            errorLabel.setStyle("-fx-text-fill: red");
                            errorLabel.setText("This Username is already taken, choose an other one");
                        }
                );
            } else if (s.type == Type.GOOD_USERNAME) {
                String username = usernameTextEdit.getText();
                Platform.runLater(
                        () -> {
                            // Update UI here.
                            progressBar.setProgress(0);
                            errorLabel.setStyle("-fx-text-fill: green");
                            errorLabel.setText("Username is valid");
                        }
                );
                connManager.setClientName(username);
                (new Thread(connManager)).start();
                Stage stage = (Stage) (usernameTextEdit).getScene().getWindow();

                    Platform.runLater(
                            () -> {
                                try {
                                    stage.setScene(new Scene(mainWindow.load(),800,600));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
            }
        }
    }
}
