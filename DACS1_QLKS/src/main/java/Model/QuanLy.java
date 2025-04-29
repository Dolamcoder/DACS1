package Model;
import java.util.ArrayList;
public class QuanLy {
    ArrayList<Room> roomList;
    ArrayList<Booking> bookingList;
    ArrayList<Customer> customerList;
    ArrayList<Service> servicesList;
    ArrayList<Employee> staffList;
    ArrayList<Salary> salaryList=new ArrayList();
    public QuanLy(){
        roomList=new ArrayList();
        bookingList=new ArrayList<>();
        customerList=new ArrayList<>();
        servicesList=new ArrayList<>();
        staffList=new ArrayList<>();
        salaryList=new ArrayList();
    }

}
