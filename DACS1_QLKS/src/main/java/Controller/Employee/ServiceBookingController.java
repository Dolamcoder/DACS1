package Controller.Employee;
import Dao.Employee.*;
import Model.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import Alert.alert;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServiceBookingController {
    TaoID taoId = new TaoID();
    ServiceBookingDao serviceBookingDao = new ServiceBookingDao();
    ServiceDao sDao = new ServiceDao();
    CustomerDao cusDao = new CustomerDao();
    RoomBookingDao rDao = new RoomBookingDao();
    ServiceBookingDetailDao detailDao = new ServiceBookingDetailDao();
    alert al = new alert();
    double tongBill = 0;
    RoomDao roomDao=new RoomDao();
    String roomID;
    @FXML
    private Button confirm;

    @FXML
    private TextField idCustomer;

    @FXML
    private TableColumn<Service, Integer> idServecie;

    @FXML
    private Button remove;

    @FXML
    private Button add;

    @FXML
    private TableColumn<Service, String> moTaService;

    @FXML
    private TextField nameCustomer;

    @FXML
    private TableColumn<Service, String> nameService;

    @FXML
    private TableColumn<Service, String> nameService2;

    @FXML
    private TableColumn<Service, Double> priceService;

    @FXML
    private TableColumn<Service, Double> priceService2;

    @FXML
    private TableView<Service> selectedServiceTable;

    @FXML
    private TableView<Service> serviceTable;

    @FXML
    private ComboBox<String> slectRoom;

    @FXML
    private TableColumn<Service, Integer> stt;

    @FXML
    private Label tongTien;

    public void initialize() {
        loadCustomerComboBox();
        loadServiceTable();
        loadSelectedServiceTable();
        this.tongTien.setText("Tổng tiền: 0VNĐ");
    }

    private void loadCustomerComboBox() {
        ArrayList<Booking> books = rDao.selectAll();
        this.slectRoom.getItems().clear();
        for (Booking x : books) {
            if(x.getStatus().equals("Đã xác nhận") || x.getStatus().equals("Đang ở")) {
                this.slectRoom.getItems().add(x.getRoomId());
            }
        }
    }
    @FXML
    private void handleRoomSelection() {
        String roomID = slectRoom.getSelectionModel().getSelectedItem();
        addThongTinKhachHang();
        loadServicesForRoom(roomID);
    }
    private void loadServiceTable() {
        ArrayList<Service> allServices = sDao.selectAll();
        ArrayList<Service> serviceList = new ArrayList<>();
        for (Service s : allServices) {
            if (s.getPrice() > 0) {
            serviceList.add(s);
            }
         }
    idServecie.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getServiceId()).asObject());
    nameService.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
    priceService.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getPrice()));
    moTaService.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescription()));

    ObservableList<Service> obsList = FXCollections.observableArrayList(serviceList);
    serviceTable.setItems(obsList);
}

private void loadSelectedServiceTable() {
    ArrayList<Service> allServices = sDao.selectAll();
    ArrayList<Service> freeServiceList = new ArrayList<>();
    for (Service s : allServices) {
        if (s.getPrice() == 0) {
            freeServiceList.add(s);
        }
    }
    stt.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getServiceId()).asObject());
    nameService2.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
    priceService2.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getPrice()));

    ObservableList<Service> obsList = FXCollections.observableArrayList(freeServiceList);
    selectedServiceTable.setItems(obsList);
}

private void loadServicesForRoom(String roomID) {
    selectedServiceTable.getItems().clear();
    serviceTable.getItems().clear();
    tongBill = 0;
    this.tongTien.setText("Tổng tiền: 0VNĐ");

    loadServiceTable();

    String serviceBookingId = serviceBookingDao.getByRoomId(roomID);
    if (serviceBookingId != null) {
        ArrayList<ServiceBookingDetail> details = detailDao.getByServiceBookingId(serviceBookingId);
        ObservableList<Service> selectedServices = FXCollections.observableArrayList();
        for (ServiceBookingDetail detail : details) {
            Service service = sDao.selectById(detail.getServiceID());
            if (service != null) {
                selectedServices.add(service);
                tongBill += service.getPrice();
                serviceTable.getItems().removeIf(s -> s.getServiceId() == service.getServiceId());
            }
        }
        selectedServiceTable.setItems(selectedServices);
        this.tongTien.setText("Tổng tiền: " + String.format("%.0f VNĐ", tongBill));
    }
}

@FXML
private void handleAddService() {
    Service selected = serviceTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
        selectedServiceTable.getItems().add(selected);
        serviceTable.getItems().remove(selected);
        tongBill += selected.getPrice();
        tongTien.setText("Tổng tiền: " + String.format("%.0f VNĐ", tongBill));
    } else {
        al.showErrorAlert("Vui lòng chọn một dịch vụ để thêm.");
    }
}

@FXML
private void handleRemoveService() {
    Service selected = selectedServiceTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
        serviceTable.getItems().add(selected);
        selectedServiceTable.getItems().remove(selected);
        tongBill -= selected.getPrice();
        tongTien.setText("Tổng tiền: " + String.format("%.0f VNĐ", tongBill));
    } else {
        al.showErrorAlert("Vui lòng chọn một dịch vụ để xoá.");
    }
}

@FXML
private void handlePlaceServiceBooking() {
    String customerID = idCustomer.getText();
    if (customerID == null || customerID.isEmpty()) {
        al.showErrorAlert("Vui lòng chọn khách hàng.");
        return;
    }

    ObservableList<Service> selectedServices = selectedServiceTable.getItems();
    if (selectedServices == null || selectedServices.isEmpty()) {
        al.showErrorAlert("Vui lòng chọn ít nhất một dịch vụ.");
        return;
    }

    String existingBookingID = serviceBookingDao.getByRoomId(slectRoom.getSelectionModel().getSelectedItem());
    String servicebookingID;
    if (existingBookingID != null) {
        servicebookingID = existingBookingID;
        boolean deleted = detailDao.deleteByServiceBookingID(servicebookingID);
        if (!deleted) {
            al.showErrorAlert("Lỗi khi xóa chi tiết đặt dịch vụ cũ.");
            return;
        }
        ServiceBooking existingBooking = serviceBookingDao.searchById(servicebookingID);
        existingBooking.setQuantity(selectedServices.size());
        existingBooking.setTotalAmount(tongBill);
        boolean updated = serviceBookingDao.update(existingBooking);
        if (!updated) {
            al.showErrorAlert("Lỗi khi cập nhật thông tin đặt dịch vụ.");
            return;
        }
    } else {
        servicebookingID = generateNewServiceBookingID();
        boolean insertedBooking = saveServiceBooking(servicebookingID, roomID , selectedServices);
        if (!insertedBooking) {
            al.showErrorAlert("Lỗi lưu đặt dịch vụ.");
            return;
        }
    }

    boolean insertedDetails = saveServiceBookingDetails(servicebookingID, selectedServices);
    if (!insertedDetails) {
        al.showErrorAlert("Lỗi lưu chi tiết dịch vụ.");
        return;
    }
    al.showInfoAlert("Đặt dịch vụ thành công");
    tongBill = 0;
    tongTien.setText("Tổng tiền: 0VNĐ");
}

private String generateNewServiceBookingID() {
    ArrayList<ServiceBooking> existingBookings = serviceBookingDao.selectAll();
    Set<String> existingBookingIDs = new HashSet<>();
    for (ServiceBooking sb : existingBookings) {
        existingBookingIDs.add(sb.getServiceBookingID());
    }
    return taoId.randomIDServiceBooking(existingBookingIDs);
}

private boolean saveServiceBooking(String bookingID, String customerID, ObservableList<Service> selectedServices) {
    int totalQuantity = selectedServices.size();
    double totalAmount = tongBill;
    ServiceBooking newBooking = new ServiceBooking(bookingID, customerID, totalQuantity, totalAmount);
    return serviceBookingDao.insert(newBooking);
}

private boolean saveServiceBookingDetails(String bookingID, ObservableList<Service> selectedServices) {
    boolean allInserted = true;
    for (Service s : selectedServices) {
        ServiceBookingDetail detail = new ServiceBookingDetail(bookingID, s.getServiceId());
        boolean inserted = detailDao.insert(detail);
        if (!inserted) {
            allInserted = false;
        }
    }
    return allInserted;
}
public void addThongTinKhachHang() {
    roomID = slectRoom.getSelectionModel().getSelectedItem();
    if (roomID == null || roomID.isEmpty()) {
        al.showErrorAlert("Vui lòng chọn phòng cần đặt trạng dịch vụ trước");
        return;
    }
    Booking booking = rDao.getBookingByRoomId(roomID);
    Customer customer = cusDao.searchById(booking.getCustomerId());
    if (customer == null) {
        al.showErrorAlert("Không tìm thấy thông tin khách hàng cho phòng này");
        return;
    }
    idCustomer.setText(customer.getId());
    nameCustomer.setText(customer.getName());
}
}