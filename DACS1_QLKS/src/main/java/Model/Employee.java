package Model;

import java.time.LocalDate;

public class Employee extends Person {
    private LocalDate hireDate;
    private String position;
    public Employee() {
        super();
    }
    public String getHireDate() {
        return hireDate.toString();
    }
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
}
