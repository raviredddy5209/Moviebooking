package com.sb.MovieBooking.model;

 
import java.time.LocalDateTime;

// ── Data Transfer Object for adding a show ────────────────────────────
// Contains both movieId AND theaterId separately
// This is the correct design — admin picks movie + theater + time
public class ShowRequest {

    private Long movieId;
    private Long theaterId;
    private LocalDateTime showTime;
    private Double goldPrice;
    private Double silverPrice;
    private Double diamondPrice;

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public Long getTheaterId() { return theaterId; }
    public void setTheaterId(Long theaterId) { this.theaterId = theaterId; }
    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
    public Double getGoldPrice() { return goldPrice; }
    public void setGoldPrice(Double goldPrice) { this.goldPrice = goldPrice; }
    public Double getSilverPrice() { return silverPrice; }
    public void setSilverPrice(Double silverPrice) { this.silverPrice = silverPrice; }
    public Double getDiamondPrice() { return diamondPrice; }
    public void setDiamondPrice(Double diamondPrice) { this.diamondPrice = diamondPrice; }
}