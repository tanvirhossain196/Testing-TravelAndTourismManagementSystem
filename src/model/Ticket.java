package model;

import util.DateUtil;
import util.IDGenerator;

public class Ticket {
    private String ticketId;
    private String bookingId;
    private String QRcode;
    private String issueDate;
    private String validFrom;
    private String validUntil;
    private String passengerName;
    private String passengerPhone;
    private String packageName;
    private String seatNumber;
    private boolean isUsed;

    public Ticket(String ticketId, String bookingId, String passengerName, String packageName) {
        this.ticketId = ticketId;
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.packageName = packageName;
        this.QRcode = generateQRCode();
        this.issueDate = DateUtil.getCurrentDate();
        this.isUsed = false;
    }

    private String generateQRCode() {
        return "QR" + IDGenerator.generateRandomString(12);
    }

    public void useTicket() {
        this.isUsed = true;
    }

    public String generateTicketText() {
        StringBuilder ticket = new StringBuilder();
        ticket.append("================== TICKET ==================\n");
        ticket.append("Ticket ID: ").append(ticketId).append("\n");
        ticket.append("Booking ID: ").append(bookingId).append("\n");
        ticket.append("Passenger: ").append(passengerName).append("\n");
        ticket.append("Package: ").append(packageName).append("\n");
        ticket.append("Issue Date: ").append(issueDate).append("\n");
        ticket.append("QR Code: ").append(QRcode).append("\n");
        ticket.append("Status: ").append(isUsed ? "USED" : "VALID").append("\n");
        ticket.append("==============================================\n");
        return ticket.toString();
    }

    // Getters and Setters
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getQRcode() { return QRcode; }
    public void setQRcode(String QRcode) { this.QRcode = QRcode; }
    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
    public String getValidFrom() { return validFrom; }
    public void setValidFrom(String validFrom) { this.validFrom = validFrom; }
    public String getValidUntil() { return validUntil; }
    public void setValidUntil(String validUntil) { this.validUntil = validUntil; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerPhone() { return passengerPhone; }
    public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", passenger='" + passengerName + '\'' +
                ", package='" + packageName + '\'' +
                ", used=" + isUsed +
                '}';
    }
}
