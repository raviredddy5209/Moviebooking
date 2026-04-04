package com.sb.MovieBooking.service;
import com.sb.MovieBooking.entity.*;
import com.sb.MovieBooking.model.User;
import com.sb.MovieBooking.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SeatService {

    @Autowired private SeatRepository seatRepo;
    @Autowired private ShowRepository showRepo;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private UserRepository userRepo;

    // ── GET seats for a show ──────────────────────────────────────────
    public List<Seat> getSeats(Long showId) {

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

        List<Seat> seats = new ArrayList<>();

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

    //Lock seats
    //Locks setas foe 5 minutes before booking
    public ResponseEntity<?> lockSeats(Long showId,
    		List<Map<String,String>> seatRequests,
    		String userEmail){
    	
    	List<String> lockedSeats=new ArrayList<>();
    	
    	for(Map<String,String> seatReq : seatRequests) {
    		
    		String seatNumber = seatReq.get("seatNumber");
    		
    		//Fetch Seat
    		Seat seat = seatRepo.findByShowIdAndSeatNumber(showId, seatNumber);
    		
    		if(seat==null) {
    			return ResponseEntity.badRequest().body("Seat not found" + seatNumber);
    			
    		}
    		//if already nbooked-> reject
    		
    		if(seat.getIsBooked()) {
    			return ResponseEntity.badRequest().body("Seat already booked"+ seatNumber);
    			
    		}
    		// ── If locked by another user and not expired 
    		if(seat.getIsLocked()
    				&& !userEmail.equals(seat.getLockedBy())
    				&& seat.getLockTime() !=null
    				&& seat.getLockTime().plusMinutes(5).isAfter(java.time.LocalDateTime.now())) {
    			return ResponseEntity.badRequest().body("Seat already locked" + seatNumber);
    		}
    		 // ── Lock seat ──
            seat.setIsLocked(true);
            seat.setLockedBy(userEmail);
            seat.setLockTime(java.time.LocalDateTime.now());

            seatRepo.save(seat);

            lockedSeats.add(seatNumber);
    					 
    	}
    	  return ResponseEntity.ok(Map.of(
    	            "message", "Seats locked for 5 minutes",
    	            "seats", lockedSeats
    	    ));
    	 
    }
    
     
    // ── BOOK MULTIPLE SEATS SERVICE METHOD ─────────────────────────────
    public ResponseEntity<?> bookSeats(Map<String, Object> request, Authentication auth) {

        Long showId = Long.valueOf(request.get("showId").toString());

        // ── Get seats list from request ───────────────────────────────
        List<Map<String, String>> seatRequests =
                (List<Map<String, String>>) request.get("seats");

        if (seatRequests == null || seatRequests.isEmpty()) {
            return ResponseEntity.badRequest().body("No seats provided");
        }

        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new RuntimeException("show not found"));

        // ── Fetch logged-in user from Spring Security ──
        String email = auth.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ── Generate ONE bookingRef for ALL seats in this transaction ─
        // This ensures grouping in UI (important concept)
        String bookingRef = UUID.randomUUID().toString();

        double totalAmount = 0;
        List<String> bookedSeats = new ArrayList<>();

        // ── Loop through all requested seats ──
        for (Map<String, String> seatReq : seatRequests) {

            String seatNumber = seatReq.get("seatNumber");
            String section = seatReq.get("section");

            // ── Validate seat existence ──
            Seat seat = seatRepo.findByShowIdAndSeatNumber(showId, seatNumber);
            
            System.out.println("Seat: " + seatNumber);
            System.out.println("isLocked: " + seat.getIsLocked());
            System.out.println("lockedBy: " + seat.getLockedBy());
            System.out.println("currentUser: " + email);
         // ── Ensure seat is locked by current user ──
            if (!seat.getIsLocked() || !email.equals(seat.getLockedBy())) {
                return ResponseEntity.badRequest()
                        .body("Seat not locked by you: " + seatNumber);
            }
            
            

            if (seat == null) {
                return ResponseEntity.badRequest().body("Seat not found " + seatNumber);
            }

            // ── Prevent double booking ──
            if (seat.getIsBooked()) {
                return ResponseEntity.badRequest().body("Seat already booked " + seatNumber);
            }

            // ── Calculate price dynamically based on section ──
            Double amount = switch (section.toUpperCase()) {

                case "DIAMOND" -> show.getDiamondPrice();
                case "GOLD" -> show.getGoldPrice();
                case "SILVER" -> show.getSilverPrice();
                default -> 0.0;
            };

            
         // ── Mark seat as booked ──
            seat.setIsBooked(true);

            // ── Unlock after booking ──
            seat.setIsLocked(false);
            seat.setLockedBy(null);
            seat.setLockTime(null);

            seatRepo.save(seat);

            // ── Create booking record ─────────────────────────────────────
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setShow(show);
            booking.setSeatNumber(seatNumber);
            booking.setSection(section);
            booking.setAmountPaid(amount);
            booking.setBookingRef(bookingRef); // same ref for grouping

            bookingRepo.save(booking);

            // ── Accumulate response data ──
            totalAmount += amount;
            bookedSeats.add(seatNumber);
        }

        // ── Final response sent to frontend ──
        return ResponseEntity.ok(Map.of(
                "message", "Booking confirmed!",
                "bookingRef", bookingRef,
                "seats", bookedSeats,
                "totalAmount", totalAmount
        ));
    }

    public ResponseEntity<?> unlockSeats(Long showId,
            List<Map<String, String>> seats,
            String email) {

    	for (Map<String, String> s : seats) {

        	String seatNumber = s.get("seatNumber");

    		Seat seat = seatRepo.findByShowIdAndSeatNumber(showId, seatNumber);


    			// ── SECURITY CHECK ─────────────────────────────
    			// Only unlock if same user locked it
    		if (seat.getLockedBy() != null &&
    				seat.getLockedBy().equals(email)) {

    				seat.setIsLocked(false);
    					
    				seat.setLockedBy(null);
    				seat.setLockTime(null);;

				seatRepo.save(seat);
}
}

return ResponseEntity.ok("Seats unlocked");
}
    
    
    
    
    
    
 // ── AUTO RELEASE EXPIRED LOCKS ──────────────────────────────────────
 // Runs every 1 minute
 @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
 public void releaseExpiredLocks() {

     List<Seat> lockedSeats = seatRepo.findByIsLockedTrue();

     for (Seat seat : lockedSeats) {

         if (seat.getLockTime() != null &&
                 seat.getLockTime().plusMinutes(5)
                         .isBefore(java.time.LocalDateTime.now())) {

             // ── Unlock seat ──
             seat.setIsLocked(false);
             seat.setLockedBy(null);
             seat.setLockTime(null);

             seatRepo.save(seat);
         }
     }
 }

}

