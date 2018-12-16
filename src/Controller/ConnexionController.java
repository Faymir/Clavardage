package Controller;

import Network.ConnexionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnexionController {

    private ConnexionManager connManager;
    private FXMLLoader mainWindow;
    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField usernameTextEdit;

    @FXML
    private Label errorLabel;

    public ConnexionController(ConnexionManager connexionManager, FXMLLoader mainWindow){
        System.out.println("Before login init");
        connManager = connexionManager;
        this.mainWindow = mainWindow;
        System.out.println("After login init");
    }

    @FXML
    public void initialize(){
        System.out.println("Initialize");
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

    private void validate(){
        String username = usernameTextEdit.getText();
        if(username.isEmpty())
            return;
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        if(connManager.isUsed(username)) {
            errorLabel.setStyle("-fx-text-fill: red");
            errorLabel.setText("This Username is already taken, choose an other one");
        }
        else {
            errorLabel.setStyle("-fx-text-fill: green");
            errorLabel.setText("Username is valid");
            connManager.setClientName(username);
            (new Thread(connManager)).start();
            Stage stage = (Stage) (usernameTextEdit).getScene().getWindow();
            try {
                stage.setScene(new Scene(mainWindow.load(),800,600));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
