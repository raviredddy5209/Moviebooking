package com.sb.MovieBooking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="theaters")
public class Theater {
    @Id @GeneratedValue
    private Long id;
    
    private String name;      // "PVR Hyderabad"
    private String city;      // "Hyderabad"
    private Integer totalSeats;   // 100
    
    // Constructors
    public Theater() {}
    public Theater(String name, String city, int totalSeats) {
        this.name = name;
        this.city = city;
        this.totalSeats = totalSeats;
    }
    
    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
}
