package Controller.Employee;

import Alert.Alert;
import Controller.Admin.AuditLogController;
import Dao.Employee.CustomerDao;
import Model.Customer;
import Model.TaoID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import regex.InputValidator;

public class selectCustomerController {
    ArrayList<Customer> listCustomer;
    CustomerDao cusDao;
    Alert al;
    InputValidator input=new InputValidator();
    public static Customer customer;
    public selectCustomerController() {
        cusDao = new CustomerDao();
        listCustomer = cusDao.selectAll();
        al = new Alert();
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
        if(input.isValidPhoneNumber(phone)==false){
            al.showErrorAlert("Số điện thoại không hợp lệ");
            return;
        }
        if(input.isValidEmail(email)==false){
            al.showErrorAlert("Email không hợp lệ");
            return;
        }
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
        if(cusDao.searchById(idCustomerText.getText())==null){
            al.showErrorAlert("Không tìm thấy khách hàng với ID: " + idCustomerText.getText());
            return;
        }

        AnchorPane roomBookingPane = FXMLLoader.load(getClass().getResource("/org/FXML/Nhan_Vien/RoomBooking.fxml"));
        rootPane.getChildren().clear();
        rootPane.getChildren().add(roomBookingPane);
    }
    /**
     * Sets up real-time search functionality for the customer table
     */
    private void setupSearchFunction() {
        // Add listener to search text field to filter results as user types
        timKiemText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCustomers(newValue);
        });
    }

    /**
     * Filters customers based on search keyword
     */
    private void filterCustomers(String keyword) {
        if (keyword == null) {
            keyword = "";
        }

        // Convert to lowercase for case-insensitive search
        String searchTerm = keyword.trim().toLowerCase();

        // If search term is empty, show all customers
        if (searchTerm.isEmpty()) {
            tableCustomer.setItems(FXCollections.observableArrayList(listCustomer));
            return;
        }

        // Filter the customer list
        ArrayList<Customer> filteredList = new ArrayList<>();
        for (Customer customer : listCustomer) {
            if (customer.getId().toLowerCase().contains(searchTerm) ||
                    customer.getName().toLowerCase().contains(searchTerm) ||
                    customer.getEmail().toLowerCase().contains(searchTerm) ||
                    customer.getPhone().toLowerCase().contains(searchTerm)) {
                filteredList.add(customer);
            }
        }

        // Update table view with filtered results
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
                    AuditLogController.getAuditLog("KhachHang", customer.getId(), "Cập nhật thông tin khách hàng "+customer.getName(), "Nhân viên");
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
        if (customer.getId() == null) {
            return;
        }
        boolean success = cusDao.insertBooking(customer);
        if (success) {
            listCustomer.add(customer);
            tableCustomer.setItems(FXCollections.observableArrayList(listCustomer));
            al.showInfoAlert("Thêm khách hàng thành công!");
            AuditLogController.getAuditLog("KhachHang", customer.getId(), "Thêm khách hàng "+customer.getName(), "Nhân viên");
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
        setupSearchFunction();
    }
}