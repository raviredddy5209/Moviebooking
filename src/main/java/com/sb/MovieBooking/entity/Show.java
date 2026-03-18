package com.sb.MovieBooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
public class Show {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "movie_id")
	private Movie movie;

	@ManyToOne
	@JoinColumn(name = "theater_id")
	private Theater theater;

	private LocalDateTime showTime;
	private double goldPrice;
	private double silverPrice;
	private double diamondPrice;

	// Constructors
	public Show() {}

	// Getters & Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Movie getMovie() { return movie; }
	public void setMovie(Movie movie) { this.movie = movie; }
	public Theater getTheater() { return theater; }
	public void setTheater(Theater theater) { this.theater = theater; }
	public LocalDateTime getShowTime() { return showTime; }
	public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
	public double getGoldPrice() { return goldPrice; }
	public void setGoldPrice(double goldPrice) { this.goldPrice = goldPrice; }
	public double getSilverPrice() { return silverPrice; }
	public void setSilverPrice(double silverPrice) { this.silverPrice = silverPrice; }
	public double getDiamondPrice() { return diamondPrice; }
	public void setDiamondPrice(double diamondPrice) { this.diamondPrice = diamondPrice; }
}