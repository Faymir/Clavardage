package Controller;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import Model.Signal;
import Model.Type;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


/**
 * Se met en ecoute par rapport à un utilisateur pour savoir si il a recu des nouveaux messages
 *
 * notifie le main controller pour lui signaler de mettre a jour la webview et soit
 *      => afficher le nouveau message si c'est la discussion actuelle qui est visualisée
 *      => soit charger la discussion actuelle dans la webview
 */
public class FriendViewController extends Observable implements Observer {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox friendView;

    @FXML
    private Label friendViewUname;

    @FXML
    private ImageView friendViewStatus;

    @FXML
    private ImageView friendPicture;

    @FXML
    void friendViewClicked(MouseEvent event) {
//        System.out.println("event = [" + event + "]");
        this.select();
        setChanged();
        notifyObservers(new Signal(Type.SHOW_DISCUSSION,nickname));

    }

    private String nickname;
    private boolean selected = false;

    public FriendViewController(String nickname){
        this.nickname = nickname;
    }

    @FXML
    void initialize() {
        assert friendView != null : "fx:id=\"friendView\" was not injected: check your FXML file 'friendView.fxml'.";
        assert friendViewUname != null : "fx:id=\"friendViewUname\" was not injected: check your FXML file 'friendView.fxml'.";
        assert friendViewStatus != null : "fx:id=\"friendViewStatus\" was not injected: check your FXML file 'friendView.fxml'.";
        friendViewUname.setText(nickname);

        friendView.getStyleClass().setAll("lbl","lbl-primary");

    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return  this.nickname;
    }

    public void unselect(){
        this.selected = false;
        friendView.setStyle("-fx-border-color: inherit");
        friendView.setEffect(null);
    }

    public void disconnected(){
        System.out.println(getClass().getResource("../disconnected.png").toString());
        this.friendViewStatus.setImage(new Image(getClass().getResource("../disconnected.png").toString()));
    }

    public void connected(){
        this.friendViewStatus.setImage(new Image(getClass().getResource("../connected.png").toString()));
    }
    public void select(){
        this.selected = true;
//        int depth = 70;
//        DropShadow borderGlow= new DropShadow();
//        borderGlow.setOffsetY(0f);
//        borderGlow.setOffsetX(0f);
//        borderGlow.setColor(Color.RED);
//        borderGlow.setWidth(depth);
//        borderGlow.setHeight(depth);
//        friendView.setEffect(borderGlow);
        friendView.setEffect(new SepiaTone());
        friendView.setStyle("-fx-border-color: blue");
        //friendView.setStyle("-fx-fill: lightblue");
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void update(Observable observable, Object o) {
        //TODO afficher le nombre de nouveaux messages
        if(this.selected){
            setChanged();
            notifyObservers(new Signal(Type.SHOW_DISCUSSION,nickname));
        }
    }
}
