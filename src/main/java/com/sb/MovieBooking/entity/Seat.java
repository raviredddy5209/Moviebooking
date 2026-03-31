package com.sb.MovieBooking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Which show this seat belongs to ───────────────────────────────
    // Each show has its own fresh set of seats
    // Same seat number can exist in multiple shows
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "show_id")
    private Show show;

    // ── Seat number e.g. A1, B5, C10 ─────────────────────────────────
    // Row letter + seat number within that row
    private String seatNumber;

    // ── Section determines price ──────────────────────────────────────
    // GOLD, SILVER, DIAMOND
    private String section;

    // ── Whether this seat is already booked ───────────────────────────
    // false = available, true = booked
    // Using Boolean wrapper not boolean primitive (avoids null crash)
    private Boolean isBooked = false;

    // ── Constructors ──────────────────────────────────────────────────
    public Seat() {}

    public Seat(Show show, String seatNumber, String section) {
        this.show = show;
        this.seatNumber = seatNumber;
        this.section = section;
        this.isBooked = false;
    }

    // ── Getters and Setters ───────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Show getShow() { return show; }
    public void setShow(Show show) { this.show = show; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Boolean getIsBooked() { return isBooked; }
    public void setIsBooked(Boolean isBooked) { this.isBooked = isBooked; }
}