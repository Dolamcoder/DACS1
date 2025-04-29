package Model;

public class Service {
    private String serviceId;
    private String name;
    private String customerID;
    private double price;
    private String description;
    // Constructor
    public Service(String serviceId, String customerID, String name, double price, String description) {
        this.serviceId = serviceId;
        this.customerID=customerID;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // Getter và Setter
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
