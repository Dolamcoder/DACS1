package Controller.Employee;

import Dao.Employee.CardLevelDao;
import Dao.Employee.CustomerDao;
import Dao.Employee.StayHistoryDao;
import Model.CardLevel;
import Model.Customer;
import Model.StayHistory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import Alert.alert;
public class ListCustomerController implements Initializable {

    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, LocalDate> birthDateColumn;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private Button deleteButton;
    @FXML
    private TableColumn<Customer, String> emailColumn;
    @FXML
    private TableColumn<Customer, String> genderColumn;
    @FXML
    private TableColumn<Customer, String> idColumn;
    @FXML
    private TableColumn<Customer, String> idNumberColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button saveButton;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<Customer, String> statusColumn;
    @FXML
    private Button updateButton;
    @FXML
    private Button viewCardLevelButton;
    @FXML
    private Button viewHistoryButton;

    private CustomerDao customerDao;
    private ObservableList<Customer> customerList;
    private FilteredList<Customer> filteredCustomers;
    private boolean operationInProgress = false;
    private Timer progressTimer = null;
    alert al=new alert();
    StayHistoryDao stayHistoryDao=new StayHistoryDao();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerDao = new CustomerDao();
        // Enable table editing and show grid lines for better visibility
        customerTable.setEditable(true);
        setupTableColumns();
        loadCustomerData();
        setupSearch();
    }
    public void setupTableColumns() {
        // ID column is typically not editable
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Name column with edit commit handler
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setName(event.getNewValue());
        });

        // Gender column with edit commit handler
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        genderColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setGender(event.getNewValue());
        });

        // Birth date column (usually requires DatePicker)
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birth"));

        // ID Number column with edit commit handler
        idNumberColumn.setCellValueFactory(new PropertyValueFactory<>("idNumber"));
        idNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        idNumberColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setIdNumber(event.getNewValue());
        });

        // Address column with edit commit handler
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setDiaChi(event.getNewValue());
        });

        // Email column with edit commit handler
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setEmail(event.getNewValue());
        });

        // Phone column with edit commit handler
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setPhone(event.getNewValue());
        });

        // Status column with edit commit handler
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        statusColumn.setOnEditCommit(event -> {
            Customer customer = event.getRowValue();
            customer.setStatus(event.getNewValue());
        });
    }
    private void loadCustomerData() {
        startRealTimeProgress();

        Task<ObservableList<Customer>> task = new Task<>() {
            @Override
            protected ObservableList<Customer> call() {
                return FXCollections.observableArrayList(customerDao.selectAll());
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(event -> {
            customerList = task.getValue();
            filteredCustomers = new FilteredList<>(customerList, p -> true);
            customerTable.setItems(filteredCustomers);
            finishRealTimeProgress();
        });

        task.setOnFailed(event -> {
            al.showErrorAlert("Lỗi khi tải dữ liệu khách hàng: " + task.getException().getMessage());
            stopRealTimeProgress();
        });

        new Thread(task).start();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCustomers.setPredicate(customer -> {
                // If search field is empty, show all customers
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Match against multiple fields
                return customer.getId().toLowerCase().contains(lowerCaseFilter) ||
                        customer.getName().toLowerCase().contains(lowerCaseFilter) ||
                        (customer.getPhone() != null && customer.getPhone().toLowerCase().contains(lowerCaseFilter)) ||
                        (customer.getIdNumber() != null && customer.getIdNumber().toLowerCase().contains(lowerCaseFilter)) ||
                        (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(lowerCaseFilter) || (customer.getDiaChi() != null && customer.getDiaChi().toLowerCase().contains(lowerCaseFilter)) || (customer.getStatus() != null && customer.getStatus().toLowerCase().contains(lowerCaseFilter)) || (customer.getGender() != null && customer.getGender().toLowerCase().contains(lowerCaseFilter)));
            });
        });
    }

    @FXML
    public void deleteCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            al.showErrorAlert("Vui lòng chọn khách hàng để xóa");
            return;
        }

        // Chỉ cho phép xóa khách hàng đi cùng (không có email)
        boolean hasNoEmail = selectedCustomer.getEmail() == null || selectedCustomer.getEmail().trim().isEmpty();
        if (!hasNoEmail) {
            al.showErrorAlert("Chỉ được phép xóa khách hàng đi cùng (không có email)");
            return;
        }

        String message = "Bạn có chắc muốn xóa khách hàng đi cùng: " + selectedCustomer.getName() + "?";
        message += "\nKhách hàng này không có email.";

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                startRealTimeProgress();
                Task<Boolean> deleteTask = new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        updateProgress(0.3, 1.0);
                        Thread.sleep(200);
                        updateProgress(0.6, 1.0);
                        Thread.sleep(200);
                        boolean stayhistoryDeleted = stayHistoryDao.deleteByCustomerID(selectedCustomer.getId());
                        boolean result = customerDao.delete(selectedCustomer.getId());
                        updateProgress(1.0, 1.0);
                        return result;
                    }
                };

                progressBar.progressProperty().bind(deleteTask.progressProperty());

                deleteTask.setOnSucceeded(event -> {
                    Boolean result = deleteTask.getValue();
                    if (result) {
                        loadCustomerData();
                        al.showInfoAlert("Xóa khách hàng thành công");
                    } else {
                        al.showErrorAlert("Không thể xóa khách hàng");
                    }
                    stopRealTimeProgress();
                });

                deleteTask.setOnFailed(event -> {
                    al.showErrorAlert("Lỗi khi xóa khách hàng: " + deleteTask.getException().getMessage());
                    stopRealTimeProgress();
                });

                new Thread(deleteTask).start();
            }
        });
    }

    @FXML
    public void saveCustomerList() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách khách hàng");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(customerTable.getScene().getWindow());

        if (file != null) {
            exportToExcel(file);
        }
    }

    private void exportToExcel(File file) {
        startRealTimeProgress();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("Danh sách khách hàng");
                    Row headerRow = sheet.createRow(0);

                    String[] headers = {
                            "STT",
                            "Mã Khách Hàng",
                            "Tên Khách Hàng",
                            "Giới Tính",
                            "Ngày Sinh",
                            "CCCD",
                            "Địa Chỉ",
                            "Email",
                            "Điện Thoại",
                            "Trạng Thái"
                    };
                    for (int i = 0; i < headers.length; i++) {
                        headerRow.createCell(i).setCellValue(headers[i]);
                    }

                    ObservableList<Customer> items = customerTable.getItems();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    for (int i = 0; i < items.size(); i++) {
                        Customer customer = items.get(i);
                        Row row = sheet.createRow(i + 1);

                        row.createCell(0).setCellValue(i + 1);
                        row.createCell(1).setCellValue(customer.getId());
                        row.createCell(2).setCellValue(customer.getName());
                        row.createCell(3).setCellValue(customer.getGender() != null ? customer.getGender() : "");

                        if (customer.getBirth() != null) {
                            row.createCell(4).setCellValue(customer.getBirth().format(dateFormatter));
                        } else {
                            row.createCell(4).setCellValue("");
                        }

                        row.createCell(5).setCellValue(customer.getIdNumber() != null ? customer.getIdNumber() : "");
                        row.createCell(6).setCellValue(customer.getDiaChi() != null ? customer.getDiaChi() : "");
                        row.createCell(7).setCellValue(customer.getEmail() != null ? customer.getEmail() : "");
                        row.createCell(8).setCellValue(customer.getPhone() != null ? customer.getPhone() : "");
                        row.createCell(9).setCellValue(customer.getStatus() != null ? customer.getStatus() : "");

                        // Update progress
                        updateProgress(i + 1, items.size());
                    }

                    for (int i = 0; i < headers.length; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        workbook.write(fileOut);
                    }
                }
                return null;
            }
        };

        // Bind progress to progressBar
        progressBar.progressProperty().bind(task.progressProperty());

        // Handle task completion
        task.setOnSucceeded(event -> {
            al.showInfoAlert("Xuất danh sách khách hàng thành công");
            finishRealTimeProgress();
        });

        // Handle errors
        task.setOnFailed(event -> {
            stopRealTimeProgress();
            al.showErrorAlert("Lỗi khi xuất danh sách khách hàng: " + task.getException().getMessage());
        });

        // Run in background
        new Thread(task).start();
    }

    @FXML
    public void updateCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            al.showErrorAlert("Vui lòng chọn khách hàng để cập nhật");
            return;
        }

        startRealTimeProgress();

        Task<Boolean> updateTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return customerDao.update2(selectedCustomer);
            }
        };

        updateTask.setOnSucceeded(event -> {
            Boolean result = updateTask.getValue();
            if (result) {
                al.showInfoAlert("Cập nhật khách hàng thành công");
            } else {
                al.showErrorAlert("Không thể cập nhật khách hàng");
                stopRealTimeProgress();
            }
        });

        updateTask.setOnFailed(event -> {
            al.showErrorAlert("Lỗi khi cập nhật khách hàng: " + updateTask.getException().getMessage());
            stopRealTimeProgress();
        });

        new Thread(updateTask).start();
    }

    @FXML
    public void viewCardLevel() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            al.showErrorAlert("Vui lòng chọn khách hàng để xem hạng thẻ");
            return;
        }

        startRealTimeProgress();

        Task<CardLevel> task = new Task<>() {
            @Override
            protected CardLevel call() throws Exception {
                CardLevelDao cardLevelDao = new CardLevelDao();
                return cardLevelDao.getLatestCardLevelByCustomerId(selectedCustomer.getId());
            }
        };

        task.setOnSucceeded(event -> {
            CardLevel cardLevel = task.getValue();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Thông tin hạng thẻ");
            dialog.setHeaderText("Thông tin hạng thẻ khách hàng: " + selectedCustomer.getName());

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            if (cardLevel != null) {
                // Format the money amount with VND currency symbol
                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedAmount = formatter.format(cardLevel.getTotalAmount()) + " VND";

                grid.add(new Label("Hạng thẻ:"), 0, 0);
                grid.add(new Label(cardLevel.getLevelName()), 1, 0);

                grid.add(new Label("Tổng chi tiêu:"), 0, 1);
                grid.add(new Label(formattedAmount), 1, 1);

                grid.add(new Label("Ưu đãi:"), 0, 2);
                grid.add(new Label(cardLevel.getDiscountPercentage() + "%"), 1, 2);

                grid.add(new Label("Mô tả:"), 0, 3);
                grid.add(new Label(cardLevel.getDescription()), 1, 3);

                // Calculate next level requirements
                String nextLevel = "";
                double amountNeeded = 0;

                switch (cardLevel.getLevelName()) {
                    case "Bronze":
                        nextLevel = "Silver";
                        amountNeeded = 5000000 - cardLevel.getTotalAmount();
                        break;
                    case "Silver":
                        nextLevel = "Gold";
                        amountNeeded = 10000000 - cardLevel.getTotalAmount();
                        break;
                    case "Gold":
                        nextLevel = "Platinum";
                        amountNeeded = 15000000 - cardLevel.getTotalAmount();
                        break;
                    case "Platinum":
                        nextLevel = "Diamond";
                        amountNeeded = 20000000 - cardLevel.getTotalAmount();
                        break;
                    default:
                        nextLevel = "Đã đạt hạng cao nhất";
                        amountNeeded = 0;
                }

                if (amountNeeded > 0) {
                    grid.add(new Label("Cần thêm:"), 0, 4);
                    grid.add(new Label(formatter.format(amountNeeded) + " VND để lên hạng " + nextLevel), 1, 4);
                } else if ("Diamond".equals(cardLevel.getLevelName())) {
                    grid.add(new Label("Trạng thái:"), 0, 4);
                    grid.add(new Label("Đã đạt hạng thẻ cao nhất"), 1, 4);
                }
            } else {
                grid.add(new Label("Khách hàng chưa có hạng thẻ"), 0, 0);
                grid.add(new Label("Cần chi tiêu ít nhất 100,000 VND để được cấp hạng Bronze"), 0, 1);
            }

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            finishRealTimeProgress();
            dialog.showAndWait();
        });

        task.setOnFailed(event -> {
            stopRealTimeProgress();
            al.showErrorAlert("Lỗi khi tải thông tin hạng thẻ: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }
    @FXML
    public void viewStayHistory() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            al.showErrorAlert("Vui lòng chọn khách hàng để xem lịch sử lưu trú");
            return;
        }

        Task<ArrayList<StayHistory>> task = new Task<>() {
            @Override
            protected ArrayList<StayHistory> call() throws Exception {
                return stayHistoryDao.selectAll().stream()
                        .filter(stay -> stay.getCustomerID().equals(selectedCustomer.getId()))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };

        task.setOnSucceeded(event -> {
            ArrayList<StayHistory> stayHistories = task.getValue();
            if (stayHistories.isEmpty()) {
                al.showInfoAlert("Khách hàng " + selectedCustomer.getName() + " chưa có lịch sử lưu trú");
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            StringBuilder content = new StringBuilder();
            content.append("LỊCH SỬ LƯU TRÚ\n");
            content.append("Khách hàng: ").append(selectedCustomer.getName()).append("\n");
            content.append("ID: ").append(selectedCustomer.getId()).append("\n\n");

            for (int i = 0; i < stayHistories.size(); i++) {
                StayHistory stay = stayHistories.get(i);
                content.append("───────── Lần lưu trú ").append(i + 1).append(" ─────────\n");
                content.append("• Phòng: ").append(stay.getRoomID()).append("\n");
                content.append("• Ngày nhận phòng: ").append(stay.getCheckIn().format(dateFormatter)).append("\n");
                content.append("• Ngày trả phòng: ").append(stay.getCheckOut().format(dateFormatter)).append("\n");

                // Calculate duration of stay
                long days = ChronoUnit.DAYS.between(stay.getCheckIn(), stay.getCheckOut());
                content.append("• Thời gian lưu trú: ").append(days).append(" ngày\n");

                if (stay.getNote() != null && !stay.getNote().isEmpty()) {
                    content.append("• Ghi chú: ").append(stay.getNote()).append("\n");
                }
                content.append("\n");
            }

            // Create a custom dialog with scrollable text area
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Lịch sử lưu trú");
            dialog.setHeaderText("Khách hàng: " + selectedCustomer.getName());

            TextArea textArea = new TextArea(content.toString());
            textArea.setEditable(false);
            textArea.setPrefWidth(500);
            textArea.setPrefHeight(400);
            textArea.setWrapText(true);

            dialog.getDialogPane().setContent(textArea);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        });

        task.setOnFailed(event -> {
            al.showErrorAlert("Lỗi khi tải lịch sử lưu trú: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }
    // Progress bar management methods
    private void startRealTimeProgress() {
        operationInProgress = true;
        stopProgressTimerIfRunning();

        progressBar.progressProperty().unbind();
        progressBar.setProgress(0.0);
    }

    private void stopRealTimeProgress() {
        operationInProgress = false;
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
    }

    private void finishRealTimeProgress() {
        operationInProgress = false;
        progressBar.progressProperty().unbind();
        progressBar.setProgress(1.0);

        // Reset after 1 second
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> progressBar.setProgress(0));
            }
        }, 1000);
    }

    private void stopProgressTimerIfRunning() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
    }
}