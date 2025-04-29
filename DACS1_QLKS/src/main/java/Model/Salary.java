package Model;

public class Salary {
    private int tichCuc;  // Thưởng tích cực (ví dụ: thưởng hiệu suất tốt)
    private int tieuCuc;   // Phạt tiêu cực (ví dụ: phạt vi phạm quy định)
    private double salary; // Lương cơ bản của nhân viên
    private String employeeId;// ID của nhân viên

    // Constructor
    public Salary(int tichCuc, int tieuCuc, double salary, String employeeId) {
        this.tichCuc = tichCuc;
        this.tieuCuc = tieuCuc;
        this.salary = salary;
        this.employeeId=employeeId;
    }
    public int getTichCuc() {
        return tichCuc;
    }

    public void setTichCuc(int tichCuc) {
        this.tichCuc = tichCuc;
    }

    // Getter và Setter cho tieuCuc
    public int getTieuCuc() {
        return tieuCuc;
    }

    public void setTieuCuc(int tieuCuc) {
        this.tieuCuc = tieuCuc;
    }

    // Getter và Setter cho salary
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    // Getter và Setter cho idStaff
    public String getEmployeeId() {
        return employeeId;
    }

    public void setIdStaff(String idStaff) {
        this.employeeId= employeeId;
    }

    // Phương thức tính lương thực tế (lương cơ bản + thưởng - phạt)
    public double calculateNetSalary() {
        return salary + tichCuc - tieuCuc;
    }
}
