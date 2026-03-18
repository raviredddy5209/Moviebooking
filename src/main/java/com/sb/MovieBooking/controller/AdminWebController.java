//package com.sb.MovieBooking.controller;
// 
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import com.sb.MovieBooking.entity.Theater;
//import com.sb.MovieBooking.repository.TheaterRepository;
//
//@Controller  // ONE class for HTML + API
//public class AdminWebController {
////
//    @Autowired
//    private TheaterRepository theatreRepository;
//
//    // Your WebController merged here
//    @GetMapping("/admin/dashboard")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String adminDashboard() {
//        return "admin/dashboard.html";
//    }
//
//    // API endpoint
//    @GetMapping("/admin/theatres")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<Theater>> getTheatres() {
//        return ResponseEntity.ok(theatreRepository.findAll());
//    }
//}
