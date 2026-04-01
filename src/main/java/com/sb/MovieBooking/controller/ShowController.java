package com.sb.MovieBooking.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sb.MovieBooking.repository.BookingRepository;
import com.sb.MovieBooking.repository.SeatRepository;
import com.sb.MovieBooking.repository.ShowRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/admin/shows")
public class ShowController {
    @Autowired private ShowRepository showRepo;
    @Autowired private SeatRepository seatRepo;
    @Autowired private BookingRepository bookingRepo;

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteShow(@PathVariable Long id) {
        seatRepo.deleteByShowId(id);
        bookingRepo.deleteByShowId(id);
        showRepo.deleteById(id);
        return ResponseEntity.ok("Show deleted");
    }
}