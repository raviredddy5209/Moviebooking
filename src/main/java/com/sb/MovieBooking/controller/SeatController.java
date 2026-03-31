package com.sb.MovieBooking.controller;

import com.sb.MovieBooking.entity.*;
import com.sb.MovieBooking.model.User;
import com.sb.MovieBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/seats")
public class SeatController {

    @Autowired private SeatRepository seatRepo;
    @Autowired private ShowRepository showRepo;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private UserRepository userRepo;

    // ── GET seats for a show ──────────────────────────────────────────
    // URL: GET /user/seats/{showId}
    // Returns all seats with booked status
    // Used by seat selection page to show layout
    @GetMapping("/{showId}")
    public List<Seat> getSeats(@PathVariable Long showId) {

        List<Seat> seats = seatRepo.findByShowId(showId);

        // ── If no seats exist yet, generate them ──────────────────────
        // First time a show is accessed, create all seats automatically
        // Multiplex layout: Diamond (A-C), Gold (D-G), Silver (H-J)
        if (seats.isEmpty()) {
            Show show = showRepo.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));
            seats = generateSeats(show);
        }

        return seats;
    }

    // ── Generate seats for a show ─────────────────────────────────────
    // Creates seat layout automatically on first access
    // Diamond: rows A-C (premium front)
    // Gold: rows D-G (middle)
    // Silver: rows H-J (back)
    private List<Seat> generateSeats(Show show) {
        List<Seat> seats = new java.util.ArrayList<>();

        // ── Diamond section — rows A to C, 10 seats each ──────────────
        for (char row = 'A'; row <= 'C'; row++) {
            for (int num = 1; num <= 10; num++) {
                seats.add(new Seat(show, row + "" + num, "DIAMOND"));
            }
        }

        // ── Gold section — rows D to G, 10 seats each ─────────────────
        for (char row = 'D'; row <= 'G'; row++) {
            for (int num = 1; num <= 10; num++) {
                seats.add(new Seat(show, row + "" + num, "GOLD"));
            }
        }

        // ── Silver section — rows H to J, 10 seats each ───────────────
        for (char row = 'H'; row <= 'J'; row++) {
            for (int num = 1; num <= 10; num++) {
                seats.add(new Seat(show, row + "" + num, "SILVER"));
            }
        }

        // ── Save all seats to DB ───────────────────────────────────────
        return seatRepo.saveAll(seats);
    }

    // ── BOOK a seat ───────────────────────────────────────────────────
    // URL: POST /user/seats/book
    // Body: { showId, seatNumber, section }
    // Marks seat as booked and creates booking record
    @PostMapping("/book")
    public ResponseEntity<?> bookSeat(
            @RequestBody Map<String, Object> request,
            Authentication auth) {

        Long showId = Long.valueOf(request.get("showId").toString());
        String seatNumber = request.get("seatNumber").toString();
        String section = request.get("section").toString();

        // ── Check if seat is already booked ───────────────────────────
        Seat seat = seatRepo.findByShowIdAndSeatNumber(showId, seatNumber);
        if (seat == null) {
            return ResponseEntity.badRequest().body("Seat not found");
        }
        if (seat.getIsBooked()) {
            return ResponseEntity.badRequest().body("Seat already booked");
        }

        // ── Get show details for pricing ──────────────────────────────
        Show show = showRepo.findById(showId)
            .orElseThrow(() -> new RuntimeException("Show not found"));

        // ── Calculate amount based on section ─────────────────────────
        Double amount = switch (section.toUpperCase()) {
            case "DIAMOND" -> show.getDiamondPrice();
            case "GOLD"    -> show.getGoldPrice();
            case "SILVER"  -> show.getSilverPrice();
            default -> 0.0;
        };

        // ── Get logged in user from JWT ───────────────────────────────
        String email = auth.getName();
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // ── Mark seat as booked ───────────────────────────────────────
        seat.setIsBooked(true);
        seatRepo.save(seat);

        // ── Create booking record ─────────────────────────────────────
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeatNumber(seatNumber);
        booking.setSection(section);
        booking.setAmountPaid(amount);
        bookingRepo.save(booking);

        return ResponseEntity.ok(Map.of(
            "message", "Booking confirmed!",
            "seatNumber", seatNumber,
            "section", section,
            "amountPaid", amount
        ));
    }
}