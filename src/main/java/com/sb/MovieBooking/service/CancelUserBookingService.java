package com.sb.MovieBooking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sb.MovieBooking.entity.Booking;
import com.sb.MovieBooking.entity.Seat;
import com.sb.MovieBooking.model.BookingStatus;
import com.sb.MovieBooking.model.User;
import com.sb.MovieBooking.repository.BookingRepository;
import com.sb.MovieBooking.repository.SeatRepository;
import com.sb.MovieBooking.repository.UserRepository;

@Service
 
public class CancelUserBookingService {
	@Autowired
	BookingRepository bookingRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired 
	SeatRepository seatRepository;
	@Transactional
	public void cancelBookingByBookingReference(String bookingRef, String email) {

		 User user = userRepository.findByEmail(email)
		            .orElseThrow(() -> new RuntimeException("User not found"));

		
		 List<Booking> bookings = bookingRepository
		            .findByUserIdAndBookingRef(user.getId(), bookingRef);
		 
		// List<Booking> bookings = bookingRepository.findByBookingRef(bookingRef);
		 System.out.println("Found bookings: " + bookings.size());

		    if (bookings.isEmpty()) {
		        throw new RuntimeException("No booking found");
		    }

		    /*boolean alreadyCancelled = bookings.stream()
		            .allMatch(b -> b.getStatus() == BookingStatus.CANCELLED);
		

		    if (alreadyCancelled) {
		        throw new RuntimeException("Already cancelled");
		    }*/List<Booking> activeBookings = bookings.stream()
		            .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
		            .toList();

		    if (activeBookings.isEmpty()) {
		        throw new RuntimeException("All bookings already cancelled");
		    }
		    
		    //1. Cancel bookings
		    for (Booking b : bookings) {
		        b.setStatus(BookingStatus.CANCELLED);
		    }

		    bookingRepository.saveAll(bookings);
		    
		    //Release seats
		    for(Booking b : activeBookings) {
		    	b.setStatus(BookingStatus.CANCELLED);
		    	//String seatNumber = b.getSeatNumber().trim().toUpperCase();
		    	Seat seat = seatRepository.findByShowIdAndSeatNumber(b.getShow().getId(), b.getSeatNumber());
		    	if (seat != null) {
		            seat.setIsBooked(false);
		            seatRepository.save(seat);
		    	}
		    }
		    
		    
		    /*Flow after fix
	Booking:
				Seat → isBooked = true
				Booking → BOOKED
	 Cancel:
				Booking → CANCELLED
				Seat → isBooked = false
	Rebooking:
				Seat now available ✅
				Booking allowed ✅
		     * 
		     * 
		     * 
		     * */
		    
		
		/*
		Booking booking = bookingRepository.findById(bookingId)
	            .orElseThrow(() -> new RuntimeException("Booking not found"));
	    System.out.println("Cancel request for bookingId: " + bookingId);
	    System.out.println("User email: " + email);
	    // ✅ Ownership check
	    if (!booking.getUser().getEmail().equals(email)) {
	        throw new RuntimeException("Unauthorized");
	    }

	    // ❌ Already cancelled check
	    if (booking.getStatus() == BookingStatus.CANCELLED) {
	        throw new RuntimeException("Already cancelled");
	    }

	    booking.setStatus(BookingStatus.CANCELLED);

	    // 🔓 release seat
	    booking.setSeatNumber(null);
	    //seat.setBooked(false);*/

	}

}
