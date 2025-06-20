package Controller.Login;
import Dao.DangNhap.ForgotPassDao;
import Alert.Alert;
import Mail.Mail;
import encryption.maHoaMatKhau;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;


public class quenMKController {
    private final Alert alert=new Alert();
    private int code;
    private int inputCode;
    @FXML
    private Button comfirm;
    public void comfirm(){
        String newPass1=this.textPass1.getText();
        String newPass2=this.textPass2.getText();
        if(newPass1.trim().isEmpty() || newPass2.trim().isEmpty() || this.textEmail.getText().isEmpty()){
            alert.showErrorAlert("Không được bỏ trôống ô nào");
        }
        if(!newPass1.equals(newPass2)){
           alert.showErrorAlert("mật khẩu không khơ");
           return;
        }
        maHoaMatKhau maHoa=new maHoaMatKhau();
        newPass1=maHoa.hashPassword(newPass1);
        try {
            inputCode = Integer.parseInt(this.textCode.getText());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(inputCode!=code){
            alert.showErrorAlert("sai mã xác nhận");
            return;
        }
        ForgotPassDao dao=new ForgotPassDao();
        if(dao.updatePassWord(this.textEmail.getText(), newPass1)){
            alert.showInfoAlert( "Cập nhật mật khẩu thành công");
            Stage stage = (Stage) comfirm.getScene().getWindow();
            stage.close();
        }
        else{
            alert.showErrorAlert("Cập nhật mật khẩu thất bại");
        }

    }
    @FXML
    private Button sendButton;
    public void send() {
        String email = this.textEmail.getText().trim();

        if (email.isEmpty()) {
            alert.showErrorAlert("Không được để Email trống");
            return;
        }

        // Tạo Alert thông báo đang gửi
        javafx.scene.control.Alert sendingAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        sendingAlert.setTitle("Đang gửi mail...");
        sendingAlert.setHeaderText(null);
        sendingAlert.setContentText("Đang gửi mã xác nhận... (đã chờ 0 giây)");
        sendingAlert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false); // Ẩn nút OK
        sendingAlert.show();

        // Biến đếm giây
        final int[] seconds = {0};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds[0]++;
            sendingAlert.setContentText("Đang gửi mã xác nhận... (đã chờ " + seconds[0] + " giây)");
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Thread gửi email
        new Thread(() -> {
            try {
                ForgotPassDao dao = new ForgotPassDao();
                if (dao.searchAccount(email)) {
                    Mail mail = new Mail(email); // Thực hiện gửi mail tại đây
                    code = mail.getMa(); // Lấy mã để xác thực

                    // Gửi xong → dừng đếm và hiển thị thông báo
                    Platform.runLater(() -> {
                        timeline.stop();
                        sendingAlert.close();
                        alert.showInfoAlert("Gửi mã thành công sau " + seconds[0] + " giây.");
                    });
                } else {
                    Platform.runLater(() -> {
                        timeline.stop();
                        sendingAlert.close();
                        alert.showErrorAlert("Email không tồn tại trong hệ thống.");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    timeline.stop();
                    sendingAlert.close();
                    alert.showErrorAlert("Gửi email thất bại: " + e.getMessage());
                });
            }
        }).start();
    }


    @FXML
    private TextField textCode;

    @FXML
    private TextField textEmail;

    @FXML
    private PasswordField textPass1;

    @FXML
    private PasswordField textPass2;



}
