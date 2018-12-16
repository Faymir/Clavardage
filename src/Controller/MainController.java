package Controller;
/**
 * Sample Skeleton for 'template.fxml' Controller Class
 */

import Model.Message;
import Model.WebViewData;
import Network.ConnexionManager;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;

import java.util.*;

public class MainController implements Observer {

    @FXML // fx:id="menuBar"
    private MenuBar menuBar; // Value injected by FXMLLoader

    @FXML // fx:id="userAvatar"
    private ImageView userAvatar; // Value injected by FXMLLoader

    @FXML // fx:id="username"
    private Label usernameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="tabPane"
    private TabPane tabPane; // Value injected by FXMLLoader

    @FXML // fx:id="friendsTab"
    private Tab friendsTab; // Value injected by FXMLLoader

    @FXML // fx:id="friendListVBox"
    private VBox friendListVBox; // Value injected by FXMLLoader

    @FXML // fx:id="connectedUsersTab"
    private Tab connectedUsersTab; // Value injected by FXMLLoader

    @FXML // fx:id="connectedUsersList"
    private ListView<String> connectedUsersList; // Value injected by FXMLLoader

    @FXML // fx:id="discussionWebview"
    private WebView discussionWebview; // Value injected by FXMLLoader

    @FXML // fx:id="textField"
    private TextField textField; // Value injected by FXMLLoader

    @FXML // fx:id="sendMessageBtn"
    private Button sendMessageBtn; // Value injected by FXMLLoader

    @FXML // fx:id="leftStatus"
    private Label leftStatus; // Value injected by FXMLLoader

    @FXML // fx:id="x3"
    private Font x3; // Value injected by FXMLLoader

    @FXML // fx:id="x4"
    private Color x4; // Value injected by FXMLLoader

    @FXML // fx:id="rightStatus"
    private Label rightStatus; // Value injected by FXMLLoader

    private ConnexionManager connManager;
    private WebViewData discussion = new WebViewData();

    public MainController(ConnexionManager connexionManager){
        this.connManager = connexionManager;
    }

    @FXML
    void initialize(){

        initContextMenu();
        Message m1 = new Message("Haha", Calendar.getInstance().getTime(), "Salut! comment vas tu?", null);
        Message m2 = new Message(null, Calendar.getInstance().getTime(), "Je vais bien et toi?", "Haha");
        Vector<Message> v = new Vector<>();
        v.add(m1);
        v.add(m2);
        discussion.setDiscussion(v); 
        discussion.loadDiscussion();
        discussionWebview.getEngine().loadContent(discussion.getHtml());
        usernameLabel.setText(connManager.getClientName());
        this.connectedUsersList.getItems().addAll(connManager.getConnectedUsersName());
    }

    @FXML
    void sendClicked(ActionEvent event) {
        if(textField.getText().isEmpty())
            return;
        Message m = new Message(null, Calendar.getInstance().getTime(), textField.getText(), "haha");
        textField.clear();
        discussion.addMessage(m,true);
        discussionWebview.getEngine().loadContent(discussion.getHtml());
        //discussionWebview.getEngine().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @FXML
    void testFunction(MouseEvent event) {

    }

    @FXML
    void textFieldKeyReleased(KeyEvent event) {
    	
    	if( event.getCode().toString().equals("ENTER"))
    	{
    		sendClicked(null);
    	}
    }

    private void initContextMenu(){
        connectedUsersList.setCellFactory(lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();


            MenuItem editItem = new MenuItem();
            editItem.textProperty().bind(Bindings.format("Démarrer une discussion"));
            editItem.setOnAction(event -> {
                String username = cell.getItem();
                handleContextMenuClick(username);
                connectedUsersList.getItems().remove(username);
            });
            MenuItem deleteItem = new MenuItem();
            //TODO: Mettre réellement un utilisateur sur liste noire car on ne fait que le supprimer de la liste et il y apparaitra encore au prochain scan
            deleteItem.textProperty().bind(Bindings.format("Mettre sur liste noire"));
            deleteItem.setOnAction(event -> connectedUsersList.getItems().remove(cell.getItem()));
            contextMenu.getItems().addAll(editItem, deleteItem);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        });
    }

    private void handleContextMenuClick(String uname){
        friendListVBox.getChildren().add(new Label(uname));
        if(connManager.initChat(uname)) {
            System.out.println("Chat démarré avec [" + uname + "]");
            connManager.addIncomingMessageListener(uname, this);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println("observable = [" + observable.getClass() + "], o = [" + o.toString() + "]");
    }
}
