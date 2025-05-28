package Model;

import java.time.LocalDate;

public class Employee extends Person {
    private LocalDate hireDate;
    public Employee() {
        super();
    }
    public String getHireDate() {
        return hireDate.toString();
    }
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
