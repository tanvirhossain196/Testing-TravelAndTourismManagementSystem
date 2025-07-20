package model;

import enumtype.BookingStatus;
import util.DateUtil;

public class Booking {
    private String bookingId;
    private String userId;
    private String packageId;
    private String bookingDate;
    private String travelDate;
    private BookingStatus status;
    private int numberOfPeople;
    private double totalAmount;
    private String specialRequests;
    private String hotelId;
    private String transportId;
    private String guideId;
    private boolean isPaid;

    public Booking(String bookingId, String userId, String packageId, String travelDate, int numberOfPeople) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.packageId = packageId;
        this.travelDate = travelDate;
        this.numberOfPeople = numberOfPeople;
        this.bookingDate = DateUtil.getCurrentDate();
        this.status = BookingStatus.PENDING;
        this.isPaid = false;
    }

    public void confirmBooking() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancelBooking() {
        this.status = BookingStatus.CANCELED;
    }

    public void completeBooking() {
        this.status = BookingStatus.COMPLETED;
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public int getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(int numberOfPeople) { this.numberOfPeople = numberOfPeople; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    public String getTransportId() { return transportId; }
    public void setTransportId(String transportId) { this.transportId = transportId; }
    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", packageId='" + packageId + '\'' +
                ", travelDate='" + travelDate + '\'' +
                ", people=" + numberOfPeople +
                ", status=" + status +
                ", amount=" + totalAmount +
                '}';
    }
}
