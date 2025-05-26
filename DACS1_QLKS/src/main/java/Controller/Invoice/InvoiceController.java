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
    private Button xuatHoaDon;@FXML
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
                exportInvoiceToPdf(file.getAbsolutePath());
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

    private void exportInvoiceToPdf(String filePath) throws IOException, DocumentException {
        // Create document with margins similar to the UI
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Load a font that supports Vietnamese (e.g., Times New Roman with Unicode support)
        String fontPath = "C:/Windows/Fonts/times.ttf"; // Path to Times New Roman font on Windows
        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        // Define fonts using the Unicode-supporting base font
        Font headerFont = new Font(baseFont, 20, Font.BOLD);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);
        Font normalFont = new Font(baseFont, 12);
        Font italicFont = new Font(baseFont, 12, Font.ITALIC);
        Font serviceTitleFont = new Font(baseFont, 16, Font.BOLD, new BaseColor(67, 185, 122));
        Font blueFont = new Font(baseFont, 12, Font.BOLD, new BaseColor(22, 57, 161));
        Font finalFont = new Font(baseFont, 12, Font.BOLD, new BaseColor(16, 29, 178));

        // HEADER - Hotel name and logo
        Paragraph header = new Paragraph("HOÁ ĐƠN KHÁCH SẠN\nHorizon Bliss", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        // Add some space after header
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        // CUSTOMER INFO SECTION - Layout in two columns
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 1}); // Equal width for two columns

        // Format date to match the UI (YYYY-MM-DD)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedIssueDate = dateFormat.format(invoice.getIssueDate());

        // Customer info rows
        addInfoCell(infoTable, "Mã Khách Hàng: " + getCustomerID(), boldFont);
        addInfoCell(infoTable, "Mã Đặt Phòng: " + invoice.getBookingID(), boldFont);
        addInfoCell(infoTable, "Ngày lập: " + formattedIssueDate, boldFont);
        addInfoCell(infoTable, "Mã Dịch Vụ: " + invoice.getServiceBookingID(), boldFont);
        addInfoCell(infoTable, "Số Ngày: " + rDao.getBookingById(invoice.getBookingID()).getNumberOfDays(), boldFont);
        addInfoCell(infoTable, "", boldFont); // Empty cell to align the table

        // Full width customer name
        PdfPCell nameCell = new PdfPCell(new Phrase("Tên Khách Hàng: " + getName(), boldFont));
        nameCell.setColspan(2);
        nameCell.setBorder(Rectangle.NO_BORDER);
        nameCell.setPaddingBottom(10);
        infoTable.addCell(nameCell);

        document.add(infoTable);

        // SERVICE LIST SECTION - With title and styling similar to UI
        Paragraph serviceTitle = new Paragraph("Danh Sách", serviceTitleFont);
        serviceTitle.setSpacingBefore(20);
        serviceTitle.setSpacingAfter(10);
        document.add(serviceTitle);

        // Service table with same columns as UI
        PdfPTable serviceTable = new PdfPTable(3);
        serviceTable.setWidthPercentage(100);
        serviceTable.setWidths(new float[]{65, 15, 20}); // Adjusted ratio to match the UI
        serviceTable.setSpacingBefore(5);

        // Add table header cells with styling
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Tên Dịch Vụ", boldFont));
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Số Lượng", boldFont));
        PdfPCell headerCell3 = new PdfPCell(new Phrase("Giá", boldFont));

        headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell3.setBackgroundColor(BaseColor.LIGHT_GRAY);

        headerCell1.setPadding(5);
        headerCell2.setPadding(5);
        headerCell3.setPadding(5);

        // Align content
        headerCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);

        serviceTable.addCell(headerCell1);
        serviceTable.addCell(headerCell2);
        serviceTable.addCell(headerCell3);

        // Add service data
        ArrayList<Service> services = getServiceList();
        services.add(new Service("Thuê Phòng", giaPhong()));
        for (Service svc : services) {
            PdfPCell nameCellService = new PdfPCell(new Phrase(svc.getName(), normalFont));
            PdfPCell quantityCell = new PdfPCell(new Phrase("1", normalFont));
            PdfPCell priceCell = new PdfPCell(new Phrase(formatCurrency(svc.getPrice()), normalFont));

            nameCellService.setHorizontalAlignment(Element.ALIGN_LEFT);
            quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            serviceTable.addCell(nameCellService);
            serviceTable.addCell(quantityCell);
            serviceTable.addCell(priceCell);
        }

        document.add(serviceTable);

        // FINANCIAL SUMMARY - Layout similar to UI
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{70, 30}); // Adjusted for better alignment

        // Get financial values
        double totalAmount = invoice.getTotalAmount();
        double discountAmount = getGiamGia();
        double taxAmount = invoice.getTax() * 0.01 * totalAmount;
        double finalAmount = totalAmount - discountAmount + taxAmount;

        // Total
        addSummaryRow(summaryTable, "Tổng Bill", formatCurrency(totalAmount), boldFont, blueFont);

        // Tax
        addSummaryRow(summaryTable, "Thuế", "+" + formatCurrency(taxAmount), boldFont, blueFont);

        // Discount
        addSummaryRow(summaryTable, "Giảm giá:", "-" + formatCurrency(discountAmount), boldFont, blueFont);

        // Paid amount
        addSummaryRow(summaryTable, "Tiền đã thanh toán", formatCurrency(totalAmount), boldFont, blueFont);

        // Final amount
        addSummaryRow(summaryTable, "Tổng thanh toán", formatCurrency(finalAmount), boldFont, finalFont);

        document.add(summaryTable);

        // Footer
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ!", italicFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    // Helper method to add info cells
    private void addInfoCell(PdfPTable table, String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(5);
        table.addCell(cell);
    }

    // Helper method to add summary rows with proper alignment
    private void addSummaryRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));

        labelCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBorder(Rectangle.NO_BORDER);

        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        labelCell.setPaddingBottom(5);
        valueCell.setPaddingBottom(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
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