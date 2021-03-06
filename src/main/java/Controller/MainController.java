package Controller;


import Model.*;
import Network.*;
import Security.Cryptography;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;

//user2%&%disconnect

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

    private WebViewData discussion;
    private Vector<User> friends;
    private Vector<FriendViewController> friendViews;
    private Stage stage;

    //"http://webresizer.com/images2/bird1_after.jpg"
    public MainController(Stage stage){
        this.stage = stage;
        friends = new Vector<>();
        friendViews = new Vector<>();
        discussion = new WebViewData();
        Message m = new Message("friend", Calendar.getInstance().getTime(), urltoImgTag("http://webresizer.com/images2/bird1_after.jpg"), null);
        discussion.addMessage(m, false);
    }

    @FXML
    void initialize(){

        SharedObjects.get().connManager.addObserver(this);
        initContextMenu();
        usernameLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                changeUsername(null);
            }
        });
        discussion.loadDiscussion();
        discussionWebview.getEngine().loadContent(discussion.getHtml());
        usernameLabel.setText(SharedObjects.get().connManager.getClientName());
        this.connectedUsersList.getItems().addAll(SharedObjects.get().connManager.getConnectedUsersName());
    }

    @FXML
    private void sendClicked(ActionEvent event) {
        if(textField.getText().isEmpty())
            return;
        String actualFriendName = getActualFriend();
        if (actualFriendName == null) {
            System.out.println("actualFriend Error = [" + actualFriendName + "]");
            return;
        }
        Message m = new Message(SharedObjects.get().connManager.getClientName(), Calendar.getInstance().getTime(), textField.getText(), actualFriendName);
        textField.clear();
        SharedObjects.get().connManager.sendMessage(m);
        discussion.addMessage(m,true); //Cette ligne rajoute aussi l'utilisateur à la liste des discussions de l'objet user rataché
        discussionWebview.getEngine().loadContent(discussion.getHtml());
    }

    @FXML
    void textFieldKeyReleased(KeyEvent event) {
    	
    	if( event.getCode().toString().equals("ENTER"))
    	{
    		sendClicked(null);
    	}
    }



    @FXML
    void newFileClicked(ActionEvent event){
        File path = openFileChooser();
        System.out.println(path);
        if(path == null) {
            return;
        }
        sendImage(getHtmlImageFormat(path.toPath()));
    }

    @FXML
    void sendUrlFile(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog("FileUrl");
        dialog.setTitle("Image File");
        dialog.setHeaderText("You can find you image url by googling it and paste it here");
        dialog.setContentText("Please enter file url (https://...):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(url -> {
            sendImage(urltoImgTag(url));
        });
    }

    @FXML
    void changeProfilePicture(ActionEvent event){
        File picture = openFileChooser();

        if(picture == null)
            return;

        try {
            userAvatar.setImage(new Image(picture.toURI().toURL().toString(), true));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void changeUsername(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog("username");
        dialog.setTitle("Username");
        dialog.setHeaderText("");
        dialog.setContentText("Enter your new username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newUname -> {
            if(!SharedObjects.get().connManager.isUsernameExist(newUname)){
                if(!Database.getInstance().checkExist(newUname, "user")) {
                    if(SharedObjects.get().connManager.changeUsername(newUname)){
                        Database.getInstance().changeUserName(SharedObjects.get().connManager.getClientName(), newUname);
                        Platform.runLater(() -> {
                            usernameLabel.setText(newUname);
                        });
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Alright!", ButtonType.YES);
                        alert.showAndWait();
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This user already exist on your computer.\nLogout and login with this new username", ButtonType.YES);
                    alert.showAndWait();
                }
            }
        });
    }

    private File openFileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Folder");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        return fileChooser.showOpenDialog(stage);
    }

    private void initContextMenu(){
        connectedUsersList.setCellFactory(lv -> {

            ListCell<String> cell = new ListCell<>();
            cell.setOnMouseClicked(event -> {
                    if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                        handleContextMenuClick(cell.getText());
                    }
                });

                ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem();
            editItem.textProperty().bind(Bindings.format("Démarrer une discussion"));

            editItem.setOnAction(event -> {
                System.out.println("event " + event);
                String username = cell.getItem();
                handleContextMenuClick(username);
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
        if(SharedObjects.get().connManager.initChat(uname)) {
            initChat(uname);
        }
    }

    private User getFriend(String uname){
        for (User u: friends) {
            if(u.getNickname().equals(uname))
                return u;
        }

        return null;
    }

    private FriendViewController getFriendView(String uname){
        for (FriendViewController u: friendViews) {
            if(u.getNickname().equals(uname))
                return u;
        }

        return null;
    }

    private String getActualFriend(){
        for (FriendViewController u: friendViews) {
            if(u.isSelected())
                return u.getNickname();
        }
        return  null;
    }

    private void handleNewConnection(Signal s){
        Platform.runLater(
                () -> {
                    // Update UI here.
                    this.connectedUsersList.getItems().remove(s.message);
                    this.connectedUsersList.getItems().add(s.message);
                }
        );
        User u = getFriend(s.message);
        if(u != null){
            u.setConnected(true);
            Objects.requireNonNull(getFriendView(s.message)).connected();
            if (discussion.getName().equals(s.message)){
                discussion.addServerMessage(s.message, " s'est reconnecté", true);
                discussion.setFriend(null, null);
                discussion.updateResultHtml();
                Platform.runLater(
                        () -> {
                            discussionWebview.getEngine().loadContent(discussion.getHtml());
                        }
                );
            }
            SharedObjects.get().connManager.initChat(s.message);
            initChat(s.message);
        }
    }

    private void handleDisconnection(Signal s){
        Platform.runLater(
            () -> {connectedUsersList.getItems().remove(s.message);}
        );

        if(discussion.getName().equals(s.message))
            discussion.addServerMessage(s.message , " viens de se déconnecter", false);

        User u = getFriend(s.message);
        FriendViewController c = getFriendView(s.message);

        if(u != null)   u.setConnected(false);
        if(c != null)   c.disconnected();

        Platform.runLater(
                () -> { discussionWebview.getEngine().loadContent(discussion.getHtml()); }
        );
    }

    private void initChat(String username){
        User friend = getFriend(username);
        if(friend == null){
            friend = new User(username);
            User tmp = Database.getInstance().getUser(username);
            if (tmp!= null)
                friend = tmp;
            friends.add(friend);
            FriendViewController c = new FriendViewController(username);
            friendViews.add(c);

            SharedObjects.get().connManager.addIncomingMessageListener(username,friend);
            c.addObserver(this);
            friend.addObserver(c);
            friend.addObserver(this);

            FXMLLoader view = null;
            try {
                view = new FXMLLoader(FileLoader.getInstance().get("friendView.fxml"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            view.setController(c);

            // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
            FXMLLoader finalView = view;
            Platform.runLater(
                    () -> {
                        // Update UI here.
                        try {
                            friendListVBox.getChildren().add(finalView.load());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            SharedObjects.get().connManager.addObserver(friend);
        }else{
            SharedObjects.get().connManager.addIncomingMessageListener(username, friend);
            SharedObjects.get().connManager.addObserver(friend);
        }
        SoundPlayer.getInstance().play(SoundPlayer.BELL);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BroadcastConnexionManager.class
                || observable.getClass() == LocalConnexionManager.class
                || observable.getClass() == ServerConnexionManager.class){
            Signal s = (Signal) o;

            switch (s.type){
                case CONNECT:
                    handleNewConnection(s);
                    break;
                case INIT_CHAT:
                    initChat(s.message);
                    break;
                case DISCONNECT:
                    handleDisconnection(s);
                    break;
                case USERNAME_CHANGED:
                    handleUsernameChanged(s);
                default:
                    break;
            }
        }
        else if(observable.getClass() == FriendViewController.class){
            //TODO: create related method when select a user from list
            
            Signal s = (Signal) o;
                if(s.type == Type.SHOW_DISCUSSION){
                for (FriendViewController c: friendViews) {
                    if (!c.getNickname().equals(s.message))
                        c.unselect();
                }
                User u = getFriend(s.message);
                if (u != null) {
                    int nbr = 0;
                    discussion = new WebViewData(u.getDiscussion());
                    discussion.setStatus(u.isConnected());
                    if(!u.getDiscussion().isEmpty())
                        nbr = u.getDiscussion().size();
                    discussion.setFriend(u.getNickname(), nbr + "");
                    discussion.updateResultHtml();
                }
                else {
                    System.out.println("SHIT HAPPENS IN SHOW DISCUSSION  observable = [" + observable + "], s.type = [" + s.type + "] s.message [" + s.message + "]");
                    System.exit(-6);
                }
                Platform.runLater(
                        () -> {
                            // Update UI here.
                                discussionWebview.getEngine().loadContent(discussion.getHtml());
                                u.resetLastMessageIndex();
                        }
                );
            }
        }
        else if(observable.getClass() == User.class){
            //TODO: create methods to update view on new message if this user is the current selected and showed on webview
            Signal s = (Signal) o;
            if(s.type == Type.NEW_MESSAGE){
                SoundPlayer.getInstance().play(SoundPlayer.MESSAGE_TONE);
            }
            if(s.type == Type.DISCONNECT){
                handleDisconnection(s);
            }
            //TODO: afficher une icone représentant le nombre de nouveau messages
        }
    }

    private void handleUsernameChanged(Signal s) {
        User u = getFriend(s.message);
        if (u!= null){
            u.setUsername(s.newUsername);

            if (discussion.getName().equals(s.message))
                discussion.setFriend(s.newUsername, null);

            if(Database.getInstance().checkExist(s.message, "friends")){
                Database.getInstance().update(s.message, u);
            }
        }
        Platform.runLater(() -> {
            this.connectedUsersList.getItems().remove(s.message);
            this.connectedUsersList.getItems().add(s.newUsername);
            if (u!= null){
                Objects.requireNonNull(getFriendView(s.message)).setNickname(s.newUsername);
            }
        });


    }

    public void saveData() {
        for (int i = 0; i < friends.size(); i++) {
            if(!Database.getInstance().checkExist(friends.get(i).getNickname(), "friends"))
                Database.getInstance().insert(friends.get(i));
            else
                Database.getInstance().update(friends.get(i).getNickname(), friends.get(i));
        }
    }
    //return an html image formatted <img src="..." >
    public String getHtmlImageFormat(Path filePath){
        String img = "";
        try {
            img = Base64.encodeBase64String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String style = "height:400px;max-width:500px;width: expression(this.width > 500 ? 500: true); position: relative; margin: auto 0px;";
        return "<img style=\"" + style + "\" src=\"data:image/png;base64, " + img + "\"/>";
    }

    private String urltoImgTag(String url){
        String style = "height:400px;max-width:500px;width: expression(this.width > 500 ? 500: true); position: relative; margin: auto 0px;";
        return "<img style=\"" + style + "\" src=\"" + url + "\"/>";
    }

    private void sendImage(String str){
        String actualFriendName = getActualFriend();
        if (actualFriendName == null) {
            System.out.println("actualFriend Error = [" + actualFriendName + "]");
            return;
        }
        Message m = new Message(null, Calendar.getInstance().getTime(),str, actualFriendName);
        SharedObjects.get().connManager.sendMessage(m);
        discussion.addMessage(m, true);
        discussionWebview.getEngine().loadContent(discussion.getHtml());
    }
}
