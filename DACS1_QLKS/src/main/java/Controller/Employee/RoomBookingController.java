package Controller.Employee;
import Dao.Employee.RoomDao;
import javafx.collections.FXCollections;
import Dao.Employee.RoomBookingDao;
import Model.Booking;
import Model.Room;
import Model.TaoID;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import Alert.alert;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class RoomBookingController {
    ArrayList<Room> listRoom;
    alert al=new alert();
    RoomDao roomDao;
    public RoomBookingController(){
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
    private AnchorPane contentPane;

    public void setContentPane(AnchorPane contentPane) {
        this.contentPane = contentPane;
    }

    public void selectCustomer() throws IOException {
        AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Nhan_Vien/SelectCustomer.fxml"));
        contentPane.getChildren().clear();
        contentPane.getChildren().add(selectPane);
    }
    @FXML
    private TextField soGiuong;

    @FXML
    private TextField soGiuongText;

    @FXML
    private TextField soNguoi;

    @FXML
    private TextField soPhong;

    @FXML
    private TableView<Room> tableRoom;

    @FXML
    private Button timKiem;
    public void timKiem(){
        this.timKiemPhong();
    }
    @FXML
    private TableColumn<Room, String> IdRoomColum;

    @FXML
    private TableColumn<Room, Integer> numberRoomColum;

    @FXML
    private TableColumn<Room, Integer> priceRoomColum;


    @FXML
    private TableColumn<Room, String> typeRoomColum;


    public void autoIDRoomBooking(){
        RoomBookingDao bookDao=new RoomBookingDao();
        ArrayList<Booking> listBooking=bookDao.selectAll();
        TaoID id=new TaoID();
        this.RoomBookingID.setText(id.taoIDRoomBooking(listBooking.size()+1));
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
    public void timKiemPhong(){
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
        Date ngayNhan = java.sql.Date.valueOf(localDate);
        // Lấy danh sách phòng mới từ RoomDao
        this.listRoom = roomDao.timKiemDatPhong(typeR, ngayNhan);
        // Cập nhật lại bảng (TableView) với dữ liệu mới
        tableRoom.setItems(FXCollections.observableArrayList(listRoom));
    }
    @FXML
    public void initialize(){
        autoIDRoomBooking();
        listLoaiPhong();
        dataTableRoom();
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
