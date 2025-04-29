package Controller.Login;
import Dao.DangNhap.LoginDao;
import Model.Account;
import encryption.maHoaMatKhau;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Alert.alert;
import java.io.IOException;

public class LoginController {
    private final alert al = new alert();
    public static Account account;
    public LoginController() {
    }

    @FXML
    private Button forgot;

    public void forgot() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/DangNhap/QuenMK.fxml"));
        Parent root = loader.load();

        // Tạo cửa sổ mới
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private Button login;

    @FXML
    public void login() throws IOException {
        String username = this.user.getText();
        String passWord = this.pass.getText();

        if (username.trim().isEmpty() || passWord.trim().isEmpty()) {
            return;
        }

        maHoaMatKhau maHoa = new maHoaMatKhau();
        passWord = maHoa.hashPassword(passWord);
        LoginDao loginDao = new LoginDao();
        account=loginDao.checkAccount(username, passWord);
        if (account!=null && account.getRole()==1) {
            this.dnsai.setText("");
            al.showInfoAlert("Horizon Bliss xin chào " + account.getName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/Nhan_Vien/HomeEmployee.fxml"));
            Parent root = loader.load();

            // Tạo cửa sổ mới
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Đóng cửa sổ hiện tại
            Stage currentStage = (Stage) ((Node) this.login).getScene().getWindow();
            currentStage.close();
        }
        else if(account!=null && account.getRole()==2){
            this.dnsai.setText("");
            al.showInfoAlert("Horizon Bliss xin chào " + account.getName());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/Quan_ly/HomeAdmin.fxml"));
            Parent root = loader.load();

            // Tạo cửa sổ mới
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Đóng cửa sổ hiện tại
            Stage currentStage = (Stage) ((Node) this.login).getScene().getWindow();
            currentStage.close();

        }
        else {
            this.dnsai.setText("Sai tài khoản hoặc mật khẩu");
        }
    }

    @FXML
    private Label dnsai;

    @FXML
    private PasswordField pass;

    @FXML
    private TextField user;
}
