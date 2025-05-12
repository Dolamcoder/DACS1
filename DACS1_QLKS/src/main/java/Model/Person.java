package Model;

import java.time.LocalDate;

public class Person {
    private String id;
    private String name;
    private String diaChi;
    private String phone;
    private String gender;
    private LocalDate birth;
    private String email;
    private String idNumber;
    // Constructor
    public Person(){

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

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setIdNumber(String idNumber){
        this.idNumber=idNumber;
    }
    public String getIdNumber(){
        return this.idNumber;
    }
}
