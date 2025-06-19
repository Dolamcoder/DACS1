package Model;

public class Salary {
    private int salaryID;
    private String employeeID;
    private int amount;
    private int transportAllowance;
    private String name;
    private String position;
    public Salary() {}

    public Salary(int salaryID, String employeeID, int amount, int transportAllowance) {
        this.salaryID = salaryID;
        this.employeeID = employeeID;
        this.amount = amount;
        this.transportAllowance = transportAllowance;
    }

    // Getters and Setters
    public int getSalaryID() {
        return salaryID;
    }

    public void setSalaryID(int salaryID) {
        this.salaryID = salaryID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTransportAllowance() {
        return transportAllowance;
    }

    public void setTransportAllowance(int transportAllowance) {
        this.transportAllowance = transportAllowance;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
}
