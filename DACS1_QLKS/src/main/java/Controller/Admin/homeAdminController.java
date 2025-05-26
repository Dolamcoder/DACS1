package Controller.Admin;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class homeAdminController {

    @FXML
    private AnchorPane centerPane;
    @FXML
    private Button accountButton;

    @FXML
    private TableColumn<?, ?> actionByColum;

    @FXML
    private TableColumn<?, ?> actionColum;

    @FXML
    private Button avartarButton;

    @FXML
    private Button baoCaoButton;

    @FXML
    private FontAwesomeIcon exitButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label nameLabel;

    @FXML
    private TableColumn<?, ?> nameTableColum;

    @FXML
    private TableColumn<?, ?> ngayThucHien;

    @FXML
    private TableColumn<?, ?> recordColum;

    @FXML
    private Button roomButton;

    @FXML
    private Button serviceButton;

    @FXML
    private Button staffButton;

    @FXML
    private TableColumn<?, ?> sttColum;

    @FXML
    private TableView<?> table;

    @FXML
    private TextField timKiemText;

    @FXML
    private TableColumn<?, ?> timeColum;

    @FXML
    void checkin(ActionEvent event) {

    }

    @FXML
    void checkout(ActionEvent event) {

    }

    @FXML
    void home(ActionEvent event) {

    }

    @FXML
    void listCustomer(ActionEvent event) {

    }

    @FXML
    void listroomBooking(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) {

    }

    @FXML
    void roomBooking(ActionEvent event) {

    }

}
