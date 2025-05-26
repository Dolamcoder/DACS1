package Controller.Invoice;

import Dao.Employee.*;
import Model.Invoice;
import Model.Service;
import Model.Booking;
import Model.CardLevel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class InvoiceExporter {
    private final RoomBookingDao roomBookingDao;
    private final CustomerDao customerDao;
    private final RoomDao roomDao;
    private final CardLevelDao cardLevelDao;
    private final NumberFormat currencyFormat;

    public InvoiceExporter() {
        roomBookingDao = new RoomBookingDao();
        customerDao = new CustomerDao();
        roomDao = new RoomDao();
        cardLevelDao = new CardLevelDao();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    public String exportInvoice(Invoice invoice) throws IOException, DocumentException {
        String filePath = "HoaDon_" + invoice.getInvoiceID() + ".pdf";
        String customerID = getCustomerID(invoice);
        String customerName = getCustomerName(customerID);
        exportInvoiceToPdf(filePath, invoice, customerID, customerName);
        return filePath;
    }

    public void exportInvoiceToPdf(String filePath, Invoice invoice) throws IOException, DocumentException {
        String customerID = getCustomerID(invoice);
        String customerName = getCustomerName(customerID);
        exportInvoiceToPdf(filePath, invoice, customerID, customerName);
    }

    private void exportInvoiceToPdf(String filePath, Invoice invoice, String customerID, String customerName) throws IOException, DocumentException {
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

        // Get booking to access number of days
        Booking booking = roomBookingDao.getBookingById(invoice.getBookingID());
        int numberOfDays = booking != null ? booking.getNumberOfDays() : 0;

        // Customer info rows
        addInfoCell(infoTable, "Mã Khách Hàng: " + customerID, boldFont);
        addInfoCell(infoTable, "Mã Đặt Phòng: " + invoice.getBookingID(), boldFont);
        addInfoCell(infoTable, "Ngày lập: " + formattedIssueDate, boldFont);
        addInfoCell(infoTable, "Mã Dịch Vụ: " + invoice.getServiceBookingID(), boldFont);
        addInfoCell(infoTable, "Số Ngày: " + numberOfDays, boldFont);
        addInfoCell(infoTable, "", boldFont); // Empty cell to align the table

        // Full width customer name
        PdfPCell nameCell = new PdfPCell(new Phrase("Tên Khách Hàng: " + customerName, boldFont));
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
        ArrayList<Service> services = getServiceList(invoice);
        double roomPrice = getRoomPrice(invoice);
        services.add(new Service("Thuê Phòng", roomPrice));

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
        double discountAmount = getDiscountAmount(invoice, customerID);
        double taxAmount = invoice.getTax() * 0.01 * totalAmount;
        double prepayment = getPrepaymentAmount(invoice);
        double finalAmount = totalAmount - discountAmount + taxAmount - prepayment;

        // Total
        addSummaryRow(summaryTable, "Tổng Bill", formatCurrency(totalAmount), boldFont, blueFont);

        // Tax
        addSummaryRow(summaryTable, "Thuế", "+" + formatCurrency(taxAmount), boldFont, blueFont);

        // Discount
        addSummaryRow(summaryTable, "Giảm giá:", "-" + formatCurrency(discountAmount), boldFont, blueFont);

        // Paid amount
        addSummaryRow(summaryTable, "Tiền đã thanh toán", "-" + formatCurrency(prepayment), boldFont, blueFont);

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

    // Helper methods from original controller
    private String getCustomerID(Invoice invoice) {
        if (invoice == null || invoice.getBookingID() == null) {
            return "";
        }
        return roomBookingDao.getCustomerID(invoice.getBookingID());
    }

    private String getCustomerName(String customerID) {
        if (customerID == null || customerID.isEmpty()) {
            return "Không tìm thấy khách hàng";
        }
        return customerDao.getNameById(customerID);
    }

    private double getDiscountAmount(Invoice invoice, String customerID) {
        if (customerID == null || customerID.isEmpty()) {
            return 0.0;
        }

        CardLevel card = cardLevelDao.selectByCustomerId(customerID);
        if (card == null) {
            return 0.0;
        }

        double discountPercentage = card.getDiscountPercentage();
        double totalAmount = invoice.getTotalAmount();

        return discountPercentage * 0.01 * totalAmount;
    }

    private double getRoomPrice(Invoice invoice) {
        Booking booking = roomBookingDao.getBookingById(invoice.getBookingID());
        if (booking == null) return 0.0;
        return roomDao.getPriceById(booking.getRoomId());
    }

    private double getPrepaymentAmount(Invoice invoice) {
        Booking booking = roomBookingDao.getBookingById(invoice.getBookingID());
        if (booking == null) return 0.0;
        return booking.calculateTotalRoomCost(getRoomPrice(invoice)) * 0.5;
    }

    private ArrayList<Service> getServiceList(Invoice invoice) {
        ServiceBookingDetailDao serviceBookingDetailDao = new ServiceBookingDetailDao();
        return serviceBookingDetailDao.getServicesByBookingId(invoice.getServiceBookingID());
    }

    // Helper method to format currency values
    private String formatCurrency(double amount) {
        return currencyFormat.format(amount).replace("₫", " VND");
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
}