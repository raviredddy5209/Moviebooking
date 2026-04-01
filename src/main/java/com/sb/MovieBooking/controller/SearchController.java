package com.sb.MovieBooking.controller;

import com.sb.MovieBooking.entity.Movie;
import com.sb.MovieBooking.entity.Show;
import com.sb.MovieBooking.entity.Theater;
import com.sb.MovieBooking.repository.MovieRepository;
import com.sb.MovieBooking.repository.ShowRepository;
import com.sb.MovieBooking.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ── @RestController ──────────────────────────────────────────────────
// Combines @Controller + @ResponseBody
// Every method automatically returns JSON — no need to write @ResponseBody on each method
// Spring converts Java objects → JSON automatically using Jackson library
@RestController

// ── @RequestMapping("/public") ───────────────────────────────────────
// All endpoints in this controller start with /public
// /public/** is permitted for all in SecurityConfig (no login needed)
// This means BOTH logged-in users AND guests can search
@RequestMapping("/public")
public class SearchController {

    // ── @Autowired ───────────────────────────────────────────────────
    // Spring automatically injects MovieRepository bean here
    // We use this to query the movies table in MySQL
    @Autowired
    private MovieRepository movieRepo;

    // ── @Autowired ───────────────────────────────────────────────────
    // Spring automatically injects TheaterRepository bean here
    // We use this to query the theaters table in MySQL
    @Autowired
    private TheaterRepository theaterRepo;
 // Need ShowRepository for the new shows endpoint
    @Autowired private ShowRepository showRepo;

    // ── SEARCH ENDPOINT ──────────────────────────────────────────────
    // URL: GET /public/search?q=PVR
    // URL: GET /public/search?q=Interstellar
    // @RequestParam String q → reads the ?q= value from URL
    // Returns: Map with two keys — "movies" list and "theaters" list
    // Example response:
    // {
    //   "movies": [{ "id": 1, "title": "Interstellar", ... }],
    //   "theaters": [{ "id": 1, "name": "PVR Gachibowli", ... }]
    // }
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String q) {

        // ── Create result map ─────────────────────────────────────────
        // HashMap is a key-value store
        // We'll put "movies" and "theaters" as keys
        // Jackson will convert this Map → JSON automatically
        Map<String, Object> result = new HashMap<>();

        // ── Search movies by title ────────────────────────────────────
        // movieRepo.findAll() → SELECT * FROM movies
        // .stream() converts List to Stream for filtering
        // .filter() keeps only movies where title contains search query
        // toLowerCase() on both sides makes search case-insensitive
        // Example: search "inter" matches "Interstellar"
        // .toList() converts filtered Stream back to List
        List<Movie> movies = movieRepo.findAll()
            .stream()
            .filter(m -> m.getTitle()
                .toLowerCase()
                .contains(q.toLowerCase()))
            .toList();

        // ── Search theaters by name OR city ───────────────────────────
        // Same approach as movies above
        // But we check TWO fields — name AND city
        // || means OR — matches if EITHER name or city contains query
        // Example: search "Hyderabad" finds all theaters in Hyderabad
        // Example: search "PVR" finds all PVR theaters in any city
        List<Theater> theaters = theaterRepo.findAll()
            .stream()
            .filter(t ->
                t.getName().toLowerCase().contains(q.toLowerCase())
                || t.getCity().toLowerCase().contains(q.toLowerCase())
            )
            .toList();

        // ── Put results in map ────────────────────────────────────────
        // "movies" key → list of matched movies
        // "theaters" key → list of matched theaters
        // Frontend JS reads: res.data.movies and res.data.theaters
        result.put("movies", movies);
        result.put("theaters", theaters);

        // ── Return map ────────────────────────────────────────────────
        // Spring + Jackson converts this Map to JSON automatically
        // Frontend receives: { "movies": [...], "theaters": [...] }
        return result;
    }
    
    // ── ADD THIS — GET ALL MOVIES ─────────────────────────────────────
    // URL: GET /public/movies
    // Called by user/home.html to load the Now Showing grid
    // No login needed — /public/** is in permitAll()
 // ── GET ALL MOVIES (public) ───────────────────────────────────────────
 // Returns movies globally — no theater attached to movie
 // Theater info comes from shows when user clicks a movie
    @GetMapping("/movies")
    public List<Movie> getAllMovies() {
        // SELECT * FROM movies (with theater JOIN)
        return movieRepo.findAll();
    }

    // ── ADD THIS — GET SHOWS FOR A MOVIE ──────────────────────────────
    // URL: GET /public/movies/1/shows
    // Called when user clicks a movie card to see show timings
    // Returns all shows with Gold/Silver/Diamond pricing
 // ── GET SHOWS FOR A MOVIE (public) ────────────────────────────────────
 // Returns shows with theater data (EAGER loaded)
 // Frontend groups these by theater to show multiple theaters
    @GetMapping("/movies/{id}/shows")
    public List<Show> getShowsForMovie(@PathVariable Long id) {
        // SELECT * FROM shows WHERE movie_id = ?
        return showRepo.findByMovieId(id);
    }
}