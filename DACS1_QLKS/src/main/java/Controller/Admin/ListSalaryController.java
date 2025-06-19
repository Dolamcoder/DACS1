package Controller.Admin;

import Controller.Login.LoginController;
import Dao.Admin.EmployeeDao;
import Dao.Admin.EmployeeReviewDAO;
import Dao.Admin.SalaryDao;
import Model.Employee;
import Model.EmployeeReview;
import Model.Salary;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import Alert.Alert;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ListSalaryController implements Initializable {
    EmployeeDao empDao=new EmployeeDao();
    SalaryDao saDao=new SalaryDao();
    EmployeeReviewDAO empReviewDAO = new EmployeeReviewDAO();
    private ObservableList<Salary> salaryList= FXCollections.observableArrayList();
    Alert al=new Alert();
    @FXML
    private Button add;
    @FXML
    private DatePicker date;
    @FXML
    private TableColumn<Salary, ?> amountColum;

    @FXML
    private TextField amountText;

    @FXML
    private TableColumn<Salary, String> chucVuColum;

    @FXML
    private ComboBox<String> chucVuText;

    @FXML
    private TableColumn<Salary, String> employeeIDColum;

    @FXML
    private ComboBox<String> employeeIDText;

    @FXML
    private TableColumn<Salary, String> nameColum;

    @FXML
    private TextField nameText;

    @FXML
    private TableColumn<Salary, Integer> phuCapColum;

    @FXML
    private TextField phuCapText;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button remove;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TableColumn<Salary, Integer> salaryIDColum;

    @FXML
    private TextField salaryIDText;

    @FXML
    private TableView<Salary> salaryTable;

    @FXML
    private FontAwesomeIcon save;

    @FXML
    private Button saveButton;

    @FXML
    private TextField searchText;

    @FXML
    private ComboBox<String> select;

    @FXML
    private Button update;

    @FXML
    void add(ActionEvent event) {
        Salary salary=getSalary();
        if(saDao.getSalaryByID(salary.getSalaryID())!= null) {
            al.showErrorAlert("Mã lương đã tồn tại");
            return;
        }
        if(saDao.insertSalary(salary)){
            al.showInfoAlert("Thêm thành công");
            salaryList.add(salary);
            AuditLogController.getAuditLog("Salary", salary.getSalaryID()+"", "Thêm lương", LoginController.account.getName());
            clearFields();
        } else {
            al.showErrorAlert("Thêm  thất bại");
        }
    }

    @FXML
    void remove(ActionEvent event) {
        Salary selectedSalary = salaryTable.getSelectionModel().getSelectedItem();
        if (selectedSalary == null) {
            al.showErrorAlert("Vui lòng chọn một mục để xóa");
            return;
        }
        int salaryID = selectedSalary.getSalaryID();
        if (saDao.deleteSalary(salaryID)) {
            al.showInfoAlert("Xóa thành công");
            salaryList.remove(selectedSalary);
            AuditLogController.getAuditLog("Salary", salaryID+"", "Xóa lương", LoginController.account.getName());
            clearFields();
        } else {
            al.showErrorAlert("Xóa thất bại");
        }
    }
    @FXML
    void save(ActionEvent event) {
        String dateValue = date.getValue() != null ? date.getValue().toString() : "";
        if (dateValue.isEmpty()) {
            al.showErrorAlert("Vui lòng chọn ngày");
            return;
        }

        List<EmployeeReview> reviews = getListEmployeeReview();
        if (reviews.isEmpty()) {
            al.showErrorAlert("Không có đánh giá nào trong tháng này");
            return;
        }

        // Map nhân viên với bonus
        Map<String, Integer> bonusMap = new HashMap<>();
        for (EmployeeReview review : reviews) {
            int bonus = getTotalBonus(review.getRatingScore());
            review.setBonusAmount(bonus);
            empReviewDAO.updateReview(review);
            bonusMap.put(review.getEmployeeID(), bonus);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Lương nhân viên");
            Row header = sheet.createRow(0);
            String[] headers = {"Salary ID", "Employee ID", "Name", "Position", "Amount", "Transport Allowance", "Bonus", "Total"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = 1;
            for (Salary s : salaryList) {
                Row row = sheet.createRow(rowIndex++);
                int bonus = bonusMap.getOrDefault(s.getEmployeeID(), 0);
                int total = (int) s.getAmount() + s.getTransportAllowance() + bonus;

                row.createCell(0).setCellValue(s.getSalaryID());
                row.createCell(1).setCellValue(s.getEmployeeID());
                row.createCell(2).setCellValue(s.getName());
                row.createCell(3).setCellValue(s.getPosition());
                row.createCell(4).setCellValue(s.getAmount());
                row.createCell(5).setCellValue(s.getTransportAllowance());
                row.createCell(6).setCellValue(bonus);
                row.createCell(7).setCellValue(total);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
            fileChooser.setSelectedFile(new java.io.File("salary_report.xlsx"));
            int result = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                try (FileOutputStream fileOut = new FileOutputStream(selectedFile)) {
                    workbook.write(fileOut);
                    al.showInfoAlert("Xuất Excel thành công: " + selectedFile.getAbsolutePath());
                    AuditLogController.getAuditLog("Salary", "Tất cả", "Xuất báo cáo lương", LoginController.account.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            al.showErrorAlert("Xuất Excel thất bại: " + e.getMessage());
        }
    }


    @FXML
    void update(ActionEvent event) {
        Salary salary= getSalary();
        if(saDao.updateSalary(salary)){
            al.showInfoAlert("Cập nhật thành công");
            AuditLogController.getAuditLog("Salary", salary.getSalaryID()+"", "Cập nhật lương", LoginController.account.getName());
            loadData();
            AuditLogController.getAuditLog("Salary", salary.getSalaryID()+"", "Cập nhật lương", LoginController.account.getName());
            return;
        } else {
            al.showErrorAlert("Cập nhật thất bại");
            return;
        }
    }
    public void setUpTable(){
        salaryIDColum.setCellValueFactory(new PropertyValueFactory<>("salaryID"));
        employeeIDColum.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        amountColum.setCellValueFactory(new PropertyValueFactory<>("amount"));
        phuCapColum.setCellValueFactory(new PropertyValueFactory<>("transportAllowance"));
        nameColum.setCellValueFactory(new PropertyValueFactory<>("name"));
        chucVuColum.setCellValueFactory(new PropertyValueFactory<>("position"));
        chucVuText.setItems(FXCollections.observableArrayList(
                "Lễ tân", "Phục vụ", "Buồng phòng", "Bảo vệ", "Kỹ thuật", "Quản lý"
        ));
        List<Employee> employeeList = empDao.getAllEmployees();
        employeeIDText.setItems(FXCollections.observableArrayList(
                employeeList.stream().map(Employee::getId).toList() // Lấy danh sách ID nhân viên
        ));
        select.setItems(FXCollections.observableArrayList("Danh sách nhân viên", "Đánh giá nhân viên"));
        salaryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showSalaryDetails(newSelection);
            }
        });
    }
    public void loadData(){
       salaryList.clear();
       salaryList.addAll(saDao.getAllSalaries());
       salaryTable.setItems(salaryList);
    }
    public void clearFields() {
        salaryIDText.clear();
        employeeIDText.getSelectionModel().clearSelection();
        amountText.clear();
        phuCapText.clear();
        nameText.clear();
        chucVuText.getSelectionModel().clearSelection();
    }
    public void showSalaryDetails(Salary salary) {
        if (salary != null) {
            salaryIDText.setText(salary.getSalaryID()+"");
            employeeIDText.setValue(salary.getEmployeeID());
            amountText.setText(String.valueOf(salary.getAmount()));
            phuCapText.setText(String.valueOf(salary.getTransportAllowance()));
            nameText.setText(salary.getName());
            chucVuText.setValue(salary.getPosition());
        } else {
            clearFields();
        }
    }
    public Salary getSalary(){
        String id = salaryIDText.getText().trim();
        String employeeID = employeeIDText.getValue();
        int amount = Integer.parseInt(amountText.getText().trim());
        int transportAllowance = Integer.parseInt(phuCapText.getText().trim());
        String name = nameText.getText().trim();
        String position = chucVuText.getValue();
        if(id.isEmpty() || employeeID == null || amount <= 0 || transportAllowance < 0 || name.isEmpty() || position == null) {
            al.showErrorAlert("Vui lòng nhập đầy đủ thông tin");
            return null;
        }
        Salary salary = new Salary();
        salary.setSalaryID(Integer.parseInt(id));
        salary.setEmployeeID(employeeID);
        salary.setAmount(amount);
        salary.setTransportAllowance(transportAllowance);
        salary.setName(name);
        salary.setPosition(position);
        return salary;
    }
    @FXML
    void selectEmployee(ActionEvent event){
        String selectedEmployeeID = employeeIDText.getValue();
        if (selectedEmployeeID != null) {
            Employee emp = empDao.findEmployeeById(selectedEmployeeID);
            Salary salary= saDao.getSalaryByEmployeeID(selectedEmployeeID);
            if (emp != null) {
                nameText.setText(emp.getName());
                chucVuText.setValue(emp.getPosition());
                if(salary!=null){
                    salaryIDText.setText(String.valueOf(salary.getSalaryID()));
                    amountText.setText(String.valueOf(salary.getAmount()));
                    phuCapText.setText(String.valueOf(salary.getTransportAllowance()));
                } else {
                    salaryIDText.clear();
                    amountText.clear();
                    phuCapText.clear();
                }
            }
        } else {
            nameText.clear();
            chucVuText.getSelectionModel().clearSelection();
        }
    }
    private void setupSearchFunction() {
        // Add listener to search text field to filter results as user types
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEmployees(newValue);
        });
    }

    private void filterEmployees(String keyword) {
        if (keyword == null) {
            keyword = "";
        }

        // Convert to lowercase for case-insensitive search
        String searchTerm = keyword.trim().toLowerCase();

        // If search term is empty, show all employees
        if (searchTerm.isEmpty()) {
            salaryTable.setItems(salaryList);
            return;
        }

        // Filter the employee list
        ObservableList<Salary> filteredList = FXCollections.observableArrayList();
        for (Salary x : salaryList) {
            String lowerSearchTerm = searchTerm.toLowerCase();  // Chuẩn hóa
            String salaryID = String.valueOf(x.getSalaryID());
            String employeeID = x.getEmployeeID().toLowerCase();
            String name = x.getName().toLowerCase();
            String position = x.getPosition().toLowerCase();

            // Chuyển lương/phụ cấp thành chuỗi để so sánh
            String amountStr = String.valueOf(x.getAmount());
            String allowanceStr = String.valueOf(x.getTransportAllowance());

            if (salaryID.contains(lowerSearchTerm)
                    || employeeID.contains(lowerSearchTerm)
                    || name.contains(lowerSearchTerm)
                    || position.contains(lowerSearchTerm)
                    || amountStr.contains(lowerSearchTerm)
                    || allowanceStr.contains(lowerSearchTerm)
            ) {
                filteredList.add(x);
            }
        }

        // Update table view with filtered results
        salaryTable.setItems(filteredList);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpTable();
        loadData();
        setupSearchFunction();
    }

    public void clickSelect(ActionEvent actionEvent) throws IOException {
        String selectedOption = select.getValue();
        System.out.println("Selected option: " + selectedOption);
        if (selectedOption == null) {
            al.showErrorAlert("Vui lòng chọn một tùy chọn!");
            return;
        }
        if (selectedOption.equals("Danh sách nhân viên")) {
            AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListStaff.fxml"));
            this.rootPane.getChildren().clear();
            this.rootPane.getChildren().add(selectPane);
        } else if (selectedOption.equals("Đánh giá nhân viên")) {
            AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListEmployeeReview.fxml"));
            this.rootPane.getChildren().clear();
            this.rootPane.getChildren().add(selectPane);
        }
    }
    public List<EmployeeReview> getListEmployeeReview (){
        int month = date.getValue().getMonthValue();
        int year = date.getValue().getYear();
        List<EmployeeReview> reviews =empReviewDAO.getReviewsByDate(month, year);
        return reviews;
    }
    public int getTotalBonus(int ratingScore) {
    return (ratingScore -30)* 200000; // Giả sử mỗi điểm đánh giá tương ứng với 200.000 đồng
    }
    public void updateBonus(){
        List<EmployeeReview> reviews = getListEmployeeReview();
        if (reviews.isEmpty()) {
            al.showErrorAlert("Không có đánh giá nào trong tháng này");
            return;
        }
        for (EmployeeReview review : reviews) {
            int bonus = getTotalBonus(review.getRatingScore());
            review.setBonusAmount(bonus);
            empReviewDAO.updateReview(review);
        }
        al.showInfoAlert("Cập nhật tiền thưởng thành công");
        AuditLogController.getAuditLog("Employee Review", "Tất cả", "Cập nhật tiền thưởng", LoginController.account.getName());
    }
}
