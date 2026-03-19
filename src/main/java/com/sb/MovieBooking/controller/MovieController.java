package com.sb.MovieBooking.controller;

import com.sb.MovieBooking.entity.Movie;
import com.sb.MovieBooking.entity.Theater;
import com.sb.MovieBooking.entity.Show;
import com.sb.MovieBooking.repository.MovieRepository;
import com.sb.MovieBooking.repository.ShowRepository;
import com.sb.MovieBooking.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/movies")
public class MovieController {
	
	
	// ── GET ALL MOVIES (Public) ───────────────────────────────────────────
	// URL: GET /public/movies
	// No login required — anyone can see what movies are showing
	// Used by user home page to display Now Showing grid
	// Returns all movies with their theater details
	@GetMapping("/public/movies")
	public List<Movie> getAllMoviesPublic() {
	    // findAll() → SELECT * FROM movies (with theater JOIN)
	    return movieRepo.findAll();
	}

	// ── GET SHOWS FOR A MOVIE (Public) ────────────────────────────────────
	// URL: GET /public/movies/1/shows
	// @PathVariable Long id → reads {id} from URL
	// No login required — users need to see show timings before booking
	// Returns all shows for that movie including theater + pricing info
	@GetMapping("/public/movies/{id}/shows")
	public List<Show> getShowsPublic(@PathVariable Long id) {
	    // findByMovieId() → SELECT * FROM shows WHERE movie_id = ?
	    return showRepo.findByMovieId(id);
	}
	
	
	
	
    @Autowired private MovieRepository movieRepo;
    @Autowired private TheaterRepository theaterRepo;
    @Autowired private ShowRepository showRepo;

    // Get all movies for a theater
    @GetMapping("/theater/{theaterId}")
    public List<Movie> getMoviesByTheater(@PathVariable Long theaterId) {
        return movieRepo.findByTheaterId(theaterId);
    }

    // Add movie with URL image
    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        Theater theater = theaterRepo.findById(movie.getTheater().getId())
            .orElseThrow(() -> new RuntimeException("Theater not found"));
        movie.setTheater(theater);
        return ResponseEntity.ok(movieRepo.save(movie));
    }

    // Add movie with file upload
    @PostMapping("/upload")
    public ResponseEntity<Movie> addMovieWithImage(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam int durationMinutes,
            @RequestParam Long theaterId,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setDurationMinutes(durationMinutes);

        Theater theater = theaterRepo.findById(theaterId)
            .orElseThrow(() -> new RuntimeException("Theater not found"));
        movie.setTheater(theater);

        // Handle image
        if (imageFile != null && !imageFile.isEmpty()) {
            // Save file to uploads folder
        	// ── Get project root directory ────────────────────────────────────────
        	// System.getProperty("user.dir") returns where Spring Boot is running from
        	// This ensures uploads folder is always found regardless of how app starts
        	String projectRoot = System.getProperty("user.dir");

        	
        	// ── Create uploads directory if it doesn't exist ──────────────────────
        	String uploadDir = projectRoot + "/uploads/movies/";
        	Files.createDirectories(Paths.get(uploadDir));

        	
        	
        	
        	
        	
          //  String uploadDir = "uploads/movies/";
          //  Files.createDirectories(Paths.get(uploadDir));
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            movie.setImagePath("/uploads/movies/" + fileName);
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            movie.setImageUrl(imageUrl);
        }

        return ResponseEntity.ok(movieRepo.save(movie));
    }

    // Delete movie
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
    	
    	// ── Step 1: Delete all shows for this movie first ─────────────────
        // Shows have a foreign key to movies (movie_id)
        // MySQL won't allow deleting a movie if shows still reference it
        // So we delete shows first, then the movie
        List<Show> shows = showRepo.findByMovieId(id);
        showRepo.deleteAll(shows);
    	
    	
     // ── Step 2: Now delete the movie safely ───────────────────────────
        // No shows reference this movie anymore → delete succeeds
    	
        movieRepo.deleteById(id);
        return ResponseEntity.ok("Movie deleted");
    }

    // Add show timing to a movie
    @PostMapping("/{movieId}/shows")
    public ResponseEntity<Show> addShow(
            @PathVariable Long movieId,
            @RequestBody Show show) {
        Movie movie = movieRepo.findById(movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found"));
        show.setMovie(movie);
        show.setTheater(movie.getTheater());
        return ResponseEntity.ok(showRepo.save(show));
    }

    // Get shows for a movie
    @GetMapping("/{movieId}/shows")
    public List<Show> getShows(@PathVariable Long movieId) {
        return showRepo.findByMovieId(movieId);
    }
}