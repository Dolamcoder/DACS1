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

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServiceBookingController {
    TaoID taoId=new TaoID();
    ServiceBookingDao serviceBookingDao=new ServiceBookingDao();
    ServiceDao sDao = new ServiceDao();
    CustomerDao cusDao = new CustomerDao();
    RoomBookingDao rDao=new RoomBookingDao();
    alert al=new alert();
    double tongBill=0;
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
    private ComboBox<String> slectCustomer;

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
        this.slectCustomer.getItems().clear();
        for (Booking x : books) {
            this.slectCustomer.getItems().add(x.getCustomerId());  // hoặc String.valueOf(c.getId())
        }
    }

    @FXML
    private void handleCustomerSelection() {
        String customerId= slectCustomer.getSelectionModel().getSelectedItem();
        Customer selected=cusDao.searchById(customerId);
        if (selected != null) {
            this.idCustomer.setText(selected.getId());
            this.nameCustomer.setText(selected.getName());
        }
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
    @FXML
    private void handleAddService() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Thêm vào bảng selectedServiceTable
            selectedServiceTable.getItems().add(selected);
            // Xóa khỏi bảng serviceTable
            serviceTable.getItems().remove(selected);
            tongBill+=selected.getPrice();
            tongTien.setText("Tổng tiền: "+String.format("%.0f VNĐ", tongBill));
        } else {
            al.showErrorAlert("Vui lòng chọn một dịch vụ để thêm.");
        }
    }
    @FXML
    private void handleRemoveService() {
        Service selected = selectedServiceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Thêm lại vào bảng serviceTable
            serviceTable.getItems().add(selected);
            // Xóa khỏi bảng selectedServiceTable
            selectedServiceTable.getItems().remove(selected);
            tongBill-=selected.getPrice();
            tongTien.setText("Tổng tiền: "+String.format("%.0f VNĐ", tongBill));
        } else {
            al.showErrorAlert("Vui lòng chọn một dịch vụ để xoá.");
        }
    }

    private void loadSelectedServiceTable() {
        ArrayList<Service> allServices = sDao.selectAll();
        ArrayList<Service> freeServiceList = new ArrayList<>();
        for (Service s : allServices) {
            if (s.getPrice() == 0) {
                freeServiceList.add(s);
            }
        }
        this.stt.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getServiceId()).asObject());
        nameService2.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        priceService2.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getPrice()));

        ObservableList<Service> obsList = FXCollections.observableArrayList(freeServiceList);
        selectedServiceTable.setItems(obsList);
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

        // Tạo bookingID mới
        String newBookingID = generateNewServiceBookingID();

        // Ghi vào bảng ServiceBooking
        boolean insertedBooking = saveServiceBooking(newBookingID, customerID, selectedServices);
        if (!insertedBooking) {
            al.showErrorAlert("Lỗi lưu đặt dịch vụ.");
            return;
        }

        // Ghi vào bảng ServiceBookingDetail
        boolean insertedDetails = saveServiceBookingDetails(newBookingID, selectedServices);
        if (!insertedDetails) {
            al.showErrorAlert("Lỗi lưu chi tiết dịch vụ.");
            return;
        }

        al.showInfoAlert("Đặt dịch vụ thành công");

        // Reset lại form
        idCustomer.clear();
        nameCustomer.clear();
        tongBill = 0;
        tongTien.setText("Tổng tiền: 0VNĐ");
        selectedServiceTable.getItems().clear();
        loadServiceTable();
        slectCustomer.getSelectionModel().clearSelection();
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
        ServiceBookingDetailDao detailDao = new ServiceBookingDetailDao();
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




}
