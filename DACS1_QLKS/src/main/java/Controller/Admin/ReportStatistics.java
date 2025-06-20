package Controller.Admin;

import Alert.Alert;
import Dao.Employee.InvoiceDao;
import Dao.Employee.RoomBookingDao;
import Dao.Employee.RoomDao;
import Dao.Employee.ServiceBookingDao;
import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

public class ReportStatistics {
    @FXML private DatePicker endDatePicker;
    @FXML private Button exportExcelBtn;
    @FXML private Button generateReportBtn;
    @FXML private TableColumn<MonthlyReport, String> growthColumn;
    @FXML private CategoryAxis monthAxis;
    @FXML private TableColumn<MonthlyReport, String> monthColumn;
    @FXML private NumberAxis revenueAxis;
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private TableColumn<MonthlyReport, String> roomRevColumn;
    @FXML private TableColumn<MonthlyReport, String> serviceRevColumn;
    @FXML private DatePicker startDatePicker;
    @FXML private TableView<MonthlyReport> summaryTable;
    @FXML private TableColumn<MonthlyReport, String> totalRevColumn;

    InvoiceDao invoiceDao = new InvoiceDao();
    RoomBookingDao bookDao = new RoomBookingDao();
    RoomDao roomDao = new RoomDao();
    ServiceBookingDao serviceBookingDao = new ServiceBookingDao();
    Alert alert = new Alert();
    @FXML
    void generateReport(ActionEvent event) {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) return;

        LocalDate start = startDatePicker.getValue().withDayOfMonth(1);
        LocalDate end = endDatePicker.getValue().withDayOfMonth(1);

        Map<String, Double> roomRevenueMap = getRoomRevenueByMonth();
        Map<String, Double> serviceRevenueMap = getServiceRevenueByMonth();

        ObservableList<MonthlyReport> reportList = FXCollections.observableArrayList();
        ObservableList<String> monthCategories = FXCollections.observableArrayList();

        XYChart.Series<String, Number> roomSeries = new XYChart.Series<>();
        roomSeries.setName("Doanh thu phòng");

        XYChart.Series<String, Number> serviceSeries = new XYChart.Series<>();
        serviceSeries.setName("Doanh thu dịch vụ");

        LocalDate current = start;
        String prevMonth = null;
        double prevTotal = 0.0;

        while (!current.isAfter(end)) {
            String monthKey = String.format("%02d-%d", current.getMonthValue(), current.getYear());
            double roomRev = roomRevenueMap.getOrDefault(monthKey, 0.0);
            double serviceRev = serviceRevenueMap.getOrDefault(monthKey, 0.0);
            double totalRev = roomRev + serviceRev;

            String growth = "-";
            if (prevMonth != null && prevTotal > 0) {
                double percent = ((totalRev - prevTotal) / prevTotal) * 100;
                growth = String.format("%.2f%%", percent);
            }

            reportList.add(new MonthlyReport(monthKey, roomRev, serviceRev, totalRev, growth));
            monthCategories.add(monthKey);

            roomSeries.getData().add(new XYChart.Data<>(monthKey, roomRev));
            serviceSeries.getData().add(new XYChart.Data<>(monthKey, serviceRev));

            prevMonth = monthKey;
            prevTotal = totalRev;

            current = current.plusMonths(1);
        }

        monthAxis.setCategories(monthCategories);
        monthAxis.setTickLabelRotation(0);
        monthAxis.setTickLabelGap(10);

        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        roomRevColumn.setCellValueFactory(new PropertyValueFactory<>("formattedRoomRevenue"));
        serviceRevColumn.setCellValueFactory(new PropertyValueFactory<>("formattedServiceRevenue"));
        totalRevColumn.setCellValueFactory(new PropertyValueFactory<>("formattedTotalRevenue"));
        growthColumn.setCellValueFactory(new PropertyValueFactory<>("growth"));

        summaryTable.setItems(reportList);
        revenueChart.getData().clear();
        revenueChart.getData().addAll(roomSeries, serviceSeries);
    }

    @FXML
    void exportToExcel(ActionEvent event) {
        ObservableList<MonthlyReport> data = summaryTable.getItems();
        if (data.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo doanh thu");
        fileChooser.setInitialFileName("BaoCaoDoanhThu.xlsx");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(exportExcelBtn.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Báo cáo");

                Row header = sheet.createRow(0);
                String[] titles = {"Tháng", "Doanh thu phòng (VND)", "Doanh thu dịch vụ (VND)", "Tổng doanh thu (VND)", "Tăng trưởng (%)"};
                for (int i = 0; i < titles.length; i++) {
                    header.createCell(i).setCellValue(titles[i]);
                }

                int rowIdx = 1;
                for (MonthlyReport item : data) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(item.getMonth());
                    row.createCell(1).setCellValue(item.getRoomRevenue());
                    row.createCell(2).setCellValue(item.getServiceRevenue());
                    row.createCell(3).setCellValue(item.getTotalRevenue());
                    row.createCell(4).setCellValue(item.getGrowth());
                }

                for (int i = 0; i < titles.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                alert.showInfoAlert("Xuất báo cáo thành công");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Invoice> getListInvoice() {
        ArrayList<Invoice> filtered = new ArrayList<>();
        ArrayList<Invoice> all = invoiceDao.findAll();

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) return filtered;

        Date start = Date.valueOf(startDatePicker.getValue());
        Date end = Date.valueOf(endDatePicker.getValue());

        for (Invoice invoice : all) {
            Date issue = invoice.getIssueDate();
            if (!issue.before(start) && !issue.after(end)) {
                filtered.add(invoice);
            }
        }
        return filtered;
    }

    public double calculateBookingTotal(String bookingID) {
        Booking book = bookDao.getBookingById(bookingID);
        if (book == null) return 0.0;
        double price = roomDao.getPriceById(book.getRoomId());
        return book.calculateTotalRoomCost(price);
    }

    public double calculateServiceTotal(String serviceBookingID) {
        if (serviceBookingID == null) return 0.0;
        ServiceBooking sb = serviceBookingDao.getById(serviceBookingID);
        return sb == null ? 0.0 : sb.getTotalAmount();
    }

    public Map<String, Double> getRoomRevenueByMonth() {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Invoice invoice : getListInvoice()) {
            String monthKey = getMonthKey(invoice.getIssueDate());
            double amount = calculateBookingTotal(invoice.getBookingID());
            result.put(monthKey, result.getOrDefault(monthKey, 0.0) + amount);
        }
        return result;
    }

    public Map<String, Double> getServiceRevenueByMonth() {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Invoice invoice : getListInvoice()) {
            String monthKey = getMonthKey(invoice.getIssueDate());
            double amount = calculateServiceTotal(invoice.getServiceBookingID());
            result.put(monthKey, result.getOrDefault(monthKey, 0.0) + amount);
        }
        return result;
    }

    private String getMonthKey(Date date) {
        LocalDate localDate = date.toLocalDate();
        return String.format("%02d-%d", localDate.getMonthValue(), localDate.getYear());
    }

}