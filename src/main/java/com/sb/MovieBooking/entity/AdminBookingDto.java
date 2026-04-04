package com.sb.MovieBooking.entity;

import java.time.LocalDateTime;

public class AdminBookingDto {

    private Long bookingId;
    private String bookingRef;
    private String status;
    private String userEmail;
    private String movieTitle;
    private String theaterName;
    private String theaterCity;
    private LocalDateTime showTime;
    private String seatNumber;
    private String section;
    private double amountPaid;
    private LocalDateTime bookedAt;

    // ── Getters and Setters ───────────────────────────────────────────
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
    public String getTheaterCity() { return theaterCity; }
    public void setTheaterCity(String theaterCity) { this.theaterCity = theaterCity; }
    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
}