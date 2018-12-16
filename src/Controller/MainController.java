package Controller;
/**
 * Sample Skeleton for 'template.fxml' Controller Class
 */

import Model.Message;
import Model.Signal;
import Model.User;
import Model.WebViewData;
import Network.ConnexionManager;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;

import java.io.IOException;
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
    private Vector<User> friends;

    public MainController(ConnexionManager connexionManager){
        this.connManager = connexionManager;
        connexionManager.addObserver(this);
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

        FXMLLoader friend = new FXMLLoader(getClass().getResource("View/friendView.fxml"));
        FriendViewController c = new FriendViewController("Faymir");
        friend.setController(c);

        try {
            this.friendListVBox.getChildren().add(friend.load());
            friend = new FXMLLoader(getClass().getResource("View/friendView.fxml"));
            c = new FriendViewController("ffbv");
            friend.setController(c);
            this.friendListVBox.getChildren().add(friend.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            System.out.println("Start chatting with [" + uname + "]");
        }
    }

    private User getFriend(String uname){
        for (User u: friends) {
            if(u.getNickname().equals(uname))
                return u;
        }

        return null;
    }

    private void handleNewConnection(Signal s){
        this.connectedUsersList.getItems().add(s.message);
    }

    private void handleinitChat(Signal s){
        //TODO: not finished not yet working: when received message from server that someone want to chat
        System.out.println("removed = [" + connectedUsersList.getItems().remove(s.message) + "]");
        Label l = new Label(s.message);
        l.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                User u = getFriend(s.message);
                if (u == null){
                    u = new User(s.message);
                }
                for (User friend: friends) {
                    friend.deleteObservers();
                }
//                u.addObserver(this);
            }
        });
//        this.friendListVBox.getChildren().add();
    }

    private void initChat(String username){
        //TODO: not finished: must init listeners, observers, and a user
//        friendListVBox.getChildren().
        //TODO: Create listof FriendView , push new initCHat to it if not exist
        // TODO: create related user if not exist

    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == ConnexionManager.class){
            Signal s = (Signal) o;

            switch (s.type){
                case CONNECT:
                    handleNewConnection(s);
                    break;
                case INIT_CHAT:
                    break;
                case DISCONNECT:
                    break;
                default:
                    break;
            }
        }
        else if(observable.getClass() == FriendViewController.class){
            //TODO: create related method when select a user from list
        }
        else if(observable.getClass() == User.class){
            //TODO: create methods to update view on new message if this user is the current selected and showed on webview
        }
    }
}
