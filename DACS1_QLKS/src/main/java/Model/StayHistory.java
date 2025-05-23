package Model;

import java.sql.Date;
import java.time.LocalDate;

public class StayHistory {
    private int stt;
    private String customerID;
    private String roomID;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String note;

    public StayHistory(int stt, String customerID, String roomID, LocalDate checkIn, LocalDate checkOut, String note) {
        this.stt = stt;
        this.customerID = customerID;
        this.roomID = roomID;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.note = note;
    }
    public StayHistory(){

    }
    public int getStt() { return stt; }
    public void setStt(int stt) { this.stt = stt; }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public String getRoomID() { return roomID; }
    public void setRoomID(String roomID) { this.roomID = roomID; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
