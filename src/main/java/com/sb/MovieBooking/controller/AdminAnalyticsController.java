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

	// ── REVENUE BY SHOW ───────────────────────────────────────────────────
	// Which specific show time made the most money
	@GetMapping("/revenue-by-show")
	public List<Map<String, Object>> revenueByShow() {
		return analyticsService.revenueByShow();
	}

	// ── BOOKINGS BY DATE ──────────────────────────────────────────────────
	// How many bookings happened on each date
	@GetMapping("/bookings-by-date")
	public List<Map<String, Object>> bookingsByDate() {
		return analyticsService.bookingsByDate();
	}

	// ── TOP MOVIES BY BOOKINGS ────────────────────────────────────────────
	// Which movie has most seats booked
	@GetMapping("/top-movies")
	public List<Map<String, Object>> topMovies() {
		return analyticsService.topMovies();
	}

	// ── SECTION BREAKDOWN ─────────────────────────────────────────────────
	// How many Diamond / Gold / Silver seats sold
	@GetMapping("/section-breakdown")
	public List<Map<String, Object>> sectionBreakdown() {
		return analyticsService.sectionBreakdown();
	}

	// ── OCCUPANCY PER SHOW ────────────────────────────────────────────────
	// What % of seats are filled for each show
	@GetMapping("/occupancy")
	public List<Map<String, Object>> occupancy() {
		return analyticsService.occupancy();
	}







}
