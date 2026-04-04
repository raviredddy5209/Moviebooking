package com.sb.MovieBooking.service;
import com.sb.MovieBooking.entity.Booking;
import com.sb.MovieBooking.model.BookingStatus;
import com.sb.MovieBooking.entity.AdminBookingDto;
import com.sb.MovieBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsService {

    @Autowired private BookingRepository bookingRepo;
    @Autowired private MovieRepository movieRepo;
    @Autowired private TheaterRepository theaterRepo;
    @Autowired private ShowRepository showRepo;
    @Autowired private UserRepository userRepo;

    // ── DASHBOARD STATS ───────────────────────────────────────────────
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalBookings   = bookingRepo.count();
        long confirmedCount  = bookingRepo.countByStatus(BookingStatus.CONFIRMED);
        long cancelledCount  = bookingRepo.countByStatus(BookingStatus.CANCELLED);
        double totalRevenue  = bookingRepo.sumRevenue();

        stats.put("totalMovies",    movieRepo.count());
        stats.put("totalTheaters",  theaterRepo.count());
        stats.put("totalShows",     showRepo.count());
        stats.put("totalBookings",  totalBookings);
        stats.put("confirmed",      confirmedCount);
        stats.put("cancelled",      cancelledCount);
        stats.put("totalRevenue",   totalRevenue);
        stats.put("totalUsers",     userRepo.count());

        return stats;
    }

    // ── ALL BOOKINGS ──────────────────────────────────────────────────
    public List<AdminBookingDto> getAllBookings() {
        return bookingRepo.findAll()
                .stream()
                .filter(b -> b.getShow() != null)
                .map(this::toDto)
                .sorted(Comparator.comparing(
                        AdminBookingDto::getBookedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toList());
    }

    // ── REVENUE BY MOVIE ──────────────────────────────────────────────
    public List<Map<String, Object>> revenueByMovie() {
        return bookingRepo.findAll()
                .stream()
                .filter(b -> b.getShow() != null && b.getAmountPaid() != null)
                .filter(b ->  
                        b.getStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.groupingBy(
                        b -> b.getShow().getMovie().getTitle(),
                        Collectors.summingDouble(Booking::getAmountPaid)
                ))
                .entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("movie", e.getKey());
                    m.put("revenue", e.getValue());
                    return m;
                })
                .sorted((a, b) ->
                        Double.compare((double) b.get("revenue"), (double) a.get("revenue")))
                .collect(Collectors.toList());
    }

    // ── REVENUE BY THEATER ────────────────────────────────────────────
    public List<Map<String, Object>> revenueByTheater() {
        return bookingRepo.findAll()
                .stream()
                .filter(b -> b.getShow() != null && b.getAmountPaid() != null)
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.groupingBy(
                        b -> b.getShow().getTheater().getName(),
                        Collectors.summingDouble(Booking::getAmountPaid)
                ))
                .entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("theater", e.getKey());
                    m.put("revenue", e.getValue());
                    return m;
                })
                .sorted((a, b) ->
                        Double.compare((double) b.get("revenue"), (double) a.get("revenue")))
                .collect(Collectors.toList());
    }

    // ── DTO MAPPER ────────────────────────────────────────────────────
    private AdminBookingDto toDto(Booking b) {
        AdminBookingDto dto = new AdminBookingDto();

        dto.setBookingId(b.getId());
        dto.setBookingRef(b.getBookingRef());
        dto.setStatus(b.getStatus() != null ? b.getStatus().name() : "UNKNOWN");
        dto.setUserEmail(b.getUser() != null ? b.getUser().getEmail() : "");

        dto.setMovieTitle(b.getShow().getMovie().getTitle());
        dto.setTheaterName(b.getShow().getTheater().getName());
        dto.setTheaterCity(b.getShow().getTheater().getCity());

        dto.setShowTime(b.getShow().getShowTime());
        dto.setSeatNumber(b.getSeatNumber());
        dto.setSection(b.getSection());

        dto.setAmountPaid(b.getAmountPaid() != null ? b.getAmountPaid() : 0.0);
        dto.setBookedAt(b.getBookedAt());

        return dto;
    }
}