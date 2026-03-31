package com.sb.MovieBooking.repository;

import com.sb.MovieBooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ── Get all bookings for a specific user ──────────────────────────
    // SQL: SELECT * FROM bookings WHERE user_id = ?
    // Used in "My Bookings" page
    List<Booking> findByUserId(Long userId);
    void deleteByShowId(Long showId);

    // ── Get all bookings for a specific show ──────────────────────────
    // SQL: SELECT * FROM bookings WHERE show_id = ?
    List<Booking> findByShowId(Long showId);
}