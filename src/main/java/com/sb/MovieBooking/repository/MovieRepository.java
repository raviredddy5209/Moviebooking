package com.sb.MovieBooking.repository;

import com.sb.MovieBooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    //List<Movie> findByTheaterId(Long theaterId);
	// ── REMOVED: theater field ────────────────────────────────────────
    // Movie no longer belongs to one theater
    // A movie can play at MANY theaters via Show entity
    // Show has both movie_id and theater_id
}