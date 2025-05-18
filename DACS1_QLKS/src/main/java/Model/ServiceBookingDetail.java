package Model;

public class ServiceBookingDetail {
    private int stt;
    private String serviceBookingID;
    private int serviceID;

    public ServiceBookingDetail() {}

    public ServiceBookingDetail(int stt, String serviceBookingID, int serviceID) {
        this.stt = stt;
        this.serviceBookingID = serviceBookingID;
        this.serviceID = serviceID;
    }

    public ServiceBookingDetail(String serviceBookingID, int serviceID) {
        this.serviceBookingID = serviceBookingID;
        this.serviceID = serviceID;
    }

    // Getters and Setters
    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public String getServiceBookingID() {
        return serviceBookingID;
    }

    public void setServiceBookingID(String serviceBookingID) {
        this.serviceBookingID = serviceBookingID;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }
}
