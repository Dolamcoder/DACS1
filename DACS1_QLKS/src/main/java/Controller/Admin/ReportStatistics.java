package Controller.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReportStatistics implements Initializable {

    @FXML private BarChart<String, Number> revenueChart;
    @FXML private CategoryAxis monthAxis;
    @FXML private NumberAxis revenueAxis;
    @FXML private TableView<MonthlyRevenue> summaryTable;
    @FXML private TableColumn<MonthlyRevenue, String> monthColumn;
    @FXML private TableColumn<MonthlyRevenue, String> roomRevColumn;
    @FXML private TableColumn<MonthlyRevenue, String> serviceRevColumn;
    @FXML private TableColumn<MonthlyRevenue, String> totalRevColumn;
    @FXML private TableColumn<MonthlyRevenue, String> growthColumn;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> reportTypeCombo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up date pickers with default values (last 6 months)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(5);
        startDatePicker.setValue(startDate);
        endDatePicker.setValue(endDate);

        // Set up report type combo box
        ObservableList<String> reportTypes = FXCollections.observableArrayList(
                "Doanh thu phòng và dịch vụ",
                "Tỷ lệ lấp đầy phòng",
                "Hiệu suất nhân viên",
                "Chi tiêu theo khu vực"
        );
        reportTypeCombo.setItems(reportTypes);
        reportTypeCombo.getSelectionModel().selectFirst();

        // Set up table columns
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        roomRevColumn.setCellValueFactory(new PropertyValueFactory<>("roomRevenue"));
        serviceRevColumn.setCellValueFactory(new PropertyValueFactory<>("serviceRevenue"));
        totalRevColumn.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        growthColumn.setCellValueFactory(new PropertyValueFactory<>("growth"));

        // Load sample data
        loadSampleData();
    }

    private void loadSampleData() {
        // Create sample data for chart
        XYChart.Series<String, Number> roomSeries = new XYChart.Series<>();
        roomSeries.setName("Doanh thu phòng");

        XYChart.Series<String, Number> serviceSeries = new XYChart.Series<>();
        serviceSeries.setName("Doanh thu dịch vụ");

        // Sample data for 3 months
        roomSeries.getData().add(new XYChart.Data<>("Tháng 1", 245000000));
        roomSeries.getData().add(new XYChart.Data<>("Tháng 2", 278000000));
        roomSeries.getData().add(new XYChart.Data<>("Tháng 3", 310000000));

        serviceSeries.getData().add(new XYChart.Data<>("Tháng 1", 87000000));
        serviceSeries.getData().add(new XYChart.Data<>("Tháng 2", 102000000));
        serviceSeries.getData().add(new XYChart.Data<>("Tháng 3", 132000000));

        // Add data to chart
        revenueChart.getData().addAll(roomSeries, serviceSeries);

        // Style the bars after they've been rendered
        javafx.application.Platform.runLater(() -> {
            // Style each data point in the series
            for (XYChart.Data<String, Number> data : roomSeries.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #3498db;"); // Blue for rooms
                }
            }

            for (XYChart.Data<String, Number> data : serviceSeries.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #2ecc71;"); // Green for services
                }
            }
        });

        // Populate table with matching data
        DecimalFormat formatter = new DecimalFormat("#,###");
        ObservableList<MonthlyRevenue> data = FXCollections.observableArrayList(
                new MonthlyRevenue(
                        "Tháng 1",
                        formatter.format(245000000) + " ₫",
                        formatter.format(87000000) + " ₫",
                        formatter.format(332000000) + " ₫",
                        "-"
                ),
                new MonthlyRevenue(
                        "Tháng 2",
                        formatter.format(278000000) + " ₫",
                        formatter.format(102000000) + " ₫",
                        formatter.format(380000000) + " ₫",
                        "+14.5%"
                ),
                new MonthlyRevenue(
                        "Tháng 3",
                        formatter.format(310000000) + " ₫",
                        formatter.format(132000000) + " ₫",
                        formatter.format(442000000) + " ₫",
                        "+16.3%"
                )
        );

        summaryTable.setItems(data);
    }

    @FXML
    private void generateReport() {
        // In a real application, this would query the database based on date range
        // For now, just show a confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Báo cáo đã được tạo");
        alert.setHeaderText(null);
        alert.setContentText("Báo cáo từ " +
                startDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " đến " +
                endDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " đã được tạo thành công!");
        alert.showAndWait();
    }

    @FXML
    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("BaoCaoDoanhThu.pdf");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            // Implementation for PDF export would go here
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Xuất PDF");
            alert.setHeaderText(null);
            alert.setContentText("Đã xuất báo cáo thành công đến: " + file.getPath());
            alert.showAndWait();
        }
    }

    @FXML
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("BaoCaoDoanhThu.xlsx");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            // Implementation for Excel export would go here
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Xuất Excel");
            alert.setHeaderText(null);
            alert.setContentText("Đã xuất báo cáo thành công đến: " + file.getPath());
            alert.showAndWait();
        }
    }

    @FXML
    private void printReport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("In báo cáo");
        alert.setHeaderText(null);
        alert.setContentText("Đang gửi báo cáo đến máy in...");
        alert.showAndWait();
    }

    // Model class for table data
    public static class MonthlyRevenue {
        private final String month;
        private final String roomRevenue;
        private final String serviceRevenue;
        private final String totalRevenue;
        private final String growth;

        public MonthlyRevenue(String month, String roomRevenue, String serviceRevenue,
                              String totalRevenue, String growth) {
            this.month = month;
            this.roomRevenue = roomRevenue;
            this.serviceRevenue = serviceRevenue;
            this.totalRevenue = totalRevenue;
            this.growth = growth;
        }

        public String getMonth() { return month; }
        public String getRoomRevenue() { return roomRevenue; }
        public String getServiceRevenue() { return serviceRevenue; }
        public String getTotalRevenue() { return totalRevenue; }
        public String getGrowth() { return growth; }
    }
}