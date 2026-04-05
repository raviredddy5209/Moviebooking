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
    
 // ── REVENUE BY SHOW ───────────────────────────────────────────────────
 // Groups confirmed bookings by show
 // Shows: Interstellar 10AM at PVR → ₹5000
 public List<Map<String, Object>> revenueByShow() {
     return bookingRepo.findAll()
         .stream()
         .filter(b -> b.getShow() != null && b.getAmountPaid() != null)
         .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
         .collect(Collectors.groupingBy(
             b -> b.getShow().getId(),
             Collectors.toList()
         ))
         .entrySet().stream()
         .map(e -> {
             List<Booking> group = e.getValue();
             Booking first = group.get(0);
             double revenue = group.stream()
                 .mapToDouble(Booking::getAmountPaid).sum();

             Map<String, Object> m = new HashMap<>();
             m.put("showId",      first.getShow().getId());
             m.put("movie",       first.getShow().getMovie().getTitle());
             m.put("theater",     first.getShow().getTheater().getName());
             m.put("showTime",    first.getShow().getShowTime());
             m.put("revenue",     revenue);
             m.put("seatsSold",   group.size());
             return m;
         })
         .sorted((a, b) ->
             Double.compare((double)b.get("revenue"), (double)a.get("revenue")))
         .collect(Collectors.toList());
 }

 // ── BOOKINGS BY DATE ──────────────────────────────────────────────────
 // Groups bookings by date booked (not show date)
 // Shows daily booking trend: Apr 1 → 12 bookings, Apr 2 → 8 bookings
 public List<Map<String, Object>> bookingsByDate() {
     return bookingRepo.findAll()
         .stream()
         .filter(b -> b.getBookedAt() != null)
         .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
         .collect(Collectors.groupingBy(
             b -> b.getBookedAt().toLocalDate().toString(),
             Collectors.toList()
         ))
         .entrySet().stream()
         .map(e -> {
             List<Booking> group = e.getValue();
             double revenue = group.stream()
                 .mapToDouble(Booking::getAmountPaid).sum();

             Map<String, Object> m = new HashMap<>();
             m.put("date",      e.getKey());
             m.put("bookings",  group.size());
             m.put("revenue",   revenue);
             return m;
         })
         .sorted(Comparator.comparing(m -> m.get("date").toString()))
         .collect(Collectors.toList());
 }

 // ── TOP MOVIES BY BOOKINGS ────────────────────────────────────────────
 // Which movie has most seats booked (not revenue, just count)
 public List<Map<String, Object>> topMovies() {
     return bookingRepo.findAll()
         .stream()
         .filter(b -> b.getShow() != null)
         .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
         .collect(Collectors.groupingBy(
             b -> b.getShow().getMovie().getTitle(),
             Collectors.counting()
         ))
         .entrySet().stream()
         .map(e -> {
             Map<String, Object> m = new HashMap<>();
             m.put("movie",    e.getKey());
             m.put("bookings", e.getValue());
             return m;
         })
         .sorted((a, b) ->
             Long.compare((long)b.get("bookings"), (long)a.get("bookings")))
         .collect(Collectors.toList());
 }

 // ── SECTION BREAKDOWN ─────────────────────────────────────────────────
 // How many Diamond / Gold / Silver seats sold with revenue
 public List<Map<String, Object>> sectionBreakdown() {
     return bookingRepo.findAll()
         .stream()
         .filter(b -> b.getSection() != null && b.getAmountPaid() != null)
         .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
         .collect(Collectors.groupingBy(Booking::getSection))
         .entrySet().stream()
         .map(e -> {
             List<Booking> group = e.getValue();
             double revenue = group.stream()
                 .mapToDouble(Booking::getAmountPaid).sum();

             Map<String, Object> m = new HashMap<>();
             m.put("section",  e.getKey());
             m.put("count",    group.size());
             m.put("revenue",  revenue);
             return m;
         })
         .collect(Collectors.toList());
 }

 // ── OCCUPANCY PER SHOW ────────────────────────────────────────────────
 // What % of seats are filled for each show
 // Uses total seats from theater to calculate %
 public List<Map<String, Object>> occupancy() {
     // Get confirmed bookings per show
     Map<Long, Long> bookedPerShow = bookingRepo.findAll()
         .stream()
         .filter(b -> b.getShow() != null)
         .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
         .collect(Collectors.groupingBy(
             b -> b.getShow().getId(),
             Collectors.counting()
         ));

     // Get all shows from DB
     return showRepo.findAll()
         .stream()
         .map(s -> {
             long booked = bookedPerShow.getOrDefault(s.getId(), 0L);
             int totalSeats = s.getTheater().getTotalSeats();
             double occupancyPct = totalSeats > 0
                 ? (booked * 100.0 / totalSeats)
                 : 0;

             Map<String, Object> m = new HashMap<>();
             m.put("showId",       s.getId());
             m.put("movie",        s.getMovie().getTitle());
             m.put("theater",      s.getTheater().getName());
             m.put("showTime",     s.getShowTime());
             m.put("booked",       booked);
             m.put("totalSeats",   totalSeats);
             m.put("occupancyPct", Math.round(occupancyPct));
             return m;
         })
         .sorted((Map<String, Object> a, Map<String, Object> b) ->
         Long.compare(
             (Long) b.get("occupancyPct"),
             (Long) a.get("occupancyPct")
         )
     )
     .collect(Collectors.toList());
}
}