package com.sb.MovieBooking.model;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDto {

    private Long showId;
    private String movieTitle;
    private Long movieId;
    private String imageUrl;

    private String theaterName;
    private LocalDateTime showTime;
    private List<String> seats;
    private double totalAmount;
 // ── Add booking ID to DTO ─────────────────────────────────────────────
 // Needed for cancel button to know which booking to cancel
 private Long id;
 private BookingStatus status;
 private LocalDateTime bookedAt;

  private String bookingRef;
	public void setShowId(Long showId) {
		this.showId = showId;
	}
	public String getMovieTitle() {
		return movieTitle;
	}
	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}
	
	public Long getMovieId() {
		return movieId;
	}
	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTheaterName() {
		return theaterName;
	}
	public void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}
	public LocalDateTime getShowTime() {
		return showTime;
	}
	public void setShowTime(LocalDateTime showTime) {
		this.showTime = showTime;
	}
	public List<String> getSeats() {
		return seats;
	}
	public void setSeats(List<String> seats) {
		this.seats = seats;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}		

	 public Long getId() { return id; }
	 public void setId(Long id) { this.id = id; }
		public Long getShowId() {
			return showId;
		}
		public BookingStatus getStatus() {
			return status;
		}
		public void setStatus(BookingStatus status) {
			this.status = status;
		}
		public LocalDateTime getBookedAt() {
			return bookedAt;
		}
		public void setBookedAt(LocalDateTime bookedAt) {
			this.bookedAt = bookedAt;
		}
		public String getBookingRef() {
			return bookingRef;
		}
		public void setBookingRef(String bookingRef) {
			this.bookingRef = bookingRef;
		}
}