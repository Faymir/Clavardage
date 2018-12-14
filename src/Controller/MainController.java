package Controller;
/**
 * Sample Skeleton for 'template.fxml' Controller Class
 */

import Model.Message;
import Model.WebViewData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;

import java.util.Calendar;
import java.util.Vector;

public class MainController {

    @FXML // fx:id="menuBar"
    private MenuBar menuBar; // Value injected by FXMLLoader

    @FXML // fx:id="userAvatar"
    private ImageView userAvatar; // Value injected by FXMLLoader

    @FXML // fx:id="username"
    private Label username; // Value injected by FXMLLoader

    @FXML // fx:id="tabPane"
    private TabPane tabPane; // Value injected by FXMLLoader

    @FXML // fx:id="friendsTab"
    private Tab friendsTab; // Value injected by FXMLLoader

    @FXML // fx:id="friendListVBox"
    private VBox friendListVBox; // Value injected by FXMLLoader

    @FXML // fx:id="connectedUsersTab"
    private Tab connectedUsersTab; // Value injected by FXMLLoader

    @FXML // fx:id="connectedUsersList"
    private ListView<?> connectedUsersList; // Value injected by FXMLLoader

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


    WebViewData discussion = new WebViewData();

    @FXML
    void initialize(){
        Message m1 = new Message("Haha", Calendar.getInstance().getTime(), "Salut! comment vas tu?", null);
        Message m2 = new Message(null, Calendar.getInstance().getTime(), "Je vais bien et toi?", "Haha");
        Vector<Message> v = new Vector<>();
        v.add(m1);
        v.add(m2);
        discussion.setDiscussion(v);
        discussion.loadDiscussion();
        discussionWebview.getEngine().loadContent(discussion.getHtml());
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
    void textFieldKeyRealeased(KeyEvent event) {
    	
    	if( event.getCode().toString().equals("ENTER"))
    	{
    		sendClicked(null);
    	}
    }

}
