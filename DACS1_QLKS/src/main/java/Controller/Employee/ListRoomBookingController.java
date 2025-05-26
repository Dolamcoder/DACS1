package Controller.Employee;
import Controller.Invoice.InvoiceController;
import Dao.Employee.CustomerDao;
import Dao.Employee.InvoiceDao;
import Dao.Employee.RoomBookingDao;
import Dao.Employee.RoomDao;
import Model.Booking;
import Model.Invoice;
import Service.BookingSchedulerService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import Alert.alert;
public class ListRoomBookingController implements Initializable {
    @FXML private AnchorPane rootPane;
    @FXML private TableView<Booking> table;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> customerIdColumn;
    @FXML private TableColumn<Booking, String> roomIdColumn;
    @FXML private TableColumn<Booking, LocalDate> checkInColumn;
    @FXML private TableColumn<Booking, LocalDate> checkOutColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, LocalDateTime> CreatedAt;

    @FXML private TextField searchField;
    @FXML private Button saveButton;
    @FXML private Button checkInvoiceButton;
    @FXML private Button updateButton;
    @FXML private Button removeButton;
    @FXML private ProgressBar progressBar;

    private ObservableList<Booking> bookingsList = FXCollections.observableArrayList();
    private RoomBookingDao bookingDao = new RoomBookingDao();
    private RoomDao roomDao = new RoomDao();
    private CustomerDao customerDao = new CustomerDao();
    private alert al = new alert();
    private Timer progressTimer;
    private boolean operationInProgress = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadBookings();
        setupEditableColumns();
        setupSearchField();

