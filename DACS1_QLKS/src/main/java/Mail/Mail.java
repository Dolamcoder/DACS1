package Mail;

import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class Mail {
    Random rd=new Random();
    private  int ma = 100000 + rd.nextInt(900000);
    private String maXacNhan="Mã Xác Nhận: ";
    private String email;
    public Mail(String email){
        this.email=email;
        final String from = "thinhphu479@gmail.com"; // Địa chỉ email người gửi
        final String pass = "xjzz vrmu fpks cfkd"; // Mật khẩu ứng dụng (nếu bật 2FA)
        String to = email; // Địa chỉ email người nhận
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587"); // Cổng TLS
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // Bật TLS
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Mã xác nhận đặt lại mật khẩu");
            message.setText(
                    "--HỆ THỐNG QUẢN LÝ KHÁCH SẠN - Horizon Bliss--\n" +
                            "Mã xác nhận của bạn là: " + this.ma + "\n" +
                            "Vui lòng KHÔNG gửi mã cho người khác!"
            );
            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public int getMa() {
        return ma;
    }

    public String getMaXacNhan() {
        return maXacNhan;
    }

    public void setRd(Random rd) {
        this.rd = rd;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public void setMaXacNhan(String maXacNhan) {
        this.maXacNhan = maXacNhan;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
