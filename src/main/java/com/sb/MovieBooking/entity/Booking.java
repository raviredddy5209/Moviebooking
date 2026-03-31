package com.sb.MovieBooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.sb.MovieBooking.model.User;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Which user made this booking ──────────────────────────────────
    // Links to users table via user_id foreign key
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    // ── Which show was booked ─────────────────────────────────────────
    // Links to shows table via show_id foreign key
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "show_id")
    private Show show;

    // ── Which seat was booked ─────────────────────────────────────────
    // e.g. "A1", "D5", "G10"
    private String seatNumber;

    // ── Section booked ────────────────────────────────────────────────
    // GOLD, SILVER, or DIAMOND
    private String section;

    // ── Amount paid at time of booking ───────────────────────────────
    // Stored so price changes don't affect old bookings
    // Using Double wrapper not double primitive
    private Double amountPaid;

    // ── When booking was made ─────────────────────────────────────────
    private LocalDateTime bookedAt = LocalDateTime.now();

    // ── Constructors ──────────────────────────────────────────────────
    public Booking() {}

    // ── Getters and Setters ───────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Show getShow() { return show; }
    public void setShow(Show show) { this.show = show; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }

    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
}