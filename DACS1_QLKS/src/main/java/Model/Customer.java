package Model;

public class Customer extends Person {
    private String email;
    private String cccd;

    public Customer(String id, String name, String diaChi, String phone, String gender, String birth) {
        super(id, name, diaChi, phone, gender, birth);
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }
}
