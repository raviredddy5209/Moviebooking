package com.sb.MovieBooking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String imageUrl;
    private String imagePath;

    // ── Changed from int to Integer ───────────────────────────────────
    // int cannot hold null — crashes when JSON field is missing
    // Integer (wrapper class) can hold null safely
    private Integer durationMinutes;

    
  /**  
    
 // ── REMOVED: theater field ────────────────────────────────────────
    // Movie no longer belongs to one theater
    // A movie can play at MANY theaters via Show entity
    // Show has both movie_id and theater_id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theater_id")
    private Theater theater; 
    */

    public Movie() {}

    // ── Getters and Setters ───────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // ── Integer getter/setter ─────────────────────────────────────────
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
 
}
