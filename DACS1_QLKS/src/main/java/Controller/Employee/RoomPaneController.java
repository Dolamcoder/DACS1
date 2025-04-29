package Controller.Employee;

import Model.Room;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RoomPaneController {

    @FXML
    private Label priceLabel;

    @FXML
    private Label roomNumberLabel;

    @FXML
    private Label statusLabel;
    public void setRoomData(Room room) {
        this.roomNumberLabel.setText("Số Phòng: " + room.getNumber());
        this.priceLabel.setText("Giá phòng: " + room.getPrice()+"VNĐ");
        this.statusLabel.setText("Trạng Thái: " + room.getStatus());
    }
}
