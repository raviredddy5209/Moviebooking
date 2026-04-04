package com.sb.MovieBooking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sb.MovieBooking.entity.Theater;
import com.sb.MovieBooking.repository.TheaterRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional  // ── Ensures all DB operations complete or rollback together ──
public class TheaterService {

    @Autowired
    private TheaterRepository repo;

    // ── CREATE Theater ───────────────────────────────────────────────
    public Theater createTheater(Theater theater) {

        // ── Saves theater entity into database ──
        return repo.save(theater);
    }

    // ── GET all theaters ─────────────────────────────────────────────
    public List<Theater> getAllTheaters() {

        // ── Fetches all records from DB ──
        return repo.findAll();
    }

    // ── DELETE Theater ───────────────────────────────────────────────
    public ResponseEntity<String> deleteTheater(Long id) {

        // ── Check if theater exists before deleting ──
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().body("Theater not found");
        }

        // ── Delete theater ──
        repo.deleteById(id);

        return ResponseEntity.ok("Theater deleted");
    }
}