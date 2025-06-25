package Controller.Admin;

import Controller.Login.LoginController;
import Dao.Admin.EmployeeDao;
import Dao.Admin.EmployeeReviewDAO;
import Model.Employee;
import Model.EmployeeReview;
import Alert.Alert;
import Util.JDBC;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListStaffController implements Initializable {

    @FXML private TableColumn<Employee, LocalDate> BirdDateColum;
    @FXML private DatePicker BirdText;
    @FXML private TableColumn<Employee, LocalDate> HireDateColum;
    @FXML private TableView<Employee> accountTable;
    @FXML private Button add;
    @FXML private ComboBox<String> chucVuText;
    @FXML private Button diemDanh;
    @FXML private TableColumn<Employee, String> emailColum;
    @FXML private TextField emailText;
    @FXML private TableColumn<Employee, String> genderColum;
    @FXML private ComboBox<String> gioiTinhText;
    @FXML private ComboBox<String> select;
    @FXML private DatePicker hireDateText;
    @FXML private TableColumn<Employee, String> idColum;
    @FXML private TextField idText;
    @FXML private TableColumn<Employee, String> nameColum;
    @FXML private TextField nameText;
    @FXML private TableColumn<Employee, String> phoneColum;
    @FXML private TextField phoneText;
    @FXML private TableColumn<Employee, String> positionColum;
    @FXML private ProgressBar progressBar;
    @FXML private Button remove;
    @FXML private FontAwesomeIcon save;
    @FXML private Button saveButton;
    @FXML private TextField searchText;
    @FXML private Button update;
    @FXML private Button xemDanhGia;
    @FXML private AnchorPane rootPane;
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private EmployeeDao employeeDao;
    private EmployeeReviewDAO reviewDAO;
    private Alert alert = new Alert();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeDao = new EmployeeDao();
        reviewDAO = new EmployeeReviewDAO();

        // Set up table columns
        idColum.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColum.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColum.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColum.setCellValueFactory(new PropertyValueFactory<>("phone"));
        genderColum.setCellValueFactory(new PropertyValueFactory<>("gender"));
        BirdDateColum.setCellValueFactory(new PropertyValueFactory<>("birth"));
        HireDateColum.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        positionColum.setCellValueFactory(new PropertyValueFactory<>("position"));

        // Set up gender combobox
        gioiTinhText.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));

        // Set up position combobox
        chucVuText.setItems(FXCollections.observableArrayList(
                "Lễ tân", "Phục vụ", "Buồng phòng", "Bảo vệ", "Kỹ thuật", "Quản lý"
        ));
        select.setItems(FXCollections.observableArrayList("Lương nhân viên", "Đánh giá nhân viên"));
        // Set default dates
        hireDateText.setValue(LocalDate.now());
        // Load employees
        loadEmployees();
        // Initialize search functionality
        setupSearchFunction();

        // Row click handler
        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showEmployeeDetails(newSelection);
            }
        });
    }

    private void loadEmployees() {
        employeeList.clear();
        employeeList.addAll(employeeDao.getAllEmployees());
        accountTable.setItems(employeeList);
    }

    private void showEmployeeDetails(Employee employee) {
        idText.setText(employee.getId());
        nameText.setText(employee.getName());
        emailText.setText(employee.getEmail());
        phoneText.setText(employee.getPhone());
        gioiTinhText.setValue(employee.getGender());
        BirdText.setValue(employee.getBirth());
        chucVuText.setValue(employee.getPosition());
        hireDateText.setValue(LocalDate.parse(employee.getHireDate()));
    }

    private void setupSearchFunction() {
        // Add listener to search text field to filter results as user types
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEmployees(newValue);
        });
    }

    private void filterEmployees(String keyword) {
        String searchTerm = (keyword == null) ? "" : keyword.trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            accountTable.setItems(employeeList);
            return;
        }

        ObservableList<Employee> filteredList = employeeList.stream()
                .filter(employee ->
                        employee.getId().toLowerCase().contains(searchTerm) ||
                                employee.getName().toLowerCase().contains(searchTerm) ||
                                employee.getEmail().toLowerCase().contains(searchTerm) ||
                                employee.getPhone().toLowerCase().contains(searchTerm)
                )
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        accountTable.setItems(filteredList);
    }


    @FXML
    void add(ActionEvent event) {
        Employee employee=getEmployee();
        if(employeeDao.findEmployeeById(employee.getId())!=null){
            alert.showErrorAlert("Mã nhân viên đã tồn tại!");
            return;
        }
        // Add employee to database
        if (employeeDao.insertEmployee(employee)) {
            alert.showInfoAlert("Thêm nhân viên thành công!");
            employeeList.add(employee);
            AuditLogController.getAuditLog("Employee", employee.getId(), "Thêm nhân viên: " + employee.getName(), LoginController.account.getName());
            clearFields();
        } else {
            alert.showErrorAlert("Thêm nhân viên thất bại!");
        }
    }
    @FXML
    public void clickSelect(ActionEvent event) throws IOException {
        String selectedOption = select.getValue();
        System.out.println("Selected option: " + selectedOption);
        System.out.println(selectedOption.equals("Lương nhân viên"));
        if (selectedOption == null) {
            System.out.println("thoat");
            alert.showErrorAlert("Vui lòng chọn một tùy chọn!");
            return;
        }
        if (selectedOption.equals("Lương nhân viên")) {
            System.out.println("go to ListSalary.fxml");
            AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListSalary.fxml"));
            this.rootPane.getChildren().clear();
            this.rootPane.getChildren().add(selectPane);
        } else if (selectedOption.equals("Đánh giá nhân viên")) {
            AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListEmployeeReview.fxml"));
            this.rootPane.getChildren().clear();
            this.rootPane.getChildren().add(selectPane);
        }
    }
    @FXML
    void update(ActionEvent event) {
        Employee selectedEmployee = accountTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            alert.showErrorAlert("Vui lòng chọn nhân viên cần cập nhật!");
            return;
        }
        selectedEmployee=getEmployee();
        // Update employee in database
        if (employeeDao.updateEmployee(selectedEmployee)) {
            alert.showInfoAlert("Cập nhật nhân viên thành công!");
            loadEmployees();
            AuditLogController.getAuditLog("Employee", selectedEmployee.getId(), "Cập nhật nhân viên: " + selectedEmployee.getName(), LoginController.account.getName());
            clearFields();
        } else {
            alert.showErrorAlert("Cập nhật nhân viên thất bại!");
        }
    }
    @FXML
    void remove(ActionEvent event) {
        Employee selectedEmployee = accountTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            alert.showErrorAlert("Vui lòng chọn nhân viên cần xóa!");
            return;
        }

        // Confirm deletion
        javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa nhân viên này không?");
        confirmAlert.setContentText("Thao tác này không thể hoàn tác.");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (employeeDao.deleteEmployee(selectedEmployee.getId())) {
                alert.showInfoAlert("Xóa nhân viên thành công!");
                employeeList.remove(selectedEmployee);
                AuditLogController.getAuditLog("Employee", selectedEmployee.getId(), "Xóa nhân viên: " + selectedEmployee.getName(), LoginController.account.getName());
                clearFields();
            } else {
                alert.showErrorAlert("Xóa nhân viên thất bại!");
            }
        }
    }

    @FXML
    void save(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách nhân viên");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("DanhSachNhanVien.xlsx");

        Window window = saveButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            progressBar.setProgress(0);

            Task<Void> exportTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    Workbook workbook = new XSSFWorkbook();
                    Sheet sheet = workbook.createSheet("Danh sách nhân viên");

                    // Tạo style cho header
                    CellStyle headerStyle = workbook.createCellStyle();
                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerStyle.setFont(headerFont);
                    headerStyle.setAlignment(HorizontalAlignment.CENTER);
                    headerStyle.setBorderTop(BorderStyle.THIN);
                    headerStyle.setBorderBottom(BorderStyle.THIN);
                    headerStyle.setBorderLeft(BorderStyle.THIN);
                    headerStyle.setBorderRight(BorderStyle.THIN);

                    // Tạo style cho dữ liệu
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);

                    // Header
                    String[] headers = {"ID", "Họ Tên", "Email", "Số điện thoại", "Giới tính",
                            "Ngày sinh", "Ngày vào làm", "Chức vụ"};
                    Row headerRow = sheet.createRow(0);
                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                        cell.setCellStyle(headerStyle);
                    }

                    // Dữ liệu nhân viên
                    for (int i = 0; i < employeeList.size(); i++) {
                        updateProgress(i, employeeList.size());
                        Employee emp = employeeList.get(i);
                        Row row = sheet.createRow(i + 1);

                        Cell c0 = row.createCell(0); c0.setCellValue(emp.getId()); c0.setCellStyle(cellStyle);
                        Cell c1 = row.createCell(1); c1.setCellValue(emp.getName()); c1.setCellStyle(cellStyle);
                        Cell c2 = row.createCell(2); c2.setCellValue(emp.getEmail()); c2.setCellStyle(cellStyle);
                        Cell c3 = row.createCell(3); c3.setCellValue(emp.getPhone()); c3.setCellStyle(cellStyle);
                        Cell c4 = row.createCell(4); c4.setCellValue(emp.getGender()); c4.setCellStyle(cellStyle);
                        Cell c5 = row.createCell(5); c5.setCellValue(emp.getBirth().toString()); c5.setCellStyle(cellStyle);
                        Cell c6 = row.createCell(6); c6.setCellValue(emp.getHireDate().toString()); c6.setCellStyle(cellStyle);
                        Cell c7 = row.createCell(7); c7.setCellValue(emp.getPosition()); c7.setCellStyle(cellStyle);
                    }

                    // Auto resize cột sau khi ghi dữ liệu xong
                    for (int i = 0; i < headers.length; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    // Ghi file
                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        workbook.write(fileOut);
                    }
                    workbook.close();
                    return null;
                }
            };

            // Ràng buộc ProgressBar
            progressBar.progressProperty().bind(exportTask.progressProperty());

            exportTask.setOnSucceeded(e -> {
                progressBar.progressProperty().unbind();
                progressBar.setProgress(1.0);
                alert.showInfoAlert("Xuất danh sách nhân viên thành công!");
                AuditLogController.getAuditLog("Employee", "Export", "Xuất danh sách nhân viên", LoginController.account.getName());
            });

            exportTask.setOnFailed(e -> {
                progressBar.progressProperty().unbind();
                progressBar.setProgress(0);
                alert.showErrorAlert("Lỗi khi xuất file Excel!");
            });

            new Thread(exportTask).start();
        }
    }

    @FXML
    void handleDiemDanh(ActionEvent event) {
        Employee selectedEmployee = accountTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            alert.showErrorAlert("Vui lòng chọn nhân viên để điểm danh!");
            return;
        }

        // Get current date
        LocalDate today = LocalDate.now();
            // Get all reviews for this employee
            List<EmployeeReview> reviews = reviewDAO.getAllReviews();
            EmployeeReview currentMonthReview = null;
            // Find if there's a review for current month
            for (EmployeeReview review : reviews) {
                if (review.getEmployeeID().equals(selectedEmployee.getId()) &&
                        review.getReviewDate().getMonth() == today.getMonth() &&
                        review.getReviewDate().getYear() == today.getYear()) {
                    currentMonthReview = review;
                    break;
                }
            }
            // If no review exists for current month, create one
            if (currentMonthReview == null) {
                currentMonthReview = new EmployeeReview();
                currentMonthReview.setEmployeeID(selectedEmployee.getId());
                currentMonthReview.setReviewDate(today);
                currentMonthReview.setRatingScore(1); // Start with 1 attendance
                currentMonthReview.setComments("Điểm danh tự động");
                currentMonthReview.setBonusAmount(0.0);

                if (reviewDAO.insertReview(currentMonthReview)) {
                    alert.showInfoAlert("Điểm danh thành công!");
                } else {
                    alert.showErrorAlert("Điểm danh thất bại!");
                }
            } else {
                currentMonthReview.setRatingScore(currentMonthReview.getRatingScore()+1);
                boolean check = reviewDAO.updateReview(currentMonthReview);
                if (check) {
                    alert.showInfoAlert("điểm danh thành công!");
                    AuditLogController.getAuditLog("Employee", selectedEmployee.getId(), selectedEmployee.getName()+" Có mặt", LoginController.account.getName());
                } else {
                    alert.showErrorAlert("điểm danh thất bại!");
                }
            }
    }
    @FXML
    void handleXemDanhGia(ActionEvent event) {
        Employee selectedEmployee = accountTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            alert.showErrorAlert("Vui lòng chọn nhân viên để xem đánh giá!");
            return;
        }
            List<EmployeeReview> reviews = reviewDAO.getAllReviews();
            EmployeeReview currentMonthReview = null;
            LocalDate today = LocalDate.now();

            // Tìm đánh giá trong tháng hiện tại
            for (EmployeeReview review : reviews) {
                if (review.getEmployeeID().equals(selectedEmployee.getId()) &&
                        review.getReviewDate().getMonth() == today.getMonth() &&
                        review.getReviewDate().getYear() == today.getYear()) {
                    currentMonthReview = review;
                    break;
                }
            }

            if (currentMonthReview == null) {
                alert.showInfoAlert("Nhân viên chưa có đánh giá trong tháng này!");
                return;
            }

            // Hiển thị đánh giá bằng Dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Thông tin đánh giá");
            dialog.setHeaderText("Đánh giá của nhân viên: " + selectedEmployee.getName());

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            DecimalFormat moneyFormat = new DecimalFormat("#,###");

            grid.add(new Label("Mã nhân viên:"), 0, 0);
            grid.add(new Label(currentMonthReview.getEmployeeID()), 1, 0);

            grid.add(new Label("Ngày đánh giá:"), 0, 1);
            grid.add(new Label(currentMonthReview.getReviewDate().toString()), 1, 1);

            grid.add(new Label("Điểm đánh giá:"), 0, 2);
            grid.add(new Label(currentMonthReview.getRatingScore() + " điểm"), 1, 2);

            grid.add(new Label("Ghi chú:"), 0, 3);
            grid.add(new Label(
                    currentMonthReview.getComments() != null ? currentMonthReview.getComments() : "Không có"
            ), 1, 3);

            grid.add(new Label("Thưởng:"), 0, 4);
            grid.add(new Label(moneyFormat.format(currentMonthReview.getBonusAmount()) + " VND"), 1, 4);
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
    }
    private void clearFields() {
        idText.clear();
        nameText.clear();
        emailText.clear();
        phoneText.clear();
        gioiTinhText.setValue(null);
        BirdText.setValue(LocalDate.now().minusYears(20));
        hireDateText.setValue(LocalDate.now());
        chucVuText.setValue(null);
        accountTable.getSelectionModel().clearSelection();
        progressBar.setProgress(0);
        searchText.clear();
        BirdText.setValue(null);
        hireDateText.setValue(null);
    }
    public Employee getEmployee(){
        String id = idText.getText().trim();
        String name = nameText.getText().trim();
        String email = emailText.getText().trim();
        String phone = phoneText.getText().trim();
        String gender = gioiTinhText.getValue();
        LocalDate birthDate = BirdText.getValue();
        LocalDate hireDate = hireDateText.getValue();
        String chucVu = chucVuText.getValue();
        Employee selectedEmployee=new Employee();
        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                gender == null || birthDate == null || hireDate == null) {
            alert.showErrorAlert("Vui lòng điền đầy đủ thông tin nhân viên!");
            return null;
        }

        // Update employee object
        selectedEmployee.setId(id);
        selectedEmployee.setName(name);
        selectedEmployee.setEmail(email);
        selectedEmployee.setPhone(phone);
        selectedEmployee.setGender(gender);
        selectedEmployee.setBirth(birthDate);
        selectedEmployee.setHireDate(hireDate);
        selectedEmployee.setPosition(chucVu);
        return selectedEmployee;
    }
}