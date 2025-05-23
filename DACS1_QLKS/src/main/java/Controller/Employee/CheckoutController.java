package Controller.Employee;

import Dao.Employee.CustomerDao;
import Dao.Employee.RoomBookingDao;
import Dao.Employee.RoomDao;
import Model.Booking;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Alert.alert;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CheckoutController {

    @FXML
    private TableView<Booking> table;
    @FXML
    private TableColumn<Booking, String> bookingID;
    @FXML
    private TableColumn<Booking, String> customerID;
    @FXML
    private TableColumn<Booking, String> roomID;
    @FXML
    private TableColumn<Booking, String> checkIn;
    @FXML
    private TableColumn<Booking, String> checkOut;
    @FXML
    private TableColumn<Booking, String> status;
    @FXML
    private TableColumn<Booking, LocalDateTime> createdAt;
    @FXML
    private TextField timKiemText;
    @FXML
    private Button checkout;

    private final RoomBookingDao bookingDao = new RoomBookingDao();
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();
    private List<Booking> dangOlist = new ArrayList<>();
    private final alert al=new alert();
    private final  CustomerDao cDao=new CustomerDao();
    private final RoomDao rDao=new RoomDao();
    @FXML
    public void initialize() {
        setupTableColumns();
        loadTableData();
        setupSearchListener();
    }

    private void setupTableColumns() {
        bookingID.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        roomID.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        checkIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAt.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });
    }

    private void loadTableData() {
        ArrayList<Booking> allBookings = bookingDao.selectAll();

        dangOlist.clear();
        for (Booking b : allBookings) {
            if ("Đang ở".equalsIgnoreCase(b.getStatus())) {
                dangOlist.add(b);
            }
        }

        bookingList.setAll(dangOlist);
        table.setItems(bookingList);
    }
    @FXML
    private void setupCheckoutButton() {
        Booking selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            al.showErrorAlert("Vui lòng chọn bản ghi cần check - out");
            return;
        }

        // Tạo hộp thoại xác nhận
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận Check-out");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn check-out không?");

        // Hiển thị và chờ người dùng chọn
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String bookingId = selected.getBookingId();
            boolean updated = bookingDao.updateBookingStatus(bookingId, "Đã thanh toán");
            boolean check=rDao.updateStatus(selected.getRoomId(), 3);
            if (updated && check) {
                al.showInfoAlert("Check-out thành công");
                loadTableData(); // refresh danh sách
            } else {
                al.showErrorAlert("Check-out thất bại");
            }
        } else {
           return;
        }
    }
    private void setupSearchListener() {
        timKiemText.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue.toLowerCase().trim();
            if (keyword.isEmpty()) {
                bookingList.setAll(dangOlist);
            } else {
                ObservableList<Booking> filtered = FXCollections.observableArrayList(
                     dangOlist.stream()
                                .filter(b -> {
                                    Customer customer = cDao.searchById(b.getCustomerId());
                                    return (customer != null && customer.getName().toLowerCase().contains(keyword))
                                            || String.valueOf(b.getBookingId()).toLowerCase().contains(keyword)         // BookingID
                                            || String.valueOf(b.getRoomId()).toLowerCase().contains(keyword)     // RoomID
                                            || String.valueOf(b.getCustomerId()).toLowerCase().contains(keyword) // CustomerID
                                            || b.getCheckInDate().toString().toLowerCase().contains(keyword)     // CheckInDate
                                            || b.getCheckOutDate().toString().toLowerCase().contains(keyword)    // CheckOutDate
                                            || b.getCreatedAt().toString().toLowerCase().contains(keyword)       // CreatedAt
                                            || b.getStatus().toLowerCase().contains(keyword);                    // Status
                                })
                                .collect(Collectors.toList())
                );
                bookingList.setAll(filtered);
            }
            table.refresh();
        });
    }

}