        // Thiết lập cơ chế cập nhật tự động dữ liệu mỗi 5 phút
        setupAutoRefresh();
    }

    // Phương thức để thiết lập cập nhật tự động
    private void setupAutoRefresh() {
        Timer refreshTimer = new Timer(true);
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!operationInProgress) {
                        loadBookings();
                    }
                });
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000); // 5 phút một lần
    }

    private void setupTableColumns() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        CreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Format date columns
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        checkInColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : dateFormatter.format(date));
            }
        });

        checkOutColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : dateFormatter.format(date));
            }
        });

        // Format CreatedAt column
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        CreatedAt.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                setText(empty || dateTime == null ? null : dateTimeFormatter.format(dateTime));
            }
        });
    }

    private void setupEditableColumns() {
        table.setEditable(true);

        // Make status column editable
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        statusColumn.setOnEditCommit(event -> {
            Booking booking = event.getRowValue();
            booking.setStatus(event.getNewValue());
        });

        // Make check-in and check-out dates editable
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringConverter<LocalDate> dateConverter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date == null ? "" : formatter.format(date);
            }

            @Override
            public LocalDate fromString(String string) {
                return string == null || string.isEmpty() ? null :
                        LocalDate.parse(string, formatter);
            }
        };

        checkInColumn.setCellFactory(column -> new DateEditingCell<>(dateConverter));
        checkInColumn.setOnEditCommit(event -> {
            Booking booking = event.getRowValue();
            booking.setCheckInDate(event.getNewValue());
        });

        checkOutColumn.setCellFactory(column -> new DateEditingCell<>(dateConverter));
        checkOutColumn.setOnEditCommit(event -> {
            Booking booking = event.getRowValue();
            booking.setCheckOutDate(event.getNewValue());
        });
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchBookings());
    }
    private void loadBookings() {
        Task<ObservableList<Booking>> task = new Task<>() {
            @Override
            protected ObservableList<Booking> call() throws Exception {
                ObservableList<Booking> tempList = FXCollections.observableArrayList();
                // Giả sử bookingDao.selectAll() trả về danh sách booking
                ArrayList<Booking> bookings = bookingDao.selectAll();
                int total = bookings.size();
                for (int i = 0; i < total; i++) {
                    tempList.add(bookings.get(i));
                    // Cập nhật tiến trình (từ 0.0 đến 1.0)
                    updateProgress(i + 1, total);
                    // Giả lập độ trễ nhỏ để thấy tiến trình
                    Thread.sleep(10); // Điều chỉnh hoặc bỏ nếu không cần
                }
                return tempList;
            }
        };

        // Ràng buộc tiến trình với progressBar
        progressBar.progressProperty().bind(task.progressProperty());

        // Xử lý khi tác vụ hoàn thành
        task.setOnSucceeded(event -> {
            bookingsList.clear();
            bookingsList.addAll(task.getValue());
            table.setItems(bookingsList);
            finishRealTimeProgress();
        });

        // Xử lý khi có lỗi
        task.setOnFailed(event -> {
            stopRealTimeProgress();
            al.showErrorAlert("Lỗi khi tải dữ liệu: " + task.getException().getMessage());
        });

        // Chạy tác vụ trong luồng nền
        new Thread(task).start();
    }


    @FXML
    public void searchBookings() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            table.setItems(bookingsList);
            return;
        }

        Task<ObservableList<Booking>> task = new Task<>() {
            @Override
            protected ObservableList<Booking> call() throws Exception {
                ObservableList<Booking> filteredList = FXCollections.observableArrayList();
                int total = bookingsList.size();
                int count = 0;

                for (Booking booking : bookingsList) {
                    // Cập nhật tiến trình cho mỗi phần tử
                    updateProgress(++count, total);

                    // Tìm kiếm theo ID đặt phòng, ID khách hàng, ID phòng
                    if (booking.getBookingId().toLowerCase().contains(searchText) ||
                            booking.getCustomerId().toLowerCase().contains(searchText) ||
                            booking.getRoomId().toLowerCase().contains(searchText) ||
                            (booking.getStatus() != null && booking.getStatus().toLowerCase().contains(searchText))) {
                        filteredList.add(booking);
                        continue;
                    }

                    // Tìm kiếm theo ngày check-in, check-out
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    if (booking.getCheckInDate() != null &&
                            formatter.format(booking.getCheckInDate()).toLowerCase().contains(searchText)) {
                        filteredList.add(booking);
                        continue;
                    }

                    if (booking.getCheckOutDate() != null &&
                            formatter.format(booking.getCheckOutDate()).toLowerCase().contains(searchText)) {
                        filteredList.add(booking);
                        continue;
                    }

                    // Tìm kiếm theo ngày tạo
                    if (booking.getCreatedAt() != null) {
                        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        if (dtFormatter.format(booking.getCreatedAt()).toLowerCase().contains(searchText)) {
                            filteredList.add(booking);
                            continue;
                        }
                    }

                    // Tìm kiếm theo tên khách hàng
                    try {
                        String customerName = customerDao.getNameById(booking.getCustomerId());
                        if (customerName != null && customerName.toLowerCase().contains(searchText)) {
                            filteredList.add(booking);
                            continue;
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi tìm kiếm theo tên khách hàng: " + e.getMessage());
                    }

                    // Tìm kiếm theo giá phòng
                    try {
                        double price = roomDao.getPriceById(booking.getRoomId());
                        String roomPrice = String.format("%,.0f", price);
                        if (roomPrice.contains(searchText) || String.valueOf((int)price).contains(searchText)) {
                            filteredList.add(booking);
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi tìm kiếm theo giá phòng: " + e.getMessage());
                    }

                    // Thêm một chút độ trễ giữa mỗi lần kiểm tra để thấy thanh tiến trình
                    Thread.sleep(5); // Điều chỉnh nếu cần
                }

                return filteredList;
            }
        };

        // Ràng buộc tiến trình với progressBar
        progressBar.progressProperty().bind(task.progressProperty());

        // Xử lý khi tác vụ hoàn thành
        task.setOnSucceeded(event -> {
            ObservableList<Booking> filteredList = task.getValue();
            table.setItems(filteredList);
            finishRealTimeProgress();
        });

        // Xử lý khi có lỗi
        task.setOnFailed(event -> {
            stopRealTimeProgress();
            al.showErrorAlert("Lỗi khi tìm kiếm: " + task.getException().getMessage());
        });

        // Chạy tác vụ trong luồng nền
        new Thread(task).start();
    }
    @FXML
    public void updateBooking() {
        Booking selectedBooking = table.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            al.showErrorAlert("Vui lòng chọn một đặt phòng để cập nhật");
            return;
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Giả lập tiến trình (vì update có thể nhanh)
                updateProgress(0.5, 1.0); // 50% khi bắt đầu
                boolean success = bookingDao.update(selectedBooking);
                updateProgress(1.0, 1.0); // 100% khi hoàn thành
                return success;
            }
        };

        // Ràng buộc tiến trình với progressBar
        progressBar.progressProperty().bind(task.progressProperty());

        // Xử lý khi có lỗi
        task.setOnFailed(event -> {
            stopRealTimeProgress();
            al.showErrorAlert("Lỗi khi cập nhật: " + task.getException().getMessage());
        });

        // Xử lý khi cập nhật thành công
        task.setOnSucceeded(event -> {
            stopRealTimeProgress();
            if (task.getValue()) {
                al.showInfoAlert("Cập nhật thành công!");
            } else {
                al.showErrorAlert("Cập nhật thất bại!");
            }
        });

        // Chạy tác vụ trong luồng nền
        new Thread(task).start();
    }
    @FXML
    public void removeBooking() {
        Booking selectedBooking = table.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            al.showErrorAlert("Vui lòng chọn một đặt phòng để xóa");
            return;
        }

        // Special case for cancelled bookings - delete directly
        if (selectedBooking.getStatus().equals("Đã hủy")) {
            startRealTimeProgress();

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    updateProgress(0.5, 1.0);
                    boolean success = bookingDao.delete(selectedBooking.getBookingId());
                    updateProgress(1.0, 1.0); // 100% when complete
                    return success ;
                }
            };
            progressBar.progressProperty().bind(task.progressProperty());
            task.setOnSucceeded(event -> {
                boolean success = task.getValue();
                if (success) {
                    loadBookings();
                    al.showInfoAlert("Xóa đặt phòng đã huỷ thành công");
                } else {
                    al.showErrorAlert("Không thể xóa đặt phòng");
                }
                finishRealTimeProgress();
            });

            task.setOnFailed(event -> {
                stopRealTimeProgress();
                al.showErrorAlert("Lỗi khi xóa: " + task.getException().getMessage());
            });

            new Thread(task).start();
            return;
        }

        // Original flow for confirmed bookings
        else if (!selectedBooking.getStatus().equals("Đã xác nhận")) {
            al.showErrorAlert("Chỉ có thể xóa đặt phòng đã xác nhận hoặc đã huỷ");
            return;
        }
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                updateProgress(0.3, 1.0); // 30% khi bắt đầu
                roomDao.updateStatus(selectedBooking.getRoomId(), 1);
                updateProgress(0.6, 1.0); // 60% sau khi cập nhật trạng thái phòng
                customerDao.updateCustomerStatus(selectedBooking.getCustomerId(), "Đã huỷ");
                updateProgress(0.9, 1.0); // 90% sau khi cập nhật trạng thái khách hàng
                boolean success = bookingDao.delete(selectedBooking.getBookingId());
                updateProgress(1.0, 1.0); // 100% khi hoàn thành
                return success;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(event -> {
            boolean success = task.getValue();
            if (success) {
                loadBookings();
                al.showInfoAlert("Xóa đặt phòng thành công");
            } else {
                al.showErrorAlert("Không thể xóa đặt phòng");
            }
            finishRealTimeProgress();
        });

        task.setOnFailed(event -> {
            stopRealTimeProgress();
            al.showErrorAlert("Lỗi khi xóa: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    public void saveBookingList() {
        // Giữ nguyên logic fileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách đặt phòng");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            exportToExcel(file);
        }
    }

    private void exportToExcel(File file) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("Danh sách đặt phòng");
                    Row headerRow = sheet.createRow(0);

                    String[] headers = {
                            "STT",
                            "Mã Đặt Phòng",
                            "Mã Khách Hàng",
                            "Tên Khách Hàng",
                            "Mã Phòng",
                            "Giá Phòng",
                            "Ngày Nhận",
                            "Ngày Trả",
                            "Ngày Tạo",
                            "Trạng Thái"
                    };

                    for (int i = 0; i < headers.length; i++) {
                        headerRow.createCell(i).setCellValue(headers[i]);
                    }

                    ObservableList<Booking> items = table.getItems();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                    for (int i = 0; i < items.size(); i++) {
                        Booking booking = items.get(i);
                        Row row = sheet.createRow(i + 1);

                        row.createCell(0).setCellValue(i + 1);
                        row.createCell(1).setCellValue(booking.getBookingId());
                        row.createCell(2).setCellValue(booking.getCustomerId());

                        String customerName = "";
                        try {
                            customerName = customerDao.getNameById(booking.getCustomerId());
                        } catch (Exception e) {
                            System.err.println("Lỗi khi lấy tên khách hàng: " + e.getMessage());
                        }
                        row.createCell(3).setCellValue(customerName != null ? customerName : "");

                        row.createCell(4).setCellValue(booking.getRoomId());

                        String roomPrice = "";
                        try {
                            double price = roomDao.getPriceById(booking.getRoomId());
                            roomPrice = String.format("%,.0f VND", price);
                        } catch (Exception e) {
                            System.err.println("Lỗi khi lấy giá phòng: " + e.getMessage());
                        }
                        row.createCell(5).setCellValue(roomPrice);
                        row.createCell(6).setCellValue(booking.getCheckInDate().format(dateFormatter));
                        row.createCell(7).setCellValue(booking.getCheckOutDate().format(dateFormatter));
                        row.createCell(8).setCellValue(booking.getCreatedAt().format(dateTimeFormatter));
                        row.createCell(9).setCellValue(booking.getStatus());

                        // Cập nhật tiến trình
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

        // Ràng buộc tiến trình với progressBar
        progressBar.progressProperty().bind(task.progressProperty());

        // Xử lý khi tác vụ hoàn thành
        task.setOnSucceeded(event -> {
            al.showInfoAlert("Xuất danh sách đặt phòng thành công");
            finishRealTimeProgress();
        });

        // Xử lý khi có lỗi
        task.setOnFailed(event -> {
            stopRealTimeProgress();
            al.showErrorAlert("Lỗi khi xuất danh sách: " + task.getException().getMessage());
        });

        // Chạy tác vụ trong luồng nền
        new Thread(task).start();
    }

    @FXML
    public void checkInvoice() {
        Booking selectedBooking = table.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            al.showErrorAlert("Vui lòng chọn một đặt phòng để xem hóa đơn");
            return;
        }

        startRealTimeProgress();
        Task<Invoice> task = new Task<>() {
            @Override
            protected Invoice call() throws Exception {
                // Load invoice data for selected booking
                InvoiceDao invoiceDao = new InvoiceDao();
                Invoice invoice = invoiceDao.findByBookingID(selectedBooking.getBookingId());
                if (invoice == null) {
                    throw new Exception("Không tìm thấy hóa đơn cho đặt phòng này");
                }
                return invoice;
            }
        };

        task.setOnSucceeded(event -> {
            try {
                Invoice invoice = task.getValue();

                // Load the invoice FXML view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/HoaDon/invoice.fxml"));
                Parent root = loader.load();
                // Get controller and pass the invoice data
                InvoiceController controller = loader.getController();
                controller.setInvoice(invoice);
                // Show the invoice in a new window
                Stage stage = new Stage();
                stage.setTitle("Hóa đơn " + invoice.getInvoiceID());
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();

            } catch (IOException e) {
                al.showErrorAlert("Lỗi khi hiển thị hóa đơn: " + e.getMessage());
            } finally {
                finishRealTimeProgress();
            }
        });

        task.setOnFailed(event -> {
            stopRealTimeProgress();
            Throwable exception = task.getException();
            al.showErrorAlert(exception != null ? exception.getMessage() : "Không thể tải hóa đơn");
        });

        new Thread(task).start();
    }
    private void finishRealTimeProgress() {
        operationInProgress = false;

        // Unbind progress property before setting value manually
        progressBar.progressProperty().unbind();
        progressBar.setProgress(1.0);

        // Đặt lại sau 1 giây
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> progressBar.setProgress(0));
            }
        }, 1000);
    }

    // Phương thức dừng tiến trình khi có lỗi
    private void stopRealTimeProgress() {
        operationInProgress = false;

        // Unbind progress property before setting value manually
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
    }

    // Phương thức mới để bắt đầu tiến trình theo thời gian thực của tác vụ
    private void startRealTimeProgress() {
        operationInProgress = true;
        stopProgressTimerIfRunning();

        // Make sure to unbind in case it was previously bound
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0.0); // Khởi tạo thanh tiến trình
    }

    // Dừng timer nếu đang chạy
    private void stopProgressTimerIfRunning() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
    }
    // Helper method to reset progress bar after a delay
    private void resetProgressBarAfterDelay() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> progressBar.setProgress(0));
            }
        }, 1000); // 1 second delay
    }

    private static class DateEditingCell<T> extends TableCell<T, LocalDate> {
        private final DatePicker datePicker;
        private final StringConverter<LocalDate> converter;

        public DateEditingCell(StringConverter<LocalDate> converter) {
            this.converter = converter;
            this.datePicker = new DatePicker();
            this.datePicker.setConverter(converter);
            this.datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            this.datePicker.setOnAction(e -> commitEdit(this.datePicker.getValue()));
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                datePicker.setValue(getItem());
                setText(null);
                setGraphic(datePicker);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(converter.toString(getItem()));
            setGraphic(null);
        }

        @Override
        public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    datePicker.setValue(item);
                    setText(null);
                    setGraphic(datePicker);
                } else {
                    setText(converter.toString(item));
                    setGraphic(null);
                }
            }
        }
    }
}