package Controller.Invoice;

import Controller.Employee.RoomBookingController;
import Dao.Employee.*;
import Model.Booking;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import Model.CardLevel;
import Model.Invoice;
import Model.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class InvoiceController {
    private Invoice invoice;
    RoomBookingDao rDao;
    // Format for currency display
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    // Format for date display
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ArrayList<Service> serviceList;
    public InvoiceController() {
        invoice = new Invoice();
        rDao = new RoomBookingDao();
        serviceList = new ArrayList<>();
    }

    @FXML
    private Label bookingID;

    @FXML
    private Label soNgay;
    @FXML
    private Label customerID;
    @FXML
    private Label giamGia;
    @FXML
    private Label name;
    @FXML
    private TableColumn<Service, String> nameServiceColum;
    @FXML
    private Label ngayLap;
    @FXML
    private TableColumn<Service, Double> priceColum;
    @FXML
    private Label serviceBookingID;
    @FXML
    private TableColumn<Service, Integer> soluongcolum;
    @FXML
    private Label thue;
    @FXML
    private Label tienDaThanhToan;
    @FXML
    private Label tongBill;
    @FXML
    private Label tongThanhToan;
    @FXML
    private TableView<Service> service;
    @FXML
    private Button xuatHoaDon;
    @FXML
    public void setXuatHoaDon() {
        // Create file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu hóa đơn PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("HoaDon_" + invoice.getInvoiceID() + ".pdf");

        // Show save dialog
        File file = fileChooser.showSaveDialog(xuatHoaDon.getScene().getWindow());

        if (file != null) {
            try {
                // Use InvoiceExporter to generate the PDF
                InvoiceExporter exporter = new InvoiceExporter();
                exporter.exportInvoiceToPdf(file.getAbsolutePath(), invoice);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Xuất hóa đơn thành công!");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi xuất PDF");
                alert.setHeaderText("Không thể xuất hóa đơn");
                alert.setContentText("Lỗi: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        updateUI();
    }

    private void updateUI() {
        if (invoice != null) {
            populateServiceTable();
            // Fix the customer ID display (was showing issue date)
            String customerId = getCustomerID();
            bookingID.setText("Mã Đặt Phòng: " + invoice.getBookingID());
            customerID.setText("Mã Khách Hàng: " + customerId);
            name.setText("Tên Khách Hàng: " + getName());
            ngayLap.setText("Ngày lập:  "+invoice.getIssueDate());
            serviceBookingID.setText("Mã Dịch Vụ: " + invoice.getServiceBookingID());
            soNgay.setText("Số Ngày: " + rDao.getBookingById(invoice.getBookingID()).getNumberOfDays());
            // Format currency values
            double totalAmount = invoice.getTotalAmount();
            double tienDaThanhToanTruoc = getTienDaThanhToanTruoc();
            double discountAmount = getGiamGia();
            double taxAmount = invoice.getTax() * 0.01 * totalAmount;
            double finalAmount = totalAmount - discountAmount + taxAmount - tienDaThanhToanTruoc;
            tienDaThanhToan.setText("-"+formatCurrency(tienDaThanhToanTruoc));
            tongBill.setText(formatCurrency(totalAmount));
            giamGia.setText("-" + formatCurrency(discountAmount));
            thue.setText("+" + formatCurrency(taxAmount));
            tongThanhToan.setText(formatCurrency(finalAmount));
        } else {
            System.out.println("Cannot update UI: invoice is null");
        }
    }

    public double getGiamGia() {
        CardLevelDao cDao = new CardLevelDao();
        String customerId = getCustomerID();
        if (customerId == null || customerId.isEmpty()) {
            return 0.0;
        }

        CardLevel card = cDao.selectByCustomerId(customerId);
        if (card == null) {
            System.out.println("getGiamGia - card is null");
            return 0.0;
        }

        double discountPercentage = card.getDiscountPercentage();
        double totalAmount = invoice.getTotalAmount();

        return discountPercentage * 0.01 * totalAmount;
    }

    public String getCustomerID() {
        if (invoice == null || invoice.getBookingID() == null) {
            return "";
        }
        return rDao.getCustomerID(invoice.getBookingID());
    }

    public String getName() {
        String customerId = getCustomerID();
        if (customerId == null || customerId.isEmpty()) {
            return "Không tìm thấy khách hàng";
        }
        CustomerDao cusDao = new CustomerDao();
        return cusDao.getNameById(customerId);
    }

    // Helper method to format currency values
    private String formatCurrency(double amount) {
        return currencyFormat.format(amount).replace("₫", " VND");
    }
    public ArrayList<Service> getServiceList() {
        ServiceBookingDetailDao serviceBookingDetailDao = new ServiceBookingDetailDao();
        serviceList = serviceBookingDetailDao.getServicesByBookingId(invoice.getServiceBookingID());
        return serviceList;
    }
    private void populateServiceTable() {
        // Clear existing items
        service.getItems().clear();

        // Set up cell value factories if not already set
        nameServiceColum.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        priceColum.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());

        // For quantity column (assuming count is always 1 for now)
        soluongcolum.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(1).asObject());

        // Get services for this invoice
        serviceList = getServiceList();

        // Add existing services to the table
        service.getItems().addAll(serviceList);

        // Add "Thuê Phòng" record directly
        double roomPrice = giaPhong(); // Get room price from giaPhong()
        Service roomService = new Service("Thuê Phòng", roomPrice); // Temporary Service object for simplicity
        service.getItems().add(roomService); // Add the room rental record
    }
    public double getTienDaThanhToanTruoc(){
        Booking booking=rDao.getBookingById(invoice.getBookingID());
        if (booking == null) {
            return 0.0;
        }
        RoomDao roomDao=new RoomDao();
        return booking.calculateTotalRoomCost(giaPhong())*0.5;
    }
    public double giaPhong(){
        RoomDao roomDao=new RoomDao();
        return  roomDao.getPriceById(rDao.getBookingById(invoice.getBookingID()).getRoomId());
    }
}