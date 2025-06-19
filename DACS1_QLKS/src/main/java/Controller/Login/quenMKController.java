package Controller.Login;
import Dao.DangNhap.ForgotPassDao;
import Alert.Alert;
import Mail.Mail;
import encryption.maHoaMatKhau;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


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
    public void send(){
        String email = this.textEmail.getText();
        if(!email.trim().isEmpty()){
            ForgotPassDao dao=new ForgotPassDao();
            if(dao.searchAccount(email)) {
                Mail mail = new Mail(email);
                code=mail.getMa();
                alert.showInfoAlert("Gửi mã thành công");
            }
            else{
                alert.showErrorAlert("Sai email");
            }
        }
        else{
                alert.showErrorAlert( "Không được để Email trống");
        }
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
