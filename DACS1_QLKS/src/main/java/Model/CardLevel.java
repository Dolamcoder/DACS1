package Model;

public class CardLevel {
    private int stt;
    private String customerID;
    private String levelName;
    private double totalAmount;
    private String description;

    // Constructors
    public CardLevel() {}

    public CardLevel(int stt, String customerID, String levelName, double totalAmount, String description) {
        this.stt = stt;
        this.customerID = customerID;
        this.levelName = levelName;
        this.totalAmount = totalAmount;
        this.description = description;
    }

    // Getters and Setters
    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Method to get discount percentage based on level name
    public double getDiscountPercentage() {
        if (levelName == null) return 0;

        switch (levelName.toLowerCase()) {
            case "bronze":
                return 5.0;
            case "silver":
                return 10.0;
            case "gold":
                return 15.0;
            case "platinum":
                return 20.0;
            case "diamond":
                return 25.0;
            default:
                return 0.0;
        }
    }
    public static String determineLevelName(double totalAmount) {
        if (totalAmount >= 20000000) {
            return "Diamond";
        } else if (totalAmount >= 15000000) {
            return "Platinum";
        } else if (totalAmount >= 10000000) {
            return "Gold";
        } else if (totalAmount >= 5000000) {
            return "Silver";
        } else {
            return "Bronze";
        }
    }
    public static String getLevelDescription(String levelName) {
        switch (levelName) {
            case "Diamond":
                return "Khách hàng VIP - Giảm 25% cho đặt phòng và dịch vụ";
            case "Platinum":
                return "Khách hàng thân thiết cao cấp - Giảm 20% cho đặt phòng và dịch vụ";
            case "Gold":
                return "Khách hàng thân thiết - Giảm 15% cho đặt phòng và dịch vụ";
            case "Silver":
                return "Khách hàng thường xuyên - Giảm 10% cho đặt phòng và dịch vụ";
            case "Bronze":
            default:
                return "Khách hàng mới - Giảm 5% cho đặt phòng và dịch vụ";
        }
    }
    public void updateLevelBasedOnAmount() {
        String newLevelName = determineLevelName(this.totalAmount);
        this.levelName = newLevelName;
        this.description = getLevelDescription(newLevelName);
    }
}