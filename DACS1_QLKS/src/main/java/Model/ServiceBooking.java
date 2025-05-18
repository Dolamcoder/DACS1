package Model;

public class ServiceBooking {
    private String serviceBookingID;
    private String customerID;
    private int quantity;
    private double totalAmount;

    // Constructor không tham số
    public ServiceBooking() {}

    // Constructor có tham số
    public ServiceBooking(String serviceBookingID, String customerID, int quantity, double totalAmount) {
        this.serviceBookingID = serviceBookingID;
        this.customerID = customerID;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    // Getter và Setter
    public String getServiceBookingID() {
        return serviceBookingID;
    }

    public void setServiceBookingID(String serviceBookingID) {
        this.serviceBookingID = serviceBookingID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "ServiceBooking{" +
                "serviceBookingID='" + serviceBookingID + '\'' +
                ", customerID='" + customerID + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
