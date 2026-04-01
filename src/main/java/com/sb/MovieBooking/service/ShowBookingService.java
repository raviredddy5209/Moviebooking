package com.sb.MovieBooking.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sb.MovieBooking.entity.Booking;
import com.sb.MovieBooking.model.BookingDto;
import com.sb.MovieBooking.model.User;
import com.sb.MovieBooking.repository.BookingRepository;
import com.sb.MovieBooking.repository.UserRepository;
@Service
public class ShowBookingService {

	@Autowired
	BookingRepository bookingRepository;
	
	@Autowired
    private UserRepository userRepository;

    public List<BookingDto> getBookingsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getBookingsByUser(user.getId());
    }
	
	public List<BookingDto> getBookingsByUser(Long userId) {
		// Fetch all booking rows for user
		//[A1 row, A2 row, B1 row, B2 row...]
	    List<Booking> bookings = bookingRepository.findByUserId(userId);
	   
	    /*GROUPING
	    	 	* A1 (show 1)
				A2 (show 1)
				B1 (show 2) to 
				showId 1 → [A1, A2]
				showId 2 → [B1]

 */ 
	    Map<Long, List<Booking>> grouped =
	            bookings.stream()
	                    .collect(Collectors.groupingBy(b -> b.getShow().getId()));
	    
//CONVERT EACH GROUP → DTO
	    
	    List<BookingDto> result = new ArrayList<>();

	    //Each loop = one booking card
	    for (Map.Entry<Long, List<Booking>> entry : grouped.entrySet()) {
	    		//Extract common data
	        List<Booking> group = entry.getValue();
	        Booking first = group.get(0);
	        	/*Why first?

Because:

All rows in group have same show/movie/theater
So we take first row as reference
	        */
	        
	        BookingDto dto = new BookingDto();

	        dto.setShowId(entry.getKey());
	        dto.setMovieTitle(first.getShow().getMovie().getTitle()); 
	        dto.setMovieId(first.getShow().getMovie().getId());
	        dto.setImageUrl(first.getShow().getMovie().getImageUrl()); 
	        dto.setTheaterName(first.getShow().getTheater().getName());
	        dto.setShowTime(first.getShow().getShowTime());
	        //collect seats. converts[A1 row, A2 row] to ["A1", "A2"]
	        List<String> seats = group.stream()
	                .map(Booking::getSeatNumber)
	                .toList();

	        dto.setSeats(seats);

	        double total = group.stream()
	                .mapToDouble(Booking::getAmountPaid)
	                .sum();

	        dto.setTotalAmount(total);

	        result.add(dto);
	    }

	    // latest first
	    result.sort((a, b) -> b.getShowTime().compareTo(a.getShowTime()));

	    return result;
	}
}
