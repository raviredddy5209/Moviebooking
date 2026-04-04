package com.sb.MovieBooking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sb.MovieBooking.entity.Booking;
import com.sb.MovieBooking.model.BookingDto;
import com.sb.MovieBooking.model.BookingStatus;
import com.sb.MovieBooking.model.User;
import com.sb.MovieBooking.repository.BookingRepository;
import com.sb.MovieBooking.repository.UserRepository;

@Service
public class ShowBookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Get bookings using logged-in user's email ─────────────────────
    // This is typically called from controller using Authentication
    public List<BookingDto> getBookingsByEmail(String email) {

        // ── Fetch user from DB ──
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ── Delegate to common method ──
        return getBookingsByUser(user.getId());
    }

    public List<BookingDto> getBookingsByUser(Long userId) {

        // Fetch all booking rows for user
        //[A1 row, A2 row, B1 row, B2 row...]
        //new Update your booking fetch to only show CONFIRMED bookings:

        // ── Step 1: Fetch and filter bookings ─────────────────────────
        List<Booking> bookings = bookingRepository.findByUserId(userId)
                .stream()
                .filter(b -> b.getShow() != null)                 // avoid null show (safety)
                .filter(b -> b.getShow().getId() != null)         // avoid corrupted data
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)  // ← only confirmed
                .toList();

        // ── Group by bookingRef ───────────────────────────────────────
        // bookingRef is a UUID generated once per booking session
        // All seats booked together share the same bookingRef
        // This gives us ONE card per booking session

        Map<String, List<Booking>> grouped =
                bookings.stream()
                        .collect(Collectors.groupingBy(b -> {

                            // ── Fallback: if bookingRef is null (old data)
                            // group by showId + bookedAt minute
                            // so old bookings still show correctly
                            if (b.getBookingRef() != null) {
                                return b.getBookingRef();
                            }

                            // old rows without bookingRef → each row is its own card
                            return "legacy_" + b.getId();
                        }));

        /* GROUPING
         * A1 (show 1)
           A2 (show 1)
           B1 (show 2)
           to
           showId 1 → [A1, A2]
           showId 2 → [B1]
         */

        /*   Map<Long, List<Booking>> grouped =
                bookings.stream()
                        .collect(Collectors.groupingBy(b -> b.getShow().getId())); */

        // CONVERT EACH GROUP → DTO

        List<BookingDto> result = new ArrayList<>();

        // Each loop = one booking card
        for (Map.Entry<String, List<Booking>> entry : grouped.entrySet()) {

            // ── Extract group (all seats in same booking session) ──
            List<Booking> group = entry.getValue();

            // ── Take first booking as reference ──
            Booking first = group.get(0);

            /* Why first?

            Because:
            All rows in group have same show/movie/theater
            So we take first row as reference
            */

            BookingDto dto = new BookingDto();

            // ── Basic booking info ───────────────────────────────────
            dto.setId(first.getId());
            dto.setShowId(first.getShow().getId());
            dto.setMovieTitle(first.getShow().getMovie().getTitle());
            dto.setMovieId(first.getShow().getMovie().getId());

            // ── Image — try URL first then file path ──────────────────
            String imageUrl = first.getShow().getMovie().getImageUrl();
            String imagePath = first.getShow().getMovie().getImagePath();

            // ── Prefer cloud URL, fallback to local path ──
            dto.setImageUrl(imageUrl != null ? imageUrl : imagePath);

            // dto.setImageUrl(first.getShow().getMovie().getImageUrl());

            // ── Show + theater details ───────────────────────────────
            dto.setTheaterName(first.getShow().getTheater().getName());
            dto.setShowTime(first.getShow().getShowTime());
            dto.setBookedAt(first.getBookedAt());
            dto.setBookingRef(first.getBookingRef());

            // collect seats. converts[A1 row, A2 row] to ["A1", "A2"]

            // ── Collect all seat numbers ──────────────────────────────
            List<String> seats = group.stream()
                    .map(Booking::getSeatNumber)
                    .filter(s -> s != null)
                    .toList();

            dto.setSeats(seats);

            // ── Calculate total amount ───────────────────────────────
            double total = group.stream()
                    .mapToDouble(Booking::getAmountPaid)
                    .sum();

            dto.setTotalAmount(total);

            // ── Booking status (same for all in group) ───────────────
            dto.setStatus(first.getStatus());

            // ── Add DTO to result list ──
            result.add(dto);
        }

        // ── Sort bookings: latest first ──────────────────────────────
        result.sort((a, b) -> b.getBookedAt().compareTo(a.getBookedAt()));

        return result;
    }
}