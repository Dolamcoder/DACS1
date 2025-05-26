package Model;

public class ServiceBooking {
    private String serviceBookingID;
    private String roomID;
    private int quantity;
    private double totalAmount;

    // Constructor không tham số
    public ServiceBooking() {}

    // Constructor có tham số
    public ServiceBooking(String serviceBookingID, String roomID, int quantity, double totalAmount) {
        this.serviceBookingID = serviceBookingID;
        this.roomID = roomID;
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

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
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
                ", customerID='" + roomID + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
