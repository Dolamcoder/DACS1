package Controller.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ListService {

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<?, ?> descriptionColum;

    @FXML
    private TextArea descriptionText;

    @FXML
    private TableColumn<?, ?> nameServiceColum;

    @FXML
    private TextField nameServiceText;

    @FXML
    private TextField numberText;

    @FXML
    private TableColumn<?, ?> priceColum;

    @FXML
    private Button removeButton;

    @FXML
    private Button saveButton;

    @FXML
    private TableColumn<?, ?> serviceIDColum;

    @FXML
    private TextField serviceText;

    @FXML
    private AnchorPane timKiemText;

    @FXML
    private Button updateButton;

    @FXML
    private Button xemChiTietButton;

}
