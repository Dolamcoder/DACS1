package Controller.Admin;

import Controller.Login.LoginController;
import Dao.Employee.RoomBookingDao;
import Dao.Employee.RoomDao;
import Dao.Employee.ServiceBookingDao;
import Dao.Employee.Type_roomDao;
import Model.Room;
import Model.ServiceBooking;
import Model.type_room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import Alert.alert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class listRoomController implements Initializable {

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<Room, String> idColum;

    @FXML
    private TextField idPhongText;

    @FXML
    private TableColumn<Room, Integer> numberColum;

    @FXML
    private TableColumn<Room, Integer> priceColum;

    @FXML
    private TextField priceText;

    @FXML
    private Button removeButton;

    @FXML
    private Button saveButton;

    @FXML
    private TableColumn<Room, String> statusColum;

    @FXML
    private ComboBox<String> statusText;

    @FXML
    private AnchorPane timKiemText;

    @FXML
    private TableColumn<Room, String> typeRoomColum;

    @FXML
    private TextField typeRoomText;

    @FXML
    private Button updateButton;

    @FXML
    private Button xemChiTietButton;

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private TextField searchText;

    private RoomDao roomDao;
    private ObservableList<Room> roomList;
    private ObservableList<String> statusList;
    private alert al = new alert();
    private Type_roomDao typeRoomDao = new Type_roomDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roomDao = new RoomDao();
        roomList = FXCollections.observableArrayList();
        statusList = FXCollections.observableArrayList("Sẵn sàng", "Đã đặt", "Cần vệ sinh", "Đang sửa chữa");
        setUpTable();
        setUpSearch();
    }

    public void setUpTable(){
        // Initialize ComboBox
        statusText.setItems(statusList);

        // Initialize Table Columns
        idColum.setCellValueFactory(new PropertyValueFactory<>("id"));
        numberColum.setCellValueFactory(new PropertyValueFactory<>("number"));
        typeRoomColum.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        priceColum.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColum.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data into table
        loadRoomData();

        // Set up table selection listener
        roomTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void setUpSearch() {
        if (searchText != null) {
            searchText.textProperty().addListener((observable, oldValue, newValue) -> {
                filterRoomList(newValue);
            });
        }
    }

    private void filterRoomList(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            roomTable.setItems(roomList);
            return;
        }
        ObservableList<Room> filtered = FXCollections.observableArrayList();
        for (Room room : roomList) {
            if (room.getId().toLowerCase().contains(keyword.toLowerCase()) ||
                room.getLoaiPhong().toLowerCase().contains(keyword.toLowerCase()) ||
                String.valueOf(room.getPrice()).contains(keyword) ||
                String.valueOf(room.getNumber()).contains(keyword) ||
               (room.getStatus()).toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(room);
            }
        }
        roomTable.setItems(filtered);
    }

    private String getStatusString(int status) {
        switch (status) {
            case 1: return "Sẵn sàng";
            case 2: return "Đã đặt";
            case 3: return "Cần vệ sinh";
            case 4: return "Đang sửa chữa";
            default: return "Sẵn sàng";
        }
    }

    @FXML
    void add() {
        if (!checkTextField()) {
            return;
        }
        if (checkId()) {
            al.showErrorAlert("Mã phòng đã tồn tại, vui lòng nhập mã phòng khác!");
            return;
        }
        try {
            Room room = new Room();
            room.setId(idPhongText.getText());
            room.setNumber(getNumberOfRooms(idPhongText.getText()));
            room.setLoaiPhong(typeRoomText.getText());
            room.setPrice(Integer.parseInt(priceText.getText()));
            room.setStatus(convertStatusToInt(statusText.getValue()));
            System.out.println(room);
            if (roomDao.insert(room)) {
                AuditLogController.getAuditLog("room", room.getId(), "Thêm phòng", LoginController.account.getName());
                // Add the new room to the ObservableList and refresh the table
                roomList.add(room);
                al.showInfoAlert("Thêm phòng thành công!");
                clearFields();
            } else {
                al.showErrorAlert("Thêm phòng thất bại!");
            }
        } catch (NumberFormatException e) {
            al.showErrorAlert("Giá phòng phải là một số hợp lệ!");
        }
    }

    @FXML
    void remove() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            al.showErrorAlert("Vui lòng chọn một phòng để xóa!");
            return;
        }
        if(checkBooking(selectedRoom.getId()) || checkServiceBooking(selectedRoom.getId())) {
            al.showErrorAlert("Phòng này đang có đặt phòng hoặc dịch vụ, không thể xóa!");
            return;
        }
        if (roomDao.delete(selectedRoom.getId())) {
            // Remove the selected room from the ObservableList and refresh the table
            roomList.remove(selectedRoom);
            al.showInfoAlert("Xóa phòng thành công!");
            AuditLogController.getAuditLog("room", selectedRoom.getId(), "Xóa phòng", LoginController.account.getName());
            clearFields();
        } else {
            al.showErrorAlert("Xóa phòng thất bại!");
        }
    }

    @FXML
    private ProgressBar progressBar;

    @FXML
    void save() {
        // Set up file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách phòng");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("DanhSachPhong.xlsx");

        // Show save dialog
        java.io.File file = fileChooser.showSaveDialog(saveButton.getScene().getWindow());

        if (file != null) {
            // Initialize progress
            progressBar.setProgress(0);

            // Create a background task
            Task<Void> exportTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Create workbook and sheet
                    Workbook workbook = new XSSFWorkbook();
                    Sheet sheet = workbook.createSheet("Danh sách phòng");

                    // Create header row
                    Row headerRow = sheet.createRow(0);
                    String[] headers = {"Mã Phòng", "Số Phòng",  "Giá Phòng", "Trạng Thái",
                            "Tên Loại Phòng", "Số Giường", "Sức Chứa", "Kích Thước", "Mô Tả"};

                    // Style for header
                    CellStyle headerStyle = workbook.createCellStyle();
                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerStyle.setFont(headerFont);

                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                        cell.setCellStyle(headerStyle);
                        sheet.autoSizeColumn(i);
                    }

                    int totalRooms = roomList.size();
                    int currentRoom = 0;

                    // Create data rows
                    for (Room room : roomList) {
                        if (isCancelled()) {
                            break;
                        }

                        // Update progress
                        currentRoom++;
                        updateProgress(currentRoom, totalRooms);

                        // Get type_room data for this room
                        type_room typeRoom = typeRoomDao.timKiem(room.getLoaiPhong());

                        int rowIndex = sheet.getLastRowNum() + 1;
                        Row row = sheet.createRow(rowIndex);

                        // Room data
                        row.createCell(0).setCellValue(room.getId());
                        row.createCell(1).setCellValue(room.getNumber());
                        row.createCell(2).setCellValue(room.getPrice());
                        row.createCell(3).setCellValue(room.getStatus());

                        // Type_room data
                        if (typeRoom != null) {
                            row.createCell(4).setCellValue(typeRoom.getNameLoaiPhong());
                            row.createCell(5).setCellValue(typeRoom.getSoGiuong());
                            row.createCell(6).setCellValue(typeRoom.getSucChua() );
                            row.createCell(7).setCellValue(typeRoom.getKichThuoc()+" m2");
                            row.createCell(8).setCellValue(typeRoom.getMoTa());
                        }
                    }
                    // Auto-size columns for better readability
                    for (int i = 0; i < headers.length; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    // Write the workbook to file
                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        workbook.write(fileOut);
                    }

                    // Close workbook
                    workbook.close();
                    return null;
                }
            };

            // Bind progress bar to task progress
            progressBar.progressProperty().bind(exportTask.progressProperty());

            // Handle task completion
            exportTask.setOnSucceeded(e -> {
                progressBar.progressProperty().unbind();
                progressBar.setProgress(1.0);
                al.showInfoAlert("Xuất danh sách phòng thành công!");
                AuditLogController.getAuditLog("room", "Tất cả bản ghi", "Xuất danh sách phòng", LoginController.account.getName());
            });
            // Handle exceptions
            exportTask.setOnFailed(e -> {
                progressBar.progressProperty().unbind();
                progressBar.setProgress(0);
                Throwable exception = exportTask.getException();
                al.showErrorAlert("Lỗi khi xuất file Excel: " + exception.getMessage());
                exception.printStackTrace();
            });

            // Start the task in a new thread
            new Thread(exportTask).start();
        }
    }

    @FXML
    void update() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            al.showErrorAlert("Vui lòng chọn một phòng để cập nhật!");
            return;
        }
        if (!checkTextField()) {
            return;
        }

        try {
            if (selectedRoom.getStatus().equals("Đã đặt")) {
                al.showErrorAlert("Không cho phép cập nhât vào lúc này");
            } else {
                updateNonBookedRoom(selectedRoom);
            }
        } catch (NumberFormatException e) {
            al.showErrorAlert("Giá phòng phải là một số hợp lệ!");
        }
    }

    private void updateNonBookedRoom(Room room) {
        try {
            // Update all fields including status
            room.setNumber(getNumberOfRooms(idPhongText.getText()));
            room.setLoaiPhong(typeRoomText.getText());
            room.setPrice(Integer.parseInt(priceText.getText()));
            room.setStatus(convertStatusToInt(statusText.getValue()));

            if (roomDao.update(room)) {
                roomTable.refresh();
                al.showInfoAlert("Cập nhật phòng thành công!");
                AuditLogController.getAuditLog("room", room.getId(), "Cập nhật phòng", LoginController.account.getName());
                clearFields();
            } else {
                al.showErrorAlert("Cập nhật phòng thất bại!");
            }
        } catch (NumberFormatException e) {
            throw e; // Propagate to caller
        }
    }

    @FXML
    void xemChiTiet() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            al.showErrorAlert("Vui lòng chọn một phòng để xem chi tiết!");
            return;
        }
        // Lấy thông tin loại phòng
        type_room tRoom = typeRoomDao.timKiem(selectedRoom.getLoaiPhong());
        String typeRoomInfo = "";
        if (tRoom != null) {
            typeRoomInfo = String.format("\nTên loại phòng: %s\nSố giường: %d\nSức chứa: %d\nKích thước: %.2f\nMô tả: %s",
                    tRoom.getNameLoaiPhong(), tRoom.getSoGiuong(), tRoom.getSucChua(), tRoom.getKichThuoc(), tRoom.getMoTa());
        }
        String details = String.format("Room ID: %s\nNumber: %d\nType: %s\nPrice: %d\nStatus: %s%s",
                selectedRoom.getId(),
                selectedRoom.getNumber(),
                selectedRoom.getLoaiPhong(),
                selectedRoom.getPrice(),
                selectedRoom.getStatus(),
                typeRoomInfo);
        al.showInfoAlert(details);
    }

    private void loadRoomData() {
        roomList.clear();
        roomList.addAll(roomDao.selectAll());
        roomTable.setItems(roomList);
    }

    private void populateFields(Room room) {
        idPhongText.setText(room.getId());
        typeRoomText.setText(room.getLoaiPhong());
        priceText.setText(room.getPrice() + "");
        statusText.setValue(room.getStatus());
    }

    private void clearFields() {
        idPhongText.clear();
        typeRoomText.clear();
        priceText.clear();
        statusText.getSelectionModel().clearSelection();
    }

    private int convertStatusToInt(String status) {
        if (status == null) return 1;
        switch (status) {
            case "Sẵn sàng":
                return 1;
            case "Đã đặt":
                return 2;
            case "Cần vệ sinh":
                return 3;
            case "Đang sửa chữa":
                return 4;
            default:
                return 1;
        }
    }

    public int getNumberOfRooms(String roomCode) {
        try {
            return Integer.parseInt(roomCode.substring(1));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new NumberFormatException("Mã phòng không hợp lệ! Vui lòng nhập mã phòng đúng định dạng (ví dụ: R101).");
        }
    }

    private boolean checkTextField() {
        if (idPhongText.getText().isEmpty() || typeRoomText.getText().isEmpty() || priceText.getText().isEmpty() || statusText.getValue() == null) {
            al.showErrorAlert("Vui lòng điền đầy đủ thông tin phòng!");
            return false;
        }
        try {
            Integer.parseInt(priceText.getText());
            getNumberOfRooms(idPhongText.getText()); // Validate room code format
            return true;
        } catch (NumberFormatException e) {
            al.showErrorAlert(e.getMessage());
            return false;
        }
    }
    public boolean checkBooking(String roomId) {
        RoomBookingDao roomBookingDao=new RoomBookingDao();
        return roomBookingDao.getBookingByRoomId(roomId)!=null;
    }
    public boolean checkServiceBooking(String roomId) {
        ServiceBookingDao serviceBookingDao=new ServiceBookingDao();
        return serviceBookingDao.getByRoomId(roomId) != null;
    }
    public boolean checkId(){
        return roomDao.getPriceById(idPhongText.getText())>0;
    }
}

