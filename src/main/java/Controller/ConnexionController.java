package Controller;

import Model.Database;
import Model.Signal;
import Model.Type;
import Network.*;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<RadioButton> b = new AtomicReference<>();
        switch (((RadioButton)event.getSource()).getText()){
            case "Test":
                b.set(testRadioButton);
                break;
            case "Broadcast":
                b.set(broadcastRadioButton);
                break;
            case "Server":
                serverRadioButton.setSelected(false);
                TextInputDialog dialog = new TextInputDialog("http://node6669-clavadage.jcloud.ik-server.com/webapi/");
                dialog.setTitle("Server address");
                dialog.setHeaderText("");
                dialog.setContentText("Please enter url:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(url -> {
                    if (url.isEmpty())
                        return;
                    SharedObjects.get().serverIp = url;
                    b.set(serverRadioButton);
                });
                break;
            default:
                break;
        }
        if(b.get() != null) {
            testRadioButton.setSelected(false);
            broadcastRadioButton.setSelected(false);
            serverRadioButton.setSelected(false);
            b.get().setSelected(true);
        }

        //System.out.println("event = [" + ((RadioButton)event.getSource()).getText() + "]");
    }

    private void validate() throws UnsupportedClassException {

        if (testRadioButton.isSelected())
            SharedObjects.get().connManager = ConnManagerFactory.getConnectionManager(LocalConnexionManager.class);
        else if (broadcastRadioButton.isSelected())
            SharedObjects.get().connManager = ConnManagerFactory.getConnectionManager(BroadcastConnexionManager.class);
        else if(serverRadioButton.isSelected()) {
            if(SharedObjects.get().serverIp == null || SharedObjects.get().serverIp.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "You must provide a valid Server ip address", ButtonType.YES);
                alert.showAndWait();
                return;
            }
            SharedObjects.get().connManager = ConnManagerFactory.getConnectionManager(ServerConnexionManager.class);
        }

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
