package Controller.Employee;
import Dao.Employee.*;
import Mail.MailThongBaoHotel;
import Model.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import Alert.alert;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RoomBookingController {
    ArrayList<Room> listRoom;
    alert al=new alert();
    Type_roomDao tDao;
    private String email;
    RoomDao roomDao;
    private  double tongTienPhong=0;
    public RoomBookingController(){
        tDao=new Type_roomDao();
        roomDao=new RoomDao();
        listRoom=roomDao.selectAll();
    }
    @FXML
    private TextField CustomerID;

    @FXML
    private DatePicker ngayTra;

    @FXML
    private TextField RoomBookingID;
    @FXML
    private TextField RoomID;

    @FXML
    private TextField giaThue;

    @FXML
    private ComboBox<String> loaiPhong;

    @FXML
    private TextField loaiPhongText;

    @FXML
    private TextArea moTa;

    @FXML
    private TextField nameCustomer;

    @FXML
    private DatePicker ngayNhan;

    @FXML
    private TextField phone;

    @FXML
    private Button roomBook;
    @FXML
    private Button selectCustomer;

    @FXML
    private TextField soGiuong;

    @FXML
    private TextField kichThuocText;

    @FXML
    private TextField soNguoi;

    @FXML
    private TextField soPhong;

    @FXML
    private TableView<Room> tableRoom;

    @FXML
    private Button timKiem;
    public void timKiem(){
        if(this.loaiPhong.getValue()!=null && this.soGiuong.getText().trim().isEmpty() && this.soNguoi.getText().trim().isEmpty()){
            timKiemPhong1();
        }
        else if(this.loaiPhong.getValue()==null &&  !this.soGiuong.getText().trim().isEmpty() && !this.soNguoi.getText().trim().isEmpty()){
            timKiemPhong2();
        }
        else{
            al.showErrorAlert("Nếu chọn loại phòng thì không nhập số người và số giường");
        }
    }
    @FXML
    private TableColumn<Room, String> IdRoomColum;

    @FXML
    private TableColumn<Room, Integer> numberRoomColum;

    @FXML
    private TableColumn<Room, Integer> priceRoomColum;


    @FXML
    private TableColumn<Room, String> typeRoomColum;
    @FXML
    private Button service;
    @FXML
    private AnchorPane rootPane;
    public void selectCustomer() throws IOException {
        AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Nhan_Vien/SelectCustomer.fxml"));
        this.rootPane.getChildren().clear();
        this.rootPane.getChildren().add(selectPane);
    };
    public void setCustomerData() {
        Customer customer=selectCustomerController.customer;
        if(customer!=null) {
            this.CustomerID.setText(customer.getId());
            this.nameCustomer.setText(customer.getName());
            this.phone.setText(customer.getPhone());
            this.email=customer.getEmail();
        }
    }
    public void autoIDRoomBooking() {
        RoomBookingDao bookDao = new RoomBookingDao();
        ArrayList<Booking> listBooking = bookDao.selectAll();

        // Tạo Set chứa các ID hiện có (lấy ID booking từ từng Booking)
        Set<String> existingIDs = new HashSet<>();
        for (Booking b : listBooking) {
            existingIDs.add(b.getBookingId()); // giả sử getBookingID() là phương thức lấy ID booking kiểu String
        }

        TaoID id = new TaoID();
        // Giả sử bạn đã bổ sung hàm randomIDRoomBooking(Set<String> existingIDs)
        this.RoomBookingID.setText(id.randomIDRoomBooking(existingIDs));
    }

    public void listLoaiPhong() {
        // Tạo danh sách các loại phòng
        loaiPhong.setItems(FXCollections.observableArrayList(
                "Business Room",
                "Double Bed",
                "Deluxe",
                "Double Suite",
                "Executive",
                "Family Room",
                "Garden View",
                "Junior Suite",
                "Ocean View",
                "Royal King",
                "Single Room",
                "Standard",
                "Suite",
                "Twin Bed",
                "VIP Room"
        ));
    }
    public void dataTableRoom() {
        // Gán dữ liệu cho các cột bằng tên getter tương ứng trong Room
        this.IdRoomColum.setCellValueFactory(new PropertyValueFactory<>("id"));             // getId()
        this.numberRoomColum.setCellValueFactory(new PropertyValueFactory<>("number"));     // getNumber()
        this.priceRoomColum.setCellValueFactory(new PropertyValueFactory<>("price"));       // getPrice()
        this.typeRoomColum.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));    // getLoaiPhong()

        // Lọc danh sách chỉ lấy phòng có status == 1 (sẵn sàng)
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (Room room : listRoom) {
            if (room.status == 1) { // hoặc room.getStatusInt() nếu bạn viết thêm phương thức đó
                availableRooms.add(room);
            }
        }

        // Đổ dữ liệu vào bảng
        tableRoom.setItems(FXCollections.observableArrayList(availableRooms));
    }
    public void timKiemPhong1(){
        if (this.ngayNhan.getValue() == null && this.ngayTra.getValue()==null) {
            al.showErrorAlert("vui lòng nhập ngày nhận và ngày trả");
            return;
        }
        String typeR = this.loaiPhong.getValue();
        if(typeR.isEmpty()) {
            return;
        }
        typeR=this.tachMaLoaiPhong(typeR);
        LocalDate localDate = this.ngayNhan.getValue();
        Date ngayNhan = Date.valueOf(localDate);
        // Lấy danh sách phòng mới từ RoomDao
        this.listRoom.clear();
        this.listRoom = roomDao.timKiemPhong1(typeR, ngayNhan, listRoom);
        // Cập nhật lại bảng (TableView) với dữ liệu mới
        tableRoom.setItems(FXCollections.observableArrayList(listRoom));
    }
    public void timKiemPhong2(){
        if (this.ngayNhan.getValue() == null && this.ngayTra.getValue()==null) {
            al.showErrorAlert("vui lòng nhập ngày nhận và ngày trả");
            return;
        }
        if(this.soGiuong.getText().trim().isEmpty() || this.soNguoi.getText().trim().isEmpty()){
            al.showErrorAlert("Vui lòng nhập vào số giường và số người");
            return;
        }
        int bedNumber = 0;
        int peopleNumber=0;
        try {
            bedNumber = Integer.parseInt(this.soGiuong.getText());
            peopleNumber=   Integer.parseInt((this.soNguoi.getText()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        this.listRoom.clear();
        ArrayList<String> listTypeRoom=tDao.timKiemLoaiPhong(bedNumber, peopleNumber);
        if(listTypeRoom==null){
            al.showErrorAlert("Không tim thấy phòng phù hợp");
            return;
        }
        LocalDate localDate = this.ngayNhan.getValue();
        Date ngayNhan = Date.valueOf(localDate);
        for(String x: listTypeRoom){
            this.listRoom=roomDao.timKiemPhong1(x, ngayNhan, this.listRoom);
        }
        tableRoom.setItems(FXCollections.observableArrayList(listRoom));

    }
    @FXML
    public void layDataTableView(){
        Room room= this.tableRoom.getSelectionModel().getSelectedItem();
        if(room==null) {
            return;
        }
        type_room typeRoom=tDao.timKiem(room.getLoaiPhong());
        this.RoomID.setText(room.getId());
        this.soPhong.setText(room.getNumber()+"");
        this.loaiPhongText.setText(typeRoom.getNameLoaiPhong());
        this.kichThuocText.setText(typeRoom.getKichThuoc()+" mét vuông");
        this.giaThue.setText(room.getPrice()+" VND");
        this.moTa.setText(typeRoom.getMoTa());
    }
    public Booking layDatPhong() {
        if (CustomerID.getText().trim().isEmpty() || RoomID.getText().trim().isEmpty() ||
                ngayNhan.getValue() == null || ngayTra.getValue() == null) {
            al.showErrorAlert("Vui lòng nhập đầy đủ thông tin đặt phòng.");
            return null;
        }

        String bookingId = RoomBookingID.getText().trim();
        String customerId = CustomerID.getText().trim();
        String roomId = RoomID.getText().trim();
        LocalDate checkInDate = ngayNhan.getValue();
        LocalDate checkOutDate = ngayTra.getValue();

        if (checkOutDate.isBefore(checkInDate)) {
            al.showErrorAlert("Ngày trả phòng phải sau ngày nhận phòng.");
            return null;
        }

        Booking booking = new Booking(bookingId, customerId, roomId, checkInDate, checkOutDate, "Đã xác nhận");
        return booking;
    }
    public void datPhong() {
        Booking booking = layDatPhong();
        if (booking == null) {
            return;
        }
        // Tính tổng tiền phòng
        String priceText = giaThue.getText().replaceAll("[^0-9]", "");
        double roomPrice = Double.parseDouble(priceText);
        tongTienPhong = booking.calculateTotalRoomCost(roomPrice);

        RoomBookingDao bookingDao = new RoomBookingDao();
        boolean success = bookingDao.insert(booking);
        if (success) {
            if (roomDao.updateStatus(booking.getRoomId(), 2)) {
                // Gửi mail xác nhận
                sendBookingConfirmationEmail(booking);
            }
        } else {
            al.showErrorAlert("Đặt phòng thất bại. Vui lòng thử lại.");
        }
    }

    private void sendBookingConfirmationEmail(Booking booking) {
        Alert sendingAlert = new Alert(Alert.AlertType.INFORMATION);
        sendingAlert.setTitle("Đang gửi mail...");
        sendingAlert.setHeaderText(null);
        sendingAlert.setContentText("Hệ thống đang gửi email... (đã chờ 0 giây)");
        sendingAlert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        sendingAlert.show();
        final int[] seconds = {0};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds[0]++;
            sendingAlert.setContentText("Hệ thống đang gửi email... (đã chờ " + seconds[0] + " giây)");
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        new Thread(() -> {
            try {
                MailThongBaoHotel hotel = new MailThongBaoHotel(email, booking.getBookingId(),
                        soPhong.getText(), giaThue.getText(), "Quý khách đã thanh toán 50% tiền phòng");

                Platform.runLater(() -> {
                    timeline.stop();
                    sendingAlert.close();
                    al.showInfoAlert("Đặt phòng thành công /n" +
                            "Mã đặt phòng: " + booking.getBookingId() + "\n" +
                            "Số phòng: " + soPhong.getText() + "\n" +
                            "Giá thuê: " + giaThue.getText() + "\n" +
                            "Số Tiền thanh toán " + tongTienPhong *0.5+ " VND\n" + "50% tiền phòng");
                    clearForm();
                    autoIDRoomBooking();
                    dataTableRoom();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    timeline.stop();
                    sendingAlert.close();
                    al.showErrorAlert("Đặt phòng thành công, nhưng gửi mail thất bại!");
                    clearForm();
                    autoIDRoomBooking();
                    dataTableRoom();
                });
            }
        }).start();
    }

    @FXML
    public void clearForm() {
        CustomerID.clear();
        nameCustomer.clear();
        phone.clear();
        RoomBookingID.clear();
        RoomID.clear();
        giaThue.clear();
        loaiPhong.getSelectionModel().clearSelection();
        loaiPhongText.clear();
        moTa.clear();
        soGiuong.clear();
        kichThuocText.clear();
        soNguoi.clear();
        soPhong.clear();
        ngayNhan.setValue(null);
        ngayTra.setValue(null);
        tableRoom.getSelectionModel().clearSelection();

        // Load lại danh sách phòng còn trống vào table
        dataTableRoom();
    }
    @FXML
    public void Servicebutton() throws IOException {
        AnchorPane ServicePane = FXMLLoader.load(getClass().getResource("/org/FXML/Nhan_Vien/ServiceBooking.fxml"));
        this.rootPane.getChildren().clear();
        this.rootPane.getChildren().add(ServicePane);
    }
    @FXML
    public void initialize(){
        autoIDRoomBooking();
        listLoaiPhong();
        dataTableRoom();
        setCustomerData();
        // Hoặc dùng sự kiện click chuột
        soGiuong.setOnMouseClicked(event -> {
            loaiPhong.getSelectionModel().clearSelection();
        });
    }
    public String tachMaLoaiPhong(String loaiPhong) {
        switch (loaiPhong) {
            case "Business Room":
                return "BS";
            case "Double Bed":
                return "DB";
            case "Deluxe":
                return "DL";
            case "Double Suite":
                return "DS";
            case "Executive":
                return "EX";
            case "Family Room":
                return "FM";
            case "Garden View":
                return "GD";
            case "Junior Suite":
                return "JR";
            case "Ocean View":
                return "OC";
            case "Premium":
                return "PR";
            case "Royal King":
                return "RK";
            case "Single Room":
                return "SG";
            case "Standard":
                return "ST";
            case "Suite":
                return "SU";
            case "Twin Bed":
                return "TW";
            case "VIP Room":
                return "VP";
            default:
                return null;
        }
    }
}