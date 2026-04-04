package com.sb.MovieBooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sb.MovieBooking.entity.Booking;
import com.sb.MovieBooking.model.BookingDto;
import com.sb.MovieBooking.service.CancelUserBookingService;
import com.sb.MovieBooking.service.ShowBookingService;

@RestController
@RequestMapping("/user") 
public class BookingController {

	@Autowired 
	ShowBookingService showbookingService;
	@Autowired
	CancelUserBookingService  canceluserbookingService ;
	
	@GetMapping("/bookings")
	public List<BookingDto> getBookings(Authentication auth) {

	    String email = auth.getName();
	    
	    return showbookingService.getBookingsByEmail(email);
	}
	// INPUT:
	// Logged-in user email (from Authentication)

	// PROCESS:
	// Calls service to fetch bookings
	// Service groups bookings by bookingRef

	// OUTPUT:
	// List<BookingDto>
	// (each DTO = one booking card with multiple seats)
	
 
	/*Controller
   ↓
ShowBookingService → fetch bookings (read)
BookingService     → cancel bookings (write/update)
	 * 
	 * 
	 * 
	 * */
	// ✅ Cancel Booking API
  //  @PutMapping("/bookings/{bookingId}/cancel")
	@PutMapping("/bookings/show/{bookingRef}/cancel")
    public ResponseEntity<?> cancelBookingByBookingReference(
            @PathVariable String bookingRef,
            Authentication auth) {

        String email = auth.getName();
try {
	
	System.out.println("Cancel entered" + bookingRef);
	canceluserbookingService.cancelBookingByBookingReference(bookingRef, email);
    //    console.log("cancel entered");

        return ResponseEntity.ok("Booking cancelled successfully");
    }catch(RuntimeException e) {
    	// ── Return 400 with message instead of throwing 500 ───────────
        // 500 triggers error handler → PUT /index.html → loop ❌
        // 400 returns JSON → axios .catch() handles it cleanly ✅
        return ResponseEntity.badRequest().body(e.getMessage());
 
    
    
    }
    }
	
    }
