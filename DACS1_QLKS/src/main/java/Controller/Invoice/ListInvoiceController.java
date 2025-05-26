package Controller.Invoice;

import Alert.alert;
import Dao.Employee.*;
import Model.Invoice;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListInvoiceController implements Initializable {
    @FXML private TableColumn<Invoice, String> invoiceIDColumn;
    @FXML private TableColumn<Invoice, String> bookingIDColumn;
    @FXML private TableColumn<Invoice, String> serviceBookingIDColumn;
    @FXML private TableColumn<Invoice, String> issueDateColumn;
    @FXML private TableColumn<Invoice, Double> totalAmountColumn;
    @FXML private TableColumn<Invoice, Double> taxColumn;
    @FXML private TableColumn<Invoice, Integer> soLuong;
    @FXML private TableColumn<Invoice, String> statusColumn;
    @FXML private TableView<Invoice> invoiceTable;
    @FXML private Button deleteButton;
    @FXML private Button exportButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private TextField searchField;
    @FXML private ProgressBar progressBar;

    private final ObservableList<Invoice> invoicesList = FXCollections.observableArrayList();
    private FilteredList<Invoice> filteredInvoices;
    private final InvoiceDao invoiceDao;
    private final CustomerDao customerDao;
    private final RoomBookingDao bookingDao;
    private final Map<String, String> customerNames = new HashMap<>();
    private final alert al = new alert();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ListInvoiceController() {
        invoiceDao = new InvoiceDao();
        customerDao = new CustomerDao();
        bookingDao = new RoomBookingDao();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        setupSearch();
        invoiceTable.setEditable(true);
        loadInvoiceDataAsync();
    }

    private void configureTableColumns() {
        invoiceIDColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceID"));
        bookingIDColumn.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        serviceBookingIDColumn.setCellValueFactory(new PropertyValueFactory<>("serviceBookingID"));
        issueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        taxColumn.setCellValueFactory(new PropertyValueFactory<>("tax"));
        soLuong.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Currency formatting for totalAmountColumn
        totalAmountColumn.setCellFactory(column -> new TableCell<Invoice, Double>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : currencyFormat.format(amount));
            }
        });

        // Editable tax column
        taxColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        taxColumn.setOnEditCommit(event -> {
            Invoice invoice = event.getRowValue();
            if (invoice != null) {
                invoice.setTax(event.getNewValue());
            }
        });
    }

    private void setupSearch() {
        filteredInvoices = new FilteredList<>(invoicesList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredInvoices.setPredicate(invoice -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return invoice.getInvoiceID().toLowerCase().contains(lowerCaseFilter) ||
                        invoice.getBookingID().toLowerCase().contains(lowerCaseFilter) ||
                        customerNames.getOrDefault(invoice.getBookingID(), "").toLowerCase().contains(lowerCaseFilter);
            });
            invoiceTable.setItems(filteredInvoices);
        });
    }

    private void loadInvoiceDataAsync() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        executor.submit(() -> {
            try {
                invoicesList.clear();
                invoicesList.addAll(invoiceDao.findAll());
                loadCustomerNames();
                javafx.application.Platform.runLater(() -> {
                    invoiceTable.setItems(invoicesList);
                    progressBar.setProgress(1.0);
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() ->
                        al.showErrorAlert("Không thể tải dữ liệu hóa đơn: " + e.getMessage()));
            }
        });
    }

    private void loadCustomerNames() {
        customerNames.clear();
        for (Invoice invoice : invoicesList) {
            String bookingID = invoice.getBookingID();
            if (bookingID != null && !bookingID.isEmpty()) {
                String customerID = bookingDao.getCustomerID(bookingID);
                if (customerID != null) {
                    Customer customer = customerDao.searchById(customerID);
                    if (customer != null) {
                        customerNames.put(bookingID, customer.getName());
                    }
                }
            }
        }
    }

    @FXML
    public void deleteInvoice() {
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();

        if (selectedInvoice == null) {
            al.showErrorAlert("Vui lòng chọn một hóa đơn để xóa!");
            return;
        }

        // Hộp thoại xác nhận xóa
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc muốn xóa hóa đơn này không?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = invoiceDao.delete(selectedInvoice.getInvoiceID());

            if (success) {
                al.showInfoAlert("Đã xóa hóa đơn thành công!");
                // Cập nhật lại bảng nếu cần
                invoiceTable.getItems().remove(selectedInvoice);
            } else {
                al.showErrorAlert("Xóa hóa đơn thất bại! Vui lòng thử lại.");
            }
        }
    }
    @FXML
    public void exportInvoice() {
        // Get the selected invoice
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedInvoice == null) {
            al.showErrorAlert("Vui lòng chọn một hóa đơn để xuất!");
            return;
        }

        // Create file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu hóa đơn PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("HoaDon_" + selectedInvoice.getInvoiceID() + ".pdf");

        // Show save dialog
        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());

        if (file != null) {
            try {
                // Use InvoiceExporter to generate the PDF
                InvoiceExporter exporter = new InvoiceExporter();
                exporter.exportInvoiceToPdf(file.getAbsolutePath(), selectedInvoice);
                al.showInfoAlert("Xuất hóa đơn thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                al.showErrorAlert("Không thể xuất hóa đơn: " + e.getMessage());
            }
        }
    }

    @FXML
    public void saveInvoiceList() {
        // Show file chooser dialog for saving the Excel file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách hóa đơn");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        // Default filename suggestion with timestamp
        fileChooser.setInitialFileName("DanhSachHoaDon_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        File file = fileChooser.showSaveDialog(saveButton.getScene().getWindow());
        if (file == null) {
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách hóa đơn");

            // Create header style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Mã hóa đơn", "Mã đặt phòng", "Tên khách hàng", "Mã đặt dịch vụ",
                    "Ngày lập", "Tổng tiền", "Thuế (%)", "Số lượng", "Trạng thái"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate data rows
            int rowNum = 1;
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Invoice invoice : invoicesList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(invoice.getInvoiceID());
                row.createCell(1).setCellValue(invoice.getBookingID());

                // Add customer name to the Excel file
                String customerName = customerNames.getOrDefault(invoice.getBookingID(), "");
                row.createCell(2).setCellValue(customerName);

                row.createCell(3).setCellValue(invoice.getServiceBookingID());
                LocalDate localDate = invoice.getIssueDate().toLocalDate();
                row.createCell(4).setCellValue(localDate.format(dateFormatter));
                row.createCell(5).setCellValue(invoice.getTotalAmount());
                row.createCell(6).setCellValue(invoice.getTax());
                row.createCell(7).setCellValue(invoice.getAmount());
                row.createCell(8).setCellValue(invoice.getStatus());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Save the file to selected location
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
            al.showInfoAlert("Đã lưu danh sách hóa đơn thành công!\nĐường dẫn: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            al.showErrorAlert("Không thể lưu danh sách: " + e.getMessage());
        }
    }

    @FXML
    public void updateInvoice() {
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedInvoice == null) {
            al.showErrorAlert("Vui lòng chọn một hóa đơn để cập nhật!");
            return;
        }

        if (invoiceDao.update(selectedInvoice)) {
            al.showInfoAlert("Cập nhật hóa đơn thành công!");
            loadInvoiceDataAsync(); // Reload data after update
        } else {
            al.showErrorAlert("Cập nhật hóa đơn thất bại!");
        }
    }

    // Cleanup executor on controller destruction
    public void shutdown() {
        executor.shutdown();
    }
}