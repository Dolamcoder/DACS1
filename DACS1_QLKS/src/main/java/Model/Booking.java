package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Booking {
    private String bookingId;
    private String customerId;
    private String roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private LocalDateTime createdAt;
    public Booking(String bookingId, String customerId, String roomId,
                   LocalDate checkInDate, LocalDate checkOutDate, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    // Getter và Setter
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public double calculateTotalRoomCost(double roomPrice) {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }

        long numberOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return roomPrice * (numberOfDays > 0 ? numberOfDays : 1); // Ensure at least 1 day
    }
    public int getNumberOfDays(){
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
}

