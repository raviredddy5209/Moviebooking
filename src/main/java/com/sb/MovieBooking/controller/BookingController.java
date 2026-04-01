package com.sb.MovieBooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sb.MovieBooking.model.BookingDto;
import com.sb.MovieBooking.service.ShowBookingService;
@RestController
@RequestMapping("/user") 
public class BookingController {

	@Autowired 
	ShowBookingService showbookingService;
	
	@GetMapping("/bookings")
	public List<BookingDto> getBookings(Authentication auth) {

	    String email = auth.getName();
	    
	    return showbookingService.getBookingsByEmail(email);
	}
	
	
}
