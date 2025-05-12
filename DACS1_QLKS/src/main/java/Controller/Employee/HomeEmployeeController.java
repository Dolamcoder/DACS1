package Controller.Employee;

import Controller.Login.LoginController;
import Dao.Employee.RoomDao;
import Model.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class HomeEmployeeController {
    @FXML
    private Button avartarButton;

    @FXML
    private Button bookRoomButton;

    @FXML
    private Button checkInButton;

    @FXML
    private Button checkOutButton;

    @FXML
    private Button logoutButton;
    public void logout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/DangNhap/Login.fxml"));
        Parent root=loader.load();
        Stage stage=new Stage();
        stage.setScene((new Scene(root)));
        stage.show();
        Stage currentStage = (Stage) ((Node) this.logoutButton).getScene().getWindow();
        currentStage.close();
    }
    @FXML
    private FlowPane flowPane;

    @FXML
    private Button homeButton;
    public void home() throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/org/FXML/Nhan_Vien/HomeEmployee.fxml"));
        Parent root=loader.load();
        Stage stage=new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        Stage currentStage = (Stage) ((Node) this.homeButton).getScene().getWindow();
        currentStage.close();
    }
    @FXML
    private Button listBookingButton;
    public void roomBooking() {
        try {
            AnchorPane datPhongPane = FXMLLoader.load(getClass().getResource("/org/FXML/Nhan_Vien/RoomBooking.fxml"));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(datPhongPane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load RoomBooking.fxml: " + e.getMessage());
        }
    }

    @FXML
    private Button listCustomerButton;

    @FXML
    private Button listInvoiceButton;

    @FXML
    private Label nameLabel;
    public void setNameLabel(){
        this.nameLabel.setText(LoginController.account.getName());
    }
    @FXML
    private Label titleLabel;

    @FXML
    private AnchorPane contentPane;
    public AnchorPane getContentPane(){
        return contentPane;
    }
    @FXML
    public void initialize() {
        setNameLabel();
        RoomDao roomDao = new RoomDao();
        // Tải danh sách phòng
        ArrayList<Room> rooms = roomDao.selectAll();

        // Xóa các ô phòng hiện có
        this.flowPane.getChildren().clear();

        // Tạo ô phòng động
        for (Room room : rooms) {
            try {
                // Tải mẫu RoomPane.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/Nhan_Vien/RoomPane.fxml"));
                AnchorPane roomPane = loader.load();

                // Lấy controller và cập nhật dữ liệu
                RoomPaneController controller = loader.getController();
                controller.setRoomData(room);

                // Thêm ô phòng vào FlowPane
                this.flowPane.getChildren().add(roomPane);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Lỗi khi tải RoomPane.fxml: " + e.getMessage());
            }
        }
    }
}
