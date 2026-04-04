package com.sb.MovieBooking.service;

import com.sb.MovieBooking.entity.Movie;
import com.sb.MovieBooking.entity.Show;
import com.sb.MovieBooking.entity.Theater;
import com.sb.MovieBooking.repository.MovieRepository;
import com.sb.MovieBooking.repository.ShowRepository;
import com.sb.MovieBooking.repository.TheaterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ── @Service ─────────────────────────────────────────────────────────
// BUSINESS LOGIC LAYER
// Controller → calls Service
// Service → talks to Repository (DB)
@Service
public class SearchService {

    // ── REPOSITORIES (DB ACCESS ONLY HERE) ───────────────────────────
    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private TheaterRepository theaterRepo;

    @Autowired
    private ShowRepository showRepo;

    // ────────────────────────────────────────────────────────────────
    // 🔍 SEARCH LOGIC
    // ────────────────────────────────────────────────────────────────
    // SERVICE RECEIVES:
    //   q → search keyword
    //
    // SERVICE RETURNS:
    //   Map → { movies: [...], theaters: [...] }
    public Map<String, Object> search(String q) {

        Map<String, Object> result = new HashMap<>();

        // ── Search Movies ────────────────────────────────────────────
        List<Movie> movies = movieRepo.findAll()
                .stream()
                .filter(m -> m.getTitle()
                        .toLowerCase()
                        .contains(q.toLowerCase()))
                .toList();

        // ── Search Theaters ──────────────────────────────────────────
        List<Theater> theaters = theaterRepo.findAll()
                .stream()
                .filter(t ->
                        t.getName().toLowerCase().contains(q.toLowerCase())
                        || t.getCity().toLowerCase().contains(q.toLowerCase())
                )
                .toList();

        result.put("movies", movies);
        result.put("theaters", theaters);

        return result;
    }

    // ────────────────────────────────────────────────────────────────
    // 🎬 GET ALL MOVIES
    // ────────────────────────────────────────────────────────────────
    public List<Movie> getAllMovies() {
        return movieRepo.findAll();
    }

    // ────────────────────────────────────────────────────────────────
    // 🎟 GET SHOWS FOR MOVIE
    // ────────────────────────────────────────────────────────────────
    public List<Show> getShowsForMovie(Long movieId) {
        return showRepo.findByMovieId(movieId);
    }
}