package Model;

public class Position {
    private String employeeID;
    private int position;
    private String name;

    // Constructor
    public Position(String idStaff, String name,  int position) {
        this.employeeID = idStaff;
        this.employeeID = employeeID;
    }

    // Getter và Setter cho idStaff
    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String idStaff) {
        this.employeeID= idStaff;
    }

    // Getter và Setter cho position
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Phương thức trả về tên chức vụ
    public String getPositionName() {
        switch (position) {
            case 1: return "Lễ tân";
            case 2: return "Buồng phòng";
            case 3: return "Dịch vụ";
            case 4: return "Food";
            case 5: return "Kỹ thuật";
            case 6: return "Kinh doanh";
            case 7: return "Tài chính";
            case 8: return "An ninh";
            case 9: return "Giải trí";
            case 10: return "Quản lý";
            default: return "Chức vụ không xác định"; // Trả về giá trị mặc định nếu không có trong các trường hợp
        }
    }
}
