package Model;

import encryption.maHoaMatKhau;

public class Account {
    private String userName;
    private String password;
    private String email;
    private String name;
    private int role; // 0: Khách hàng, 1: Nhân viên, 2: Quản lý
    private String path;
    int stt;
    maHoaMatKhau maHoa=new maHoaMatKhau();
    // Constructor
    public Account(String userName, String password, String email, String name, int role) {
        this.userName = userName;
        this.password = maHoa.hashPassword(password);
        this.email = email;
        this.name = name;
        this.role = role;
    }
    public Account() {
        // Constructor không tham số
    }
    // Getter và Setter cho userName
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Getter và Setter cho password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter và Setter cho email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và Setter cho name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter và Setter cho role
    public int getRole() {
        return role;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getStt(){
        return stt;
    }
    public void setRole(int role) {
        this.role = role;
    }
    public void setStt(int stt) {
        this.stt = stt;
    }
    @Override
    public String toString() {
        return "Account{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", path='" + path + '\'' +
                ", stt=" + stt +
                ", maHoa=" + maHoa +
                '}';
    }
}
