package Model;

import java.time.LocalDate;

public class Employee extends Person {
    private String email;
    private LocalDate hireDate;
    public Employee(String id, String name, String diaChi, String phone, String gender, String birth) {
        super(id, name, diaChi, phone, gender, birth);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
