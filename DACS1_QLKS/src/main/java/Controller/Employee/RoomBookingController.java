package Controller.Employee;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class RoomBookingController {

    @FXML
    private TextField CustomerID;

    @FXML
    private DatePicker NgayTra;

    @FXML
    private TextField RoomBookingID;

    @FXML
    private TextField RoomID;

    @FXML
    private TextField giaThue;

    @FXML
    private ComboBox<?> loaiPhong;

    @FXML
    private TextField loaiPhongText;

    @FXML
    private TextArea moTa;

    @FXML
    private TextField nameCustomer;

    @FXML
    private DatePicker ngayNhan;

    @FXML
    private TextField phone;

    @FXML
    private Button roomBook;

    @FXML
    private Button selectCustomer;
    private AnchorPane contentPane;

    public void setContentPane(AnchorPane contentPane) {
        this.contentPane = contentPane;
    }

    public void selectCustomer() throws IOException {
        AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Nhan_Vien/SelectCustomer.fxml"));
        contentPane.getChildren().clear();
        contentPane.getChildren().add(selectPane);
    }
    @FXML
    private TextField soGiuong;

    @FXML
    private TextField soGiuongText;

    @FXML
    private TextField soNguoi;

    @FXML
    private TextField soPhong;

    @FXML
    private TableView<?> tableRoom;

    @FXML
    private Button timKiem;

}
