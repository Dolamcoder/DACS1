package Model;

import java.time.LocalDate;
import java.util.List;

public class Invoice {
    private String invoiceId;
    private String bookingId;
    private String customerId;
    private double totalAmount;
    private String bookingServiceID;
    private List<Service> servicesUsed;
    private LocalDate issueDate;
    private String status; // Paid, Unpaid

    // Constructor
    public Invoice(String invoiceId, String bookingId, String customerId,
                   List<Service> servicesUsed, LocalDate issueDate, String status) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.servicesUsed = servicesUsed;
        this.issueDate = issueDate;
        this.status = status;
        this.totalAmount = calculateTotalAmount(); // Tự động tính tổng tiền
    }

    public String getBookingServiceID() {
        return bookingServiceID;
    }

    public void setBookingServiceID(String bookingServiceID) {
        this.bookingServiceID = bookingServiceID;
    }

    // Tính tổng số tiền dựa trên danh sách dịch vụ
    private double calculateTotalAmount() {
        double sum = 0;
        for (Service service : servicesUsed) {
            sum += service.getPrice();
        }
        return sum;
    }

    // Getter và Setter
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<Service> getServicesUsed() {
        return servicesUsed;
    }

    public void setServicesUsed(List<Service> servicesUsed) {
        this.servicesUsed = servicesUsed;
        this.totalAmount = calculateTotalAmount(); // Cập nhật lại tổng tiền
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
