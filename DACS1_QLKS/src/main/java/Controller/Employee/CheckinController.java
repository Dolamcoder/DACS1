    package Controller.Employee;

    import Controller.Admin.AuditLogController;
    import Controller.Login.LoginController;
    import Dao.Employee.*;
    import Model.*;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.concurrent.Task;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.scene.input.MouseEvent;
    import Alert.Alert;
    import regex.InputValidator;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Set;
    import java.util.stream.Collectors;
    public class CheckinController {
        Alert al;
        RoomBookingDao rdao;
        Booking booking;
        CustomerDao cDao;
        RoomDao roomDao;
        private String nameNguoiDat;
        StayHistoryDao stayDao;

        public CheckinController() {
            al = new Alert();
            rdao = new RoomBookingDao();
            cDao = new CustomerDao();
            stayDao=new StayHistoryDao();
            roomDao=new RoomDao();
        }

        @FXML
        private TableView<Booking> table;
        @FXML
        private TableColumn<Booking, String> bookingID;
        @FXML
        private TableColumn<Booking, String> CustomerID;
        @FXML
        private TableColumn<Booking, String> RoomID;
        @FXML
        private TableColumn<Booking, String> CheckinDate;
        @FXML
        private TableColumn<Booking, String> CheckOutDate;
        @FXML
        private TableColumn<Booking, String> status;
        @FXML
        private TableColumn<Booking, LocalDateTime> CreatedAt;
        @FXML
        private TextField nameText, cccd, diaChi, email, phone, idPhongText, timKiemText;
        @FXML
        private DatePicker ngaySInh;
        @FXML
        private Button checkin, huyDatPhong,  addCustomer;
        @FXML
        private RadioButton nam, nu, khac;

        @FXML
        private ProgressBar tienTrinh;

        private ToggleGroup genderGroup;
        private ObservableList<Booking> bookingList = FXCollections.observableArrayList();
        private ArrayList<Booking> allConfirmedBookings = new ArrayList<>();
        Booking booktemp;
        public void initialize() {
            setupGenderGroup();
            setupTableColumns();
            loadTableData();
            setupRowClickEvent();
            setupSearchListener();
        }

        private void setupGenderGroup() {
            genderGroup = new ToggleGroup();
            nam.setToggleGroup(genderGroup);
            nu.setToggleGroup(genderGroup);
            khac.setToggleGroup(genderGroup);
        }

        private void setupTableColumns() {
            bookingID.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
            CustomerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            RoomID.setCellValueFactory(new PropertyValueFactory<>("roomId"));
            CheckinDate.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
            CheckOutDate.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
            status.setCellValueFactory(new PropertyValueFactory<>("status"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            CreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            CreatedAt.setCellFactory(column -> new TableCell<Booking, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null) ? null : formatter.format(item));
                }
            });
        }

        private void loadTableData() {
            ArrayList<Booking> allBookings = rdao.selectAll();

            allConfirmedBookings.clear();
            for (Booking b : allBookings) {
                if ("Đã xác nhận".equals(b.getStatus())) {
                    allConfirmedBookings.add(b);
                }
            }

            bookingList.setAll(allConfirmedBookings);
            table.setItems(bookingList);
        }

        private void setupSearchListener() {
            timKiemText.textProperty().addListener((observable, oldValue, newValue) -> {
                String keyword = (newValue == null) ? "" : newValue.trim().toLowerCase();

                if (keyword.isEmpty()) {
                    bookingList.setAll(allConfirmedBookings);
                } else {
                    List<Booking> filtered = allConfirmedBookings.stream()
                            .filter(b -> {
                                String bookingId = String.valueOf(b.getBookingId()).toLowerCase();
                                String roomId = String.valueOf(b.getRoomId()).toLowerCase();
                                String customerId = String.valueOf(b.getCustomerId()).toLowerCase();
                                String checkIn = b.getCheckInDate() != null ? b.getCheckInDate().toString().toLowerCase() : "";
                                String checkOut = b.getCheckOutDate() != null ? b.getCheckOutDate().toString().toLowerCase() : "";
                                String createdAt = b.getCreatedAt() != null ? b.getCreatedAt().toString().toLowerCase() : "";
                                String status = b.getStatus() != null ? b.getStatus().toLowerCase() : "";

                                Customer customer = cDao.searchById(b.getCustomerId());
                                String customerName = customer != null && customer.getName() != null
                                        ? customer.getName().toLowerCase()
                                        : "";

                                return bookingId.contains(keyword)
                                        || roomId.contains(keyword)
                                        || customerId.contains(keyword)
                                        || checkIn.contains(keyword)
                                        || checkOut.contains(keyword)
                                        || createdAt.contains(keyword)
                                        || status.contains(keyword)
                                        || customerName.contains(keyword);
                            })
                            .collect(Collectors.toList());

                    bookingList.setAll(filtered);
                }

                table.refresh();
            });
        }

        private boolean isEmpty(TextField tf) {
            return tf.getText() == null || tf.getText().trim().isEmpty();
        }

        // Add this field at the top of the class
        private InputValidator validator = new InputValidator();
        // Modify the getCustomerFromForm method to include validation
        private Customer getCustomerFromForm() {
            if (isEmpty(nameText) || isEmpty(email) || isEmpty(phone)
                    || isEmpty(cccd) || isEmpty(diaChi)
                    || ngaySInh.getValue() == null || genderGroup.getSelectedToggle() == null) {
                al.showErrorAlert("Vui lòng nhập đầy đủ thông tin khách hàng.");
                return null;
            }

            // Validate email
            if (!validator.isValidEmail(email.getText())) {
                al.showErrorAlert("Email không hợp lệ. Vui lòng kiểm tra lại.");
                return null;
            }

            // Validate phone number
            if (!validator.isValidPhoneNumber(phone.getText())) {
                al.showErrorAlert("Số điện thoại không hợp lệ. Vui lòng kiểm tra lại.");
                return null;
            }
            Customer customer = new Customer();
            customer.setName(nameText.getText());
            customer.setEmail(email.getText());
            customer.setPhone(phone.getText());
            customer.setIdNumber(cccd.getText());
            customer.setDiaChi(diaChi.getText());
            customer.setBirth(ngaySInh.getValue());

            RadioButton selectedGender = (RadioButton) genderGroup.getSelectedToggle();
            customer.setGender(selectedGender.getText());

            return customer;
        }
        private Customer getAccompanyingGuestFromForm() {
            if (isEmpty(nameText) || isEmpty(phone) || isEmpty(cccd) || isEmpty(diaChi)
                    || ngaySInh.getValue() == null || genderGroup.getSelectedToggle() == null) {
                al.showErrorAlert("Vui lòng nhập thông tin cần thiết của khách hàng đi cùng (tên, số điện thoại, CCCD, địa chỉ, ngày sinh, giới tính).");
                return null;
            }

            // Validate phone number
            if (!validator.isValidPhoneNumber(phone.getText())) {
                al.showErrorAlert("Số điện thoại không hợp lệ. Vui lòng kiểm tra lại.");
                return null;
            }

            String cccd = this.cccd.getText();
            Customer customer = new Customer();
            customer.setName(nameText.getText());
            customer.setPhone(phone.getText());
            customer.setIdNumber(cccd);
            customer.setDiaChi(diaChi.getText());
            customer.setBirth(ngaySInh.getValue());

            RadioButton selectedGender = (RadioButton) genderGroup.getSelectedToggle();
            customer.setGender(selectedGender.getText());

            return customer;
        }
        @FXML
        private void checkIn() {
            if (booking == null) {
                al.showErrorAlert("Vui lòng chọn một đặt phòng trước khi check-in.");
                return;
            }

            Customer customer = getCustomerFromForm();
            if (customer == null) return;
            customer.setId(booking.getCustomerId());
            Task<Boolean> checkInTask = new Task<>() {
                @Override
                protected Boolean call() {
                    try {
                        updateProgress(0.1, 1);
                        updateMessage("Đang cập nhật thông tin khách hàng...");

                        boolean customerUpdated = cDao.update(customer);
                        if (!customerUpdated) {
                            return false;
                        }

                        updateProgress(0.4, 1);
                        updateMessage("Đang cập nhật trạng thái đặt phòng...");

                        boolean statusUpdated = rdao.updateBookingStatus(booking.getBookingId(), "Đang ở");
                        if (!statusUpdated) {
                            return false;
                        }

                        updateProgress(0.7, 1);
                        updateMessage("Đang lưu lịch sử lưu trú...");

                        boolean historyCreated = stayDao.insert(createHistory(customer.getId(),
                                booking.getCheckInDate(), booking.getCheckOutDate(),
                                "Người đặt phòng " + idPhongText.getText()));
                        if (!historyCreated) {
                            return false;
                        }

                        updateProgress(1, 1);
                        updateMessage("Hoàn tất check-in");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };

            // Create and display status label
            Label statusLabel = new Label();
            statusLabel.textProperty().bind(checkInTask.messageProperty());

            // Bind progress bar
            tienTrinh.progressProperty().bind(checkInTask.progressProperty());

            checkInTask.setOnSucceeded(event -> {
                tienTrinh.progressProperty().unbind();
                tienTrinh.setProgress(0);
                statusLabel.textProperty().unbind();

                if (checkInTask.getValue()) {
                    al.showInfoAlert("Check-in thành công");
                    AuditLogController.getAuditLog("booking", booking.getBookingId(), "Check-in", LoginController.account.getName());
                    bookingList.remove(booking);
                    table.refresh();
                    clearForm();
                    booktemp = booking;
                    booking = null;
                } else {
                    al.showErrorAlert("Check-in thất bại. Vui lòng thử lại.");
                }
            });

            Thread thread = new Thread(checkInTask);
            thread.setDaemon(true);
            thread.start();
        }
        private void clearForm() {
            nameText.clear();
            email.clear();
            phone.clear();
            cccd.clear();
            diaChi.clear();
            ngaySInh.setValue(null);
            genderGroup.selectToggle(null);
        }

        private void setupRowClickEvent() {
            table.setOnMouseClicked(this::handleRowClick);
        }
        @FXML
        public void addKhachHang() {
            if (booking != null) {
                al.showErrorAlert("Vui lòng bấm check-in");
                return;
            }

            if (booktemp == null) {
                al.showErrorAlert("Không có thông tin đặt phòng. Vui lòng check-in người đặt phòng trước.");
                return;
            }

            Customer customer = getAccompanyingGuestFromForm();
            if (customer == null) return;

            Task<Boolean> addCustomerTask = new Task<>() {
                @Override
                protected Boolean call() {
                    try {
                        updateProgress(0.1, 1);
                        updateMessage("Đang tạo mã khách hàng...");

                        TaoID taoID = new TaoID();
                        Set<String> usedIDs = cDao.getAllCustomerIDs();
                        String newID = taoID.randomIDKH(usedIDs);
                        customer.setId(newID);

                        updateProgress(0.4, 1);
                        updateMessage("Đang thêm thông tin khách hàng...");

                        boolean customerAdded = cDao.insertWithoutEmail(customer);
                        if (!customerAdded) {
                            return false;
                        }

                        updateProgress(0.7, 1);
                        updateMessage("Đang lưu lịch sử lưu trú...");

                        boolean historyCreated = stayDao.insert(createHistory(newID,
                                booktemp.getCheckInDate(), booktemp.getCheckOutDate(),
                                "Ở cùng với " + nameNguoiDat));
                        if (!historyCreated) {
                            return false;
                        }

                        updateProgress(1, 1);
                        updateMessage("Hoàn tất thêm khách hàng");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };

            // Status and progress updates
            Label statusLabel = new Label();
            statusLabel.textProperty().bind(addCustomerTask.messageProperty());
            tienTrinh.progressProperty().bind(addCustomerTask.progressProperty());

            addCustomerTask.setOnSucceeded(event -> {
                tienTrinh.progressProperty().unbind();
                tienTrinh.setProgress(0);
                statusLabel.textProperty().unbind();

                if (addCustomerTask.getValue()) {
                    al.showInfoAlert("Thêm thông tin khách hàng thành công");
                    AuditLogController.getAuditLog("customer", customer.getId(), "Thêm khách hàng đi cùng", LoginController.account.getName());
                    clearForm();
                } else {
                    al.showErrorAlert("Thêm thông tin khách hàng thất bại");
                }
            });

            Thread thread = new Thread(addCustomerTask);
            thread.setDaemon(true);
            thread.start();
        }
        @FXML
        public void huyDatPhong(){
            if (booking == null) {
                al.showErrorAlert("Vui lòng chọn một đặt phòng trước khi hủy.");
                return;
            }
            Task<Void> huyTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    updateProgress(0.5, 1);
                    boolean success = rdao.updateBookingStatus(booking.getBookingId(), "Đã hủy");
                    updateProgress(1, 1);
                    boolean success2=roomDao.updateStatus(booking.getRoomId(), 1);
                    Customer customer = cDao.searchById(booking.getCustomerId());
                    boolean success3 = cDao.updateCustomerStatus(customer.getId(), "Đã huỷ");
                    javafx.application.Platform.runLater(() -> {
                        tienTrinh.progressProperty().unbind();
                        tienTrinh.setProgress(0);
                        if (success && success2 && success3) {
                            al.showInfoAlert("Hủy đặt phòng thành công");
                            AuditLogController.getAuditLog("booking", booking.getBookingId(), "Hủy đặt phòng", LoginController.account.getName());
                            bookingList.remove(booking);
                            table.refresh();
                            clearForm();
                            booking = null;
                        } else {
                            al.showErrorAlert("Hủy đặt phòng thất bại. Vui lòng thử lại.");
                        }
                    });

                    return null;
                }
            };

            tienTrinh.progressProperty().bind(huyTask.progressProperty());
            Thread thread = new Thread(huyTask);
            thread.setDaemon(true);
            thread.start();
        }
        private void handleRowClick(MouseEvent event) {
            booking = table.getSelectionModel().getSelectedItem();
            if (booking != null) {
                Customer customer = new CustomerDao().searchById(booking.getCustomerId());
                nameNguoiDat=customer.getName();
                if (customer != null) {
                    fillCustomerDetails(customer);
                } else {
                    al.showErrorAlert("Không tìm thấy thông tin khách hàng.");
                }
            }
        }
        private void fillCustomerDetails(Customer customer) {
            nameText.setText(customer.getName());
            email.setText(customer.getEmail());
            phone.setText(customer.getPhone());
            cccd.setText(customer.getIdNumber());
            diaChi.setText(customer.getDiaChi());
            ngaySInh.setValue(customer.getBirth());
            this.idPhongText.setText(booking.getRoomId());

            String gender = customer.getGender();
            if (gender != null) {
                switch (gender) {
                    case "Nam" -> nam.setSelected(true);
                    case "Nữ" -> nu.setSelected(true);
                    default -> khac.setSelected(true);
                }
            } else {
                genderGroup.selectToggle(null);
            }
        }
        public  StayHistory createHistory(String customerId, LocalDate checkin, LocalDate checkout, String node) {
            StayHistory history = new StayHistory();
            history.setCustomerID(customerId);
            history.setRoomID(idPhongText.getText());
            history.setCheckIn(checkin);
            history.setCheckOut(checkout);
            history.setNote(node);
            return history;
        }
    }
