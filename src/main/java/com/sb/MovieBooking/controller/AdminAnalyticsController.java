package com.sb.MovieBooking.controller;
 
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sb.MovieBooking.entity.AdminBookingDto;
import com.sb.MovieBooking.service.AdminAnalyticsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/analytics")
public class AdminAnalyticsController {

    @Autowired
    private AdminAnalyticsService analyticsService;

    // ── DASHBOARD STATS ───────────────────────────────────────────────
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return analyticsService.getStats();
    }

    // ── ALL BOOKINGS ──────────────────────────────────────────────────
    @GetMapping("/bookings")
    public List<AdminBookingDto> getAllBookings() {
        return analyticsService.getAllBookings();
    }

    // ── REVENUE BY MOVIE ──────────────────────────────────────────────
    @GetMapping("/revenue-by-movie")
    public List<Map<String, Object>> revenueByMovie() {
        return analyticsService.revenueByMovie();
    }

    // ── REVENUE BY THEATER ────────────────────────────────────────────
    @GetMapping("/revenue-by-theater")
    public List<Map<String, Object>> revenueByTheater() {
        return analyticsService.revenueByTheater();
    }
}
