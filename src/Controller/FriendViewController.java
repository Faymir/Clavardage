package Controller;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class FriendViewController extends Observable {

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
        System.out.println("event = [" + event + "]");
        friendView.setEffect(new SepiaTone());
        friendView.setStyle("-fx-border-color: blue");

    }

    private String nickname;
    private boolean status;

    public FriendViewController(String nickname){
        this.nickname = nickname;
    }

    @FXML
    void initialize() {
        assert friendView != null : "fx:id=\"friendView\" was not injected: check your FXML file 'friendView.fxml'.";
        assert friendViewUname != null : "fx:id=\"friendViewUname\" was not injected: check your FXML file 'friendView.fxml'.";
        assert friendViewStatus != null : "fx:id=\"friendViewStatus\" was not injected: check your FXML file 'friendView.fxml'.";
        friendViewUname.setText(nickname);

    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }
}
