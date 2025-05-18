package Mail;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailThongBaoHotel {
    private final String from = "thinhphu479@gmail.com"; // Email người gửi
    private final String pass = "xjzz vrmu fpks cfkd";   // Mật khẩu ứng dụng
    private String to;         // Email người nhận
    private String maDatPhong;
    private String soPhong;
    private String giaPhong;

    public MailThongBaoHotel(String to, String maDatPhong, String soPhong, String giaPhong) {
        this.to = to;
        this.maDatPhong = maDatPhong;
        this.soPhong = soPhong;
        this.giaPhong = giaPhong;
        sendEmail();
    }

    private void sendEmail() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Đặt phòng thành công");

            String content =
                    "--HỆ THỐNG QUẢN LÝ KHÁCH SẠN - Horizon Bliss--\n" +
                            "Chúc mừng bạn đã đặt phòng thành công!\n\n" +
                            "🆔 Mã đặt phòng: " + maDatPhong + "\n" +
                            "🚪 Số phòng: " + soPhong + "\n" +
                            "💰 Giá phòng: " + giaPhong + " \n" +
                            "Vui lòng đến khu vực Check-in để hoàn tất quá trình nhận phòng.\n" +
                            "Xin cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi.";

            message.setText(content);
            Transport.send(message);
            System.out.println("Thông báo đặt phòng đã gửi thành công tới: " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
