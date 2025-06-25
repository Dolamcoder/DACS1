package Controller.Admin;

import Dao.Admin.EmployeeDao;
import Dao.DangNhap.LoginDao;
import Model.Account;
import Alert.Alert;
import Model.Employee;
import encryption.maHoaMatKhau;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListAccountController implements Initializable {

    @FXML
    private Button addButton;
    @FXML
    private ComboBox<String> chucVu;
    @FXML
    private ComboBox<String> employIDCombobox;
    @FXML
    private TableColumn<Account, String> EmployeeColum;
    @FXML
    private TableColumn<Account, String> emailColum;

    @FXML
    private TextField emailText;

    @FXML
    private TableColumn<Account, Integer> idColum;
    @FXML
    private TableColumn<Account, String> nameColum;

    @FXML
    private TextField nameText;

    @FXML
    private TableColumn<Account, String> passwordColum;

    @FXML
    private TextField passwordText;

    @FXML
    private TableColumn<Account, String> positionColum;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button removeButton;

    @FXML
    private TableView<Account> accountTable;

    @FXML
    private Button saveButton;

    @FXML
    private TextField searchText;


    @FXML
    private Button updateButton;

    @FXML
    private TableColumn<Account, String> usernameColum;

    @FXML
    private TextField usernameText;

    private LoginDao loginDao;
    private ObservableList<Account> accountList = FXCollections.observableArrayList();
    private ObservableList<String> employIDList = FXCollections.observableArrayList();
    private Alert al=new Alert();
    private ObservableList<String>  chucVuList;
    EmployeeDao empDao=new EmployeeDao();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginDao = new LoginDao();
        chucVuList = FXCollections.observableArrayList("Admin - Quản Lý", "Nhân viên");
        setupTable();
        loadAccounts();
        setupSearch();
        comboBoxEmployeeID();
    }
    private void setupTable() {
        chucVu.setItems(chucVuList);
        idColum.setCellValueFactory(new PropertyValueFactory<>("stt"));
        usernameColum.setCellValueFactory(new PropertyValueFactory<>("userName"));
        passwordColum.setCellValueFactory(new PropertyValueFactory<>("password"));
        nameColum.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColum.setCellValueFactory(new PropertyValueFactory<>("email"));
        positionColum.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        EmployeeColum.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showAccountDetails(newSelection);
            }
        });
    }

    private void loadAccounts() {
        accountList.clear();
        List<Account> accounts = loginDao.getAllAccounts();
        System.out.println("Loaded accounts: " + accounts.size());
        accountList.addAll(accounts);
        accountTable.setItems(accountList);
    }
    public void comboBoxEmployeeID() {
        employIDList.clear();
        for (Employee x: empDao.getAllEmployees()) {
            if (x.getId() != null && !x.getId().isEmpty()) {
                employIDList.add(x.getId());
            }
        }
        employIDCombobox.setItems(employIDList);
    }

    private void setupSearch() {
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAccounts(newValue);
            System.out.println("Searching for: " + newValue);
        });
    }

    private void filterAccounts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            accountTable.setItems(accountList);
            return;
        }

        String lowerKeyword = keyword.toLowerCase();
        List<Account> filtered = accountList.stream()
                .filter(account ->
                        account.getUserName().toLowerCase().contains(lowerKeyword) ||
                                account.getName().toLowerCase().contains(lowerKeyword) ||
                                account.getEmail().toLowerCase().contains(lowerKeyword)
                )
                .collect(Collectors.toList());

        accountTable.setItems(FXCollections.observableArrayList(filtered));
    }


    private void showAccountDetails(Account account) {
        employIDCombobox.setValue(account.getEmployeeId());
        usernameText.setText(account.getUserName());
        passwordText.setText(account.getPassword());
        nameText.setText(account.getName());
        emailText.setText(account.getEmail());
        chucVu.setValue(account.convertRoleToString());
    }
    @FXML
    private void add() {
        String selectedEmployeeId = employIDCombobox.getValue();
        if (selectedEmployeeId == null || selectedEmployeeId.isEmpty()) {
            al.showErrorAlert("Vui lòng chọn ID nhân viên!");
            return;
        }

        // Check if the employee already has an account
        Account existingAccount = loginDao.findAccountByEmployeeId(selectedEmployeeId);
        if (existingAccount != null) {
            al.showErrorAlert("Nhân viên này đã có tài khoản!");
            return;
        }
        String username = usernameText.getText().trim();
        String password = passwordText.getText().trim();
        int role = getChucVu();

        if (username.isEmpty() || password.isEmpty()) {
            al.showErrorAlert("Vui lòng điền đầy đủ thông tin tài khoản!");
            return;
        }

        // Create new account
        Account newAccount = new Account(username, password, emailText.getText(), nameText.getText(), role);
        newAccount.setEmployeeId(selectedEmployeeId);

        // Insert account into database
        if (loginDao.insertAccount(newAccount)) {
            al.showInfoAlert("Thêm tài khoản thành công!");
            loadAccounts();
            clearFields();
        } else {
            al.showErrorAlert("Thêm tài khoản thất bại!");
        }
    }
    @FXML
    private void update() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            al.showErrorAlert("Vui lòng chọn tài khoản cần cập nhật!");
            return;
        }
        String username = usernameText.getText().trim();
        String inputPassword = passwordText.getText().trim();
        String name = nameText.getText().trim();
        String email = emailText.getText().trim();
        int role = getChucVu();
        // Only update the password if it has been changed
        String password = selectedAccount.getPassword();
        System.out.println(inputPassword.equals(password));
        if (!inputPassword.equals(password)) {
            password= new maHoaMatKhau().hashPassword(inputPassword);
        }
        selectedAccount.setUserName(username);
        selectedAccount.setPassword(password);
        selectedAccount.setName(name);
        selectedAccount.setEmail(email);
        selectedAccount.setRole(role);
        if (loginDao.updateAccount(selectedAccount)) {
            al.showInfoAlert("Cập nhật tài khoản thành công!");
            loadAccounts();
            clearFields();
        } else {
            al.showErrorAlert("Cập nhật tài khoản thất bại!");
        }
    }
    @FXML
    private void remove() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            al.showErrorAlert("Vui lòng chọn tài khoản cần xóa!");
            return;
        }
        if (loginDao.deleteAccount(selectedAccount.getStt())) {
            al.showInfoAlert("Xóa tài khoản thành công!");
            accountList.remove(selectedAccount);
            clearFields();
        } else {
            al.showErrorAlert("Xóa tài khoản thất bại!");
        }
    }
    @FXML
    private void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách tài khoản");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("DanhSachTaiKhoan.xlsx");

        Window window = saveButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            progressBar.setProgress(0);

            Task<Void> exportTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Workbook workbook = new XSSFWorkbook();
                    Sheet sheet = workbook.createSheet("Danh sách tài khoản");

                    Row headerRow = sheet.createRow(0);
                    String[] headers = {"ID", "Username", "Password", "Tên Người Dùng", "Email", "Chức Vụ"};
                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                    }

                    for (int i = 0; i < accountList.size(); i++) {
                        Account account = accountList.get(i);
                        Row row = sheet.createRow(i + 1);
                        row.createCell(0).setCellValue(account.getStt());
                        row.createCell(1).setCellValue(account.getUserName());
                        row.createCell(2).setCellValue(account.getPassword());
                        row.createCell(3).setCellValue(account.getName());
                        row.createCell(4).setCellValue(account.getEmail());
                        row.createCell(5).setCellValue(account.convertRoleToString());
                    }

                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        workbook.write(fileOut);
                    }
                    workbook.close();
                    return null;
                }
            };

            progressBar.progressProperty().bind(exportTask.progressProperty());

            exportTask.setOnSucceeded(e -> {
                progressBar.progressProperty().unbind();
                progressBar.setProgress(1.0);
                al.showInfoAlert("Xuất danh sách tài khoản thành công!");
            });

            exportTask.setOnFailed(e -> {
                progressBar.progressProperty().unbind();
                progressBar.setProgress(0);
                al.showErrorAlert("Lỗi khi xuất file Excel!");
            });

            new Thread(exportTask).start();
        }
    }

    private void clearFields() {
        usernameText.clear();
        passwordText.clear();
        nameText.clear();
        emailText.clear();
    }
    public int getChucVu() {
        String selected = chucVu.getValue();
        System.out.println(selected);
        if (selected.equals("Admin - Quản Lý")) {
            return 2; // Admin
        } else if (selected.equals("Nhân viên")) {
            return 1; // Nhân viên
        }
        return -1; // Không hợp lệ
    }
    public void handleEmployIDCombobox() {
        String selectedEmployeeId = employIDCombobox.getValue();
        if (selectedEmployeeId != null && !selectedEmployeeId.isEmpty()) {
            Employee employee = empDao.findEmployeeById(selectedEmployeeId);
            Account account=loginDao.findAccountByEmployeeId(selectedEmployeeId);
            if (employee != null) {
                nameText.setText(employee.getName());
                emailText.setText(employee.getEmail());
                passwordText.clear();
                usernameText.clear();
                if(account!=null){
                    usernameText.setText(account.getUserName());
                    passwordText.setText(account.getPassword());
                    chucVu.setValue(account.convertRoleToString());
                } else {
                    usernameText.clear();
                    passwordText.clear();
                    chucVu.setValue("Nhân viên");
                }
            } else {
                al.showErrorAlert("Không tìm thấy thông tin nhân viên!");
            }
        } else {
            nameText.clear();
            emailText.clear();
        }
    }
}