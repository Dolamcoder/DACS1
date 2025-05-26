package Controller.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class listRoomController {

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<?, ?> idColum;

    @FXML
    private TableColumn<?, ?> numberColum;

    @FXML
    private TextField numberText;

    @FXML
    private TableColumn<?, ?> priceColum;

    @FXML
    private TextField priceText;

    @FXML
    private Button removeButton;

    @FXML
    private Button saveButton;

    @FXML
    private TableColumn<?, ?> statusColum;

    @FXML
    private ComboBox<?> statusText;

    @FXML
    private AnchorPane timKiemText;

    @FXML
    private TableColumn<?, ?> typeRoomColum;

    @FXML
    private TextField typeRoomText;

    @FXML
    private Button updateButton;

    @FXML
    private Button xemChiTietButton;

}
