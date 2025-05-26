package Controller.Employee;

import Dao.Employee.*;
import Model.*;
import Service.BookingSchedulerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Alert.alert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final alert al = new alert();
    private final CustomerDao cDao = new CustomerDao();
    private final RoomDao rDao = new RoomDao();
    ServiceBookingDao serviceBookingDao = new ServiceBookingDao();
    ServiceBookingDetailDao serviceBookingDetailDao = new ServiceBookingDetailDao();
    InvoiceDao invoiceDao;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadTableData();
        setupSearchListener();
        invoiceDao = new InvoiceDao();
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
            boolean check = rDao.updateStatus(selected.getRoomId(), 3);
            boolean updateCustomer = cDao.updateCustomerStatus(selected.getCustomerId(), "Đã trả phòng");
            if (updated && check) {
                if (!addHoaDon(selected)) {
                    al.showErrorAlert("Lỗi khi tạo hóa đơn. Vui lòng thử lại sau.");
                    return;
                }
                double totalAmount = getTotalAmount(selected, serviceBookingDetailDao.getServicesByBookingId(serviceBookingDao.getByRoomId(selected.getRoomId())));
                if(!addCardLevel(totalAmount, selected.getCustomerId())) {
                    al.showErrorAlert("Lỗi khi cập nhật cấp độ thẻ khách hàng. Vui lòng thử lại sau.");
                    return;
                }
                // Đặt lịch cập nhật trạng thái phòng thành 1 (trống) sau 1 giờ
                BookingSchedulerService.scheduleRoomStatusUpdate(
                        selected.getRoomId(),
                        1,  // Trạng thái 1 = Trống/Có sẵn
                        20// 60 phút = 1 giờ
                );
                al.showInfoAlert("Check-out thành công. Phòng Cần dọn dẹp trong x20 phút");
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

    public String generateInvoiceID() {
        ArrayList<Invoice> invoices = invoiceDao.findAll();
        Set<String> invoiceIDs = new HashSet<>();
        for (Invoice invoice : invoices) {
            invoiceIDs.add(invoice.getInvoiceID());
        }
        TaoID id = new TaoID();
        return id.randomIDinvoice(invoiceIDs);
    }

    public double getTotalAmount(Booking booking, ArrayList<Service> services) {
        double tienPhong = booking.calculateTotalRoomCost(rDao.getPriceById(booking.getRoomId()));
        double tienDichVu = 0;
        for (Service service : services) {
            tienDichVu += service.getPrice();
            System.out.println("Service Price: " + service.getPrice());
        }
        return tienPhong + tienDichVu;
    }

    public boolean addHoaDon(Booking selected) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(generateInvoiceID()); // Hàm sinh ID hóa đơn
        invoice.setBookingID(selected.getBookingId());
        String serviceBookingID = serviceBookingDao.getByRoomId(selected.getRoomId());
        invoice.setServiceBookingID(serviceBookingID);
        invoice.setTotalAmount(getTotalAmount(selected, serviceBookingDetailDao.getServicesByBookingId(serviceBookingID))); // Tổng tiền phòng + thuế 10%
        System.out.println("Total Amount: " + invoice.getTotalAmount());
        invoice.setTax(10);         // Bạn có thể tính thuế nếu cần
        invoice.setDiscount(1 + serviceBookingDetailDao.getServicesByBookingId(selected.getBookingId()).size());    // Không có dịch vụ -> discount = 1
        invoice.setIssueDate(new java.sql.Date(System.currentTimeMillis()));
        invoice.setStatus("Đã Thanh toán");
        boolean checkInvoice = invoiceDao.insert(invoice);
        return checkInvoice;
    }
    public boolean addCardLevel(double totalAmount, String customerId) {
        CardLevelDao cardLevelDao = new CardLevelDao();
        // Kiểm tra xem khách hàng đã có cấp độ thẻ chưa
        CardLevel existingCard = cardLevelDao.selectByCustomerId(customerId);

        if (existingCard != null) {
            // Cập nhật cấp độ thẻ hiện có
            double newTotalAmount = existingCard.getTotalAmount() + totalAmount;
            existingCard.setTotalAmount(newTotalAmount);
            existingCard.updateLevelBasedOnAmount();
            return cardLevelDao.update(existingCard);
        } else {
            // Tạo cấp độ thẻ mới cho khách hàng
            CardLevel newCardLevel = new CardLevel();
            newCardLevel.setCustomerID(customerId);
            newCardLevel.setTotalAmount(totalAmount);
            // Xác định tên cấp độ và mô tả dựa trên tổng số tiền
            String levelName = CardLevel.determineLevelName(totalAmount);
            String description = CardLevel.getLevelDescription(levelName);

            newCardLevel.setLevelName(levelName);
            newCardLevel.setDescription(description);

            return cardLevelDao.insert(newCardLevel);
        }
    }
}
