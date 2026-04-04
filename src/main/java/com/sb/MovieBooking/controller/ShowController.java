package com.sb.MovieBooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sb.MovieBooking.service.ShowService;

@RestController
@RequestMapping("/admin/shows")
public class ShowController {

    @Autowired
    private ShowService showService; // ── Service layer handles deletion logic ──

    // ── DELETE a show ────────────────────────────────────────────────
    // URL: DELETE /admin/shows/{id}
    // Deletes show along with its seats and bookings
    // Used by admin to remove a show completely
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShow(@PathVariable Long id) {

        // ── Delegating deletion to service ──
        // Controller remains thin (no DB logic here)
        return showService.deleteShow(id);
    }
}