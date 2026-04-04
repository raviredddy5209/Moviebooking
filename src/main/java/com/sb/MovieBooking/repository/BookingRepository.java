package com.sb.MovieBooking.repository;

import com.sb.MovieBooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	// ── Count bookings by status ──────────────────────────────────────────
	@Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
	long countByStatus(@Param("status") String status);

	// ── Total revenue from confirmed bookings ─────────────────────────────
	@Query("SELECT COALESCE(SUM(b.amountPaid), 0) FROM Booking b WHERE b.status = 'CONFIRMED'")
	double sumRevenue();
    // ── Get all bookings for a specific user ──────────────────────────
    // SQL: SELECT * FROM bookings WHERE user_id = ?
    // Used in "My Bookings" page
    List<Booking> findByUserId(Long userId);
    void deleteByShowId(Long showId);
    //SELECT * FROM bookings 
   //	 WHERE user_id = ? AND show_id = ?
    List<Booking> findByUserIdAndBookingRef(Long userId, String bookingRef);
    List<Booking> findByUserIdAndShowId(Long userId, Long showId);
    // ── Get all bookings for a specific show ──────────────────────────
    // SQL: SELECT * FROM bookings WHERE show_id = ?
    List<Booking> findByShowId(Long showId);
    
}