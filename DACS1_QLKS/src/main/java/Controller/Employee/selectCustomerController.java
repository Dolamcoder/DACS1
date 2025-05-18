package Controller.Employee;

import Alert.alert;
import Dao.Employee.CustomerDao;
import Dao.Employee.RoomBookingDao;
import Model.Booking;
import Model.Customer;
import Model.TaoID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class selectCustomerController {
    ArrayList<Customer> listCustomer;
    CustomerDao cusDao;
    alert al;
    public static Customer customer;
    public selectCustomerController() {
        cusDao = new CustomerDao();
        listCustomer = cusDao.selectAll();
        al = new alert();
    }
    @FXML
    private Button addButton;

    @FXML
    private Button datPhongButton;

    @FXML
    private TableColumn<Customer, String> emailColum;

    @FXML
    private TextField emailCustomerText;

    @FXML
    private TableColumn<Customer, String> idColum;

    @FXML
    private TextField idCustomerText;

    @FXML
    private TableColumn<Customer, String> nameColum;

    @FXML
    private TextField nameCustomerText;

    @FXML
    private TableColumn<Customer, String> phoneColum;

    @FXML
    private TextField phoneCustomerText;

    @FXML
    private Button timKiemButton;

    @FXML
    private TextField timKiemText;

    @FXML
    private Button updateButton;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TableView<Customer> tableCustomer;
    public void autoID(){
        // Tạo Set chứa các ID hiện có (lấy ID booking từ từng Booking)
        Set<String> existingIDs = new HashSet<>();
        for (Customer c : this.listCustomer) {
            existingIDs.add(c.getId()); // giả sử getBookingID() là phương thức lấy ID booking kiểu String
        }

        TaoID id = new TaoID();
        // Giả sử bạn đã bổ sung hàm randomIDRoomBooking(Set<String> existingIDs)
        this.idCustomerText.setText(id.randomIDKH(existingIDs));
    }
    public void layObject() {
        customer=new Customer();
        String name = this.nameCustomerText.getText();
        String email = this.emailCustomerText.getText();
        String phone = this.phoneCustomerText.getText();
        if(name.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty()){
            al.showErrorAlert("Không để dữ liệu trống");
            return;
        }
        customer.setId(this.idCustomerText.getText());
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
    }
    public void dataTable() {
        idColum.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColum.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColum.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColum.setCellValueFactory(new PropertyValueFactory<>("phone"));
        this.tableCustomer.setItems(FXCollections.observableArrayList(listCustomer));
    }
    @FXML
    public void layRowTable() {
        Customer selectedCustomer = this.tableCustomer.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            this.idCustomerText.setText(selectedCustomer.getId());
            this.nameCustomerText.setText(selectedCustomer.getName());
            this.emailCustomerText.setText(selectedCustomer.getEmail());
            this.phoneCustomerText.setText(selectedCustomer.getPhone());
        }
    }
    @FXML
    public void handleDatPhong() throws IOException {
        layObject();
        AnchorPane roomBookingPane = FXMLLoader.load(getClass().getResource("/org/FXML/Nhan_Vien/RoomBooking.fxml"));
        rootPane.getChildren().clear();
        rootPane.getChildren().add(roomBookingPane);
    }
    @FXML
    public void handleTimKiem() {
        String keyword = timKiemText.getText().trim().toLowerCase();
        ArrayList<Customer> filteredList = new ArrayList<>();

        // Nếu từ khóa rỗng, hiển thị toàn bộ danh sách
        if (keyword.isEmpty()) {
            tableCustomer.setItems(FXCollections.observableArrayList(listCustomer));
            return;
        }

        // Lọc danh sách khách hàng theo từ khóa
        for (Customer customer : listCustomer) {
            if (customer.getId().toLowerCase().contains(keyword) ||
                    customer.getName().toLowerCase().contains(keyword) ||
                    customer.getEmail().toLowerCase().contains(keyword) ||
                    customer.getPhone().toLowerCase().contains(keyword)) {
                filteredList.add(customer);
            }
        }

        // Cập nhật TableView với danh sách đã lọc
        tableCustomer.setItems(FXCollections.observableArrayList(filteredList));
    }

    @FXML
    public void handleUpdate() {
        // Lấy thông tin từ TextField
        String id = idCustomerText.getText().trim();
        String name = nameCustomerText.getText().trim();
        String email = emailCustomerText.getText().trim();
        String phone = phoneCustomerText.getText().trim();

        // Kiểm tra dữ liệu hợp lệ
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            al.showErrorAlert("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // Tìm và cập nhật khách hàng trong listCustomer
        for (Customer customer : listCustomer) {
            if (customer.getId().equals(id)) {
                // Cập nhật thông tin cho đối tượng customer trước
                customer.setName(name);
                customer.setEmail(email);
                customer.setPhone(phone);

                // Gọi updateRoomBooking để cập nhật cơ sở dữ liệu
                boolean success = cusDao.updateRoomBooking(customer);
                if (success) {
                    al.showInfoAlert("Cập nhật thông tin khách hàng thành công!");
                    tableCustomer.refresh(); // Làm mới TableView
                } else {
                    al.showErrorAlert("Cập nhật cơ sở dữ liệu thất bại!");
                }
                return;
            }
        }
        al.showErrorAlert("Không tìm thấy khách hàng với ID: " + id);
    }

    @FXML
    public void addButton() {
        layObject();
        if (customer == null) {
            al.showErrorAlert("Không để dữ liệu trống");
            return;
        }
        boolean success = cusDao.insertBooking(customer);
        if (success) {
            listCustomer.add(customer);
            tableCustomer.setItems(FXCollections.observableArrayList(listCustomer));
            al.showInfoAlert("Thêm khách hàng thành công!");
            autoID();
            nameCustomerText.clear();
            emailCustomerText.clear();
            phoneCustomerText.clear();
        } else {
            al.showErrorAlert("Thêm khách hàng vào cơ sở dữ liệu thất bại!");
        }
    }
    @FXML
    public void initialize(){
        autoID();
        dataTable();
        layRowTable();
    }
}