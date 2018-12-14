package Controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.BottomNavigation;
import com.gluonhq.charm.glisten.control.BottomNavigationButton;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.mvc.View;
public class ConnexionController {

    @FXML
    private TextField usernameTextField;

    @FXML
    void CancelClicked(MouseEvent event) {
        System.out.println("event = [" + event.getSource().toString() + "]");
    }

    @FXML
    void validerClicked(MouseEvent event) {
        System.out.println("event = [" + event.getSource().toString() + "]");
    }

}
