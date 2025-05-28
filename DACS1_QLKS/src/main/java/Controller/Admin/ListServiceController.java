package Controller.Admin;

import Controller.Login.LoginController;
import Dao.Employee.ServiceBookingDetailDao;
import Dao.Employee.ServiceDao;
import Model.Service;
import Alert.alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListServiceController implements Initializable {

    // FXML Injected Controls
    @FXML private Button addButton;
    @FXML private TableColumn<Service, String> descriptionColum;
    @FXML private TextArea descriptionText;
    @FXML private TableColumn<Service, String> nameServiceColum;
    @FXML private TextField nameServiceText;
    @FXML private TextField numberText;
    @FXML private TableColumn<Service, Double> priceColum;
    @FXML private Button removeButton;
    @FXML private Button saveButton;
    @FXML private TableColumn<Service, Integer> serviceIDColum;
    @FXML private TextField serviceText;
    @FXML private TextField timKiemText;
    @FXML private Button updateButton;

    // Missing in FXML but needed
    @FXML private TextField searchTextField;
    @FXML private TableView<Service> tableView;
    @FXML private ProgressBar progressBar;

    // Data and utilities
    private ServiceDao serviceDao;
    private ObservableList<Service> serviceList = FXCollections.observableArrayList();
    private alert al=new alert();
    private Service selectedService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize DAO
        serviceDao = new ServiceDao();
        setupTableColumns();

        // Load data
        loadServices();
        // Set up search functionality
        setupSearch();
    }

    private void setupTableColumns() {
        serviceIDColum.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        nameServiceColum.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColum.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionColum.setCellValueFactory(new PropertyValueFactory<>("description"));
        // Set selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedService = newSelection;
                showServiceDetails(selectedService);
            }
        });
    }

    private void loadServices() {
        List<Service> services = serviceDao.selectAll();
        serviceList.clear();
        serviceList.addAll(services);
        tableView.setItems(serviceList);
    }
    private void setupSearch() {
        if (timKiemText != null) {
            timKiemText.textProperty().addListener((observable, oldValue, newValue) -> {
                filterServices(newValue);
            });
        }
    }

    private void filterServices(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            tableView.setItems(serviceList);
            return;
        }

        keyword = keyword.toLowerCase();
        ObservableList<Service> filteredList = FXCollections.observableArrayList();

        for (Service service : serviceList) {
            if ((service.getServiceId()+"").equals(keyword) ||
                    service.getName().toLowerCase().contains(keyword) ||
                    String.valueOf(service.getPrice()).contains(keyword) ||
                    (service.getDescription() != null &&
                            service.getDescription().toLowerCase().contains(keyword))) {
                filteredList.add(service);
            }
        }

        tableView.setItems(filteredList);
    }

    private void showServiceDetails(Service service) {
        serviceText.setText(service.getServiceId()+"");
        nameServiceText.setText(service.getName());
        numberText.setText(String.valueOf(service.getPrice()));
        descriptionText.setText(service.getDescription());
    }

    private void clearFields() {
        serviceText.clear();
        nameServiceText.clear();
        numberText.clear();
        descriptionText.clear();
    }

    private boolean validateFields() {
        if (serviceText.getText().trim().isEmpty() ||
                nameServiceText.getText().trim().isEmpty() ||
                numberText.getText().trim().isEmpty()) {
            al.showErrorAlert("Vui lòng điền đầy đủ thông tin dịch vụ!");
            return  false;
        }
        try {
            Double.parseDouble(numberText.getText());
            return true;
        } catch (NumberFormatException e) {
            al.showErrorAlert("Giá dịch vụ phải là số!");
            return false;
        }
    }

    public void addService() {
        if (!validateFields()) return;
        String serviceID = serviceText.getText().trim();
        String name = nameServiceText.getText().trim();
        double price = Double.parseDouble(numberText.getText().trim());
        String description = descriptionText.getText().trim();

        Service existingService = serviceDao.selectById(Integer.parseInt(serviceID));
        if (existingService != null) {
            al.showErrorAlert("Mã dịch vụ đã tồn tại!");
            return;
        }
        Service newService = new Service(Integer.parseInt(serviceID), name, price, description);

        if (serviceDao.insert(newService)) {
            al.showInfoAlert("Thêm dịch vụ thành công!");
            AuditLogController.getAuditLog("service", serviceID, "Thêm dịch vụ mới", "admin");
            serviceList.add(newService);
            clearFields();
        } else {
            al.showErrorAlert("Thêm dịch vụ thất bại!");
        }
    }

    public void updateService() {
        if (selectedService == null) {
            al.showErrorAlert("Vui lòng chọn dịch vụ cần cập nhật!");
            return;
        }

        if (!validateFields()) return;

        String serviceID = serviceText.getText().trim();
        String name = nameServiceText.getText().trim();
        double price = Double.parseDouble(numberText.getText().trim());
        String description = descriptionText.getText().trim();

        if (!serviceID.equals(selectedService.getServiceId()+"")) {
            al.showErrorAlert("Không thể thay đổi mã dịch vụ!");
            return;
        }

        Service updatedService = new Service(Integer.parseInt(serviceID), name, price, description);

        if (serviceDao.update(updatedService)) {
            al.showInfoAlert("Cập nhật dịch vụ thành công!");
            AuditLogController.getAuditLog("service", serviceID, "Cập nhật dịch vụ", "admin");
            loadServices();
            clearFields();
        } else {
            al.showErrorAlert("Cập nhật dịch vụ thất bại!");
        }
    }

    public void removeService() {
        Service selectSevice= tableView.getSelectionModel().getSelectedItem();
        if (selectSevice == null) {
            al.showErrorAlert("Vui lòng chọn dịch vụ cần xóa!");
            return;
        }
        ServiceBookingDetailDao serviceBookingDetailDao=new ServiceBookingDetailDao();
        boolean success1= serviceBookingDetailDao.deleteByServiceID(selectSevice.getServiceId());
        boolean success2=serviceDao.delete(selectSevice.getServiceId()+"");
        if(success2) {
            al.showInfoAlert("Xóa dịch vụ thành công!");
            AuditLogController.getAuditLog("service", selectSevice.getServiceId()+"", "Xóa dịch vụ", "admin");
            serviceList.remove(selectSevice);
            clearFields();
        }
    }
    public void saveToExcel() {
        // Set up file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách dịch vụ");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("DanhSachDichVu.xlsx");

        Window window = saveButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            progressBar.setProgress(0);

            Task<Void> exportTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Create workbook and sheet
                    Workbook workbook = new XSSFWorkbook();
                    Sheet sheet = workbook.createSheet("Danh sách dịch vụ");

                    // Create header row
                    Row headerRow = sheet.createRow(0);
                    String[] headers = {"Mã Dịch Vụ", "Tên Dịch Vụ", "Giá Dịch Vụ", "Mô Tả Dịch Vụ"};

                    // Style for header
                    CellStyle headerStyle = workbook.createCellStyle();
                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerStyle.setFont(headerFont);

                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                        cell.setCellStyle(headerStyle);
                    }

                    // Create data rows
                    List<Service> services = serviceList;
                    int totalServices = services.size();

                    for (int i = 0; i < services.size(); i++) {
                        if (isCancelled()) break;

                        Service service = services.get(i);
                        Row row = sheet.createRow(i + 1);

                        row.createCell(0).setCellValue(service.getServiceId());
                        row.createCell(1).setCellValue(service.getName());
                        row.createCell(2).setCellValue(service.getPrice());
                        row.createCell(3).setCellValue(service.getDescription());

                        // Update progress
                        updateProgress(i + 1, totalServices);
                    }

                    // Auto-size columns
                    for (int i = 0; i < headers.length; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    // Write to file
                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        workbook.write(fileOut);
                    }

                    workbook.close();
                    return null;
                }
            };

            // Bind progress bar
            progressBar.progressProperty().bind(exportTask.progressProperty());

            // Set up completion handlers
            exportTask.setOnSucceeded(e -> {
                progressBar.progressProperty().unbind();
                progressBar.setProgress(1.0);
                al.showInfoAlert("Xuất danh sách dịch vụ thành công!");
                AuditLogController.getAuditLog("Service","Tất cả bản ghi", "Xuất danh sách dịch vụ " , LoginController.account.getName());
            });

            exportTask.setOnFailed(e -> {
                progressBar.progressProperty().unbind();
                progressBar.setProgress(0);
                Throwable exception = exportTask.getException();
                al.showErrorAlert("Lỗi khi xuất file Excel: " + exception.getMessage());
                exception.printStackTrace();
            });

            // Start the task in a background thread
            new Thread(exportTask).start();
        }
    }
}