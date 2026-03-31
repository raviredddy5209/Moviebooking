package com.sb.MovieBooking.repository;

import com.sb.MovieBooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // ── Get all seats for a specific show ─────────────────────────────
    // SQL: SELECT * FROM seats WHERE show_id = ?
    List<Seat> findByShowId(Long showId);

    // ── Get all seats for a show in a specific section ────────────────
    // SQL: SELECT * FROM seats WHERE show_id = ? AND section = ?
    List<Seat> findByShowIdAndSection(Long showId, String section);

    // ── Check if specific seat is already booked ──────────────────────
    // SQL: SELECT * FROM seats WHERE show_id = ? AND seat_number = ?
    Seat findByShowIdAndSeatNumber(Long showId, String seatNumber);
    
    void deleteByShowId(Long showId);
}