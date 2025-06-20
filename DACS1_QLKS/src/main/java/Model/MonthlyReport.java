package Model;
import java.text.NumberFormat;
import java.util.Locale;

public class MonthlyReport {
    private final String month;
    private final double roomRevenue;
    private final double serviceRevenue;
    private final double totalRevenue;
    private final String growth;

    public MonthlyReport(String month, double roomRevenue, double serviceRevenue, double totalRevenue, String growth) {
        this.month = month;
        this.roomRevenue = roomRevenue;
        this.serviceRevenue = serviceRevenue;
        this.totalRevenue = totalRevenue;
        this.growth = growth;
    }

    public String getMonth() { return month; }
    public double getRoomRevenue() { return roomRevenue; }
    public double getServiceRevenue() { return serviceRevenue; }
    public double getTotalRevenue() { return totalRevenue; }
    public String getGrowth() { return growth; }

    public String getFormattedRoomRevenue() {
        return formatCurrency(roomRevenue);
    }

    public String getFormattedServiceRevenue() {
        return formatCurrency(serviceRevenue);
    }

    public String getFormattedTotalRevenue() {
        return formatCurrency(totalRevenue);
    }

    private String formatCurrency(double value) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(value);
    }
}