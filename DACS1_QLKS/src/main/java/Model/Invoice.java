package Model;
import java.sql.Date;

public class Invoice {
    private String invoiceID;
    private String bookingID;
    private double totalAmount;
    private double tax;
    private double amount;
    private Date issueDate;
    private String status; // "Đã thanh toán", "Chưa thanh toán", "Hủy"
    private String serviceBookingID; // ID của dịch vụ đi kèm nếu có
    public Invoice() {}

    public Invoice(String invoiceID, String bookingID, double totalAmount, double tax, double amount, Date issueDate, String status) {
        this.invoiceID = invoiceID;
        this.bookingID = bookingID;
        this.totalAmount = totalAmount;
        this.tax = tax;
        this.amount = amount;
        this.issueDate = issueDate;
        this.status = status;
    }
    // Getter - Setter
    public String getInvoiceID() { return invoiceID; }
    public void setInvoiceID(String invoiceID) { this.invoiceID = invoiceID; }

    public String getBookingID() { return bookingID; }
    public void setBookingID(String bookingID) { this.bookingID = bookingID; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getAmount() { return amount; }
    public void setAmount(double discount) { this.amount= discount; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getServiceBookingID() { return serviceBookingID; }
    public void setServiceBookingID(String serviceBookingID) { this.serviceBookingID = serviceBookingID;}

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceID='" + invoiceID + '\'' +
                ", bookingID='" + bookingID + '\'' +
                ", totalAmount=" + totalAmount +
                ", tax=" + tax +
                ", discount=" + amount+
                ", issueDate=" + issueDate +
                ", status='" + status + '\'' +
                ", serviceBookingID='" + serviceBookingID + '\'' +
                '}';
    }
}
