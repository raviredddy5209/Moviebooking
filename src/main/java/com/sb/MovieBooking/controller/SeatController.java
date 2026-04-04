package com.sb.MovieBooking.controller;

import com.sb.MovieBooking.entity.Seat;
import com.sb.MovieBooking.service.SeatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/seats")
public class SeatController {

    @Autowired
    private SeatService seatService; // ── Service layer handles all business logic ──

    // ── GET seats for a show ──────────────────────────────────────────
    // URL: GET /user/seats/{showId}
    // Returns all seats with booked status
    // Used by seat selection page to show layout
    @GetMapping("/{showId}")
    public List<Seat> getSeats(@PathVariable Long showId) {

        // ── Delegating to service ──
        // Controller should not contain DB/business logic
        return seatService.getSeats(showId);
    }

    // ── BOOK a seat ───────────────────────────────────────────────────
    // URL: POST /user/seats/book
    // Body: { showId, seatNumber, section }
    // Marks seat as booked and creates booking record
    
    
    // ── BOOK MULTIPLE SEATS in one request ───────────────────────────────
    // Body: { showId, seats: [{seatNumber, section}, ...] }
    // All seats in one request share the SAME bookingRef UUID
    // This is what groups them into ONE card in My Bookings
    
    @PostMapping("/book")
    public ResponseEntity<?> bookSeat(
            @RequestBody Map<String, Object> request,
            Authentication auth) {

        // ── Delegating full booking flow to service ──
        // Keeps controller thin and clean
        return seatService.bookSeats(request, auth);
    }
    /*1. User selects seats
2. CALL /lock  → seats locked (5 min)
3. CALL /book  → only if locked by same user
4. If not booked → auto unlock after 5 min
“I implemented seat locking with expiration using scheduled tasks and
 enforced ownership validation during booking to prevent concurrent booking issues.”*/
    @PostMapping("/lock")
    public ResponseEntity<?> lockSeats(@RequestBody Map<String, Object> request,
                                       Authentication auth) {

        Long showId = Long.valueOf(request.get("showId").toString());

        List<Map<String, String>> seats =
                (List<Map<String, String>>) request.get("seats");

        String email = auth.getName();

        return seatService.lockSeats(showId, seats, email);
    }
    
 // ── UNLOCK SEATS ─────────────────────────────────────────────
 // URL: POST /user/seats/unlock
 // Body: { showId, seats: [{seatNumber, section}, ...] }
 // Unlocks seats only if locked by same user

 @PostMapping("/unlock")
 public ResponseEntity<?> unlockSeats(@RequestBody Map<String, Object> request,
                                      Authentication auth) {

     // ── Extract showId ────────────────────────────────────────
     Long showId = Long.valueOf(request.get("showId").toString());

     // ── Extract seats list ────────────────────────────────────
     List<Map<String, String>> seats =
             (List<Map<String, String>>) request.get("seats");

     // ── Get logged-in user email ──────────────────────────────
     String email = auth.getName();

     // ── Delegate to service ───────────────────────────────────
     return seatService.unlockSeats(showId, seats, email);
 }
    
    
    
    
    
    
}