package com.sb.MovieBooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sb.MovieBooking.repository.BookingRepository;
import com.sb.MovieBooking.repository.SeatRepository;
import com.sb.MovieBooking.repository.ShowRepository;

import jakarta.transaction.Transactional;

@Service
public class ShowService {

    @Autowired private ShowRepository showRepo;
    @Autowired private SeatRepository seatRepo;
    @Autowired private BookingRepository bookingRepo;

    // ── DELETE SHOW SERVICE ───────────────────────────────────────────
    // Handles complete deletion of a show and its related data
    @Transactional
    public ResponseEntity<String> deleteShow(Long id) {

        // ── Step 1: Delete all seats linked to the show ───────────────
        // Important: Seats must be deleted first due to foreign key constraints
        seatRepo.deleteByShowId(id);

        // ── Step 2: Delete all bookings for the show ──────────────────
        // Prevents orphan records and maintains DB integrity
        bookingRepo.deleteByShowId(id);

        // ── Step 3: Delete the show itself ────────────────────────────
        // Final step after removing dependencies
        showRepo.deleteById(id);

        // ── Return success response ──
        return ResponseEntity.ok("Show deleted");
    }
}