package Model;

public class Person {
    private String id;
    private String name;
    private String diaChi;
    private String phone;
    private String gender;
    private String birth;
    // Constructor
    public Person(String id, String name, String diaChi, String phone, String gender, String birth) {
        this.id = id;
        this.name = name;
        this.diaChi = diaChi;
        this.phone = phone;
        this.gender=gender;
        this.birth=birth;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
}
