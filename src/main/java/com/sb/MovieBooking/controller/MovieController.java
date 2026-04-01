package com.sb.MovieBooking.controller;

import com.sb.MovieBooking.entity.Booking;
import com.sb.MovieBooking.entity.Movie;
import com.sb.MovieBooking.entity.Theater;
import com.sb.MovieBooking.model.ShowRequest;
import com.sb.MovieBooking.entity.Show;
import com.sb.MovieBooking.repository.BookingRepository;
import com.sb.MovieBooking.repository.MovieRepository;
import com.sb.MovieBooking.repository.SeatRepository;
import com.sb.MovieBooking.repository.ShowRepository;
import com.sb.MovieBooking.repository.TheaterRepository;

import jakarta.transaction.Transactional;

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
	/*
	 * @GetMapping("/public/movies") public List<Movie> getAllMoviesPublic() { //
	 * findAll() → SELECT * FROM movies (with theater JOIN) return
	 * movieRepo.findAll(); }
	 * 
	 * // ── GET SHOWS FOR A MOVIE (Public) ──────────────────────────────────── //
	 * URL: GET /public/movies/1/shows // @PathVariable Long id → reads {id} from
	 * URL // No login required — users need to see show timings before booking //
	 * Returns all shows for that movie including theater + pricing info
	 * 
	 * @GetMapping("/public/movies/{id}/shows") public List<Show>
	 * getShowsPublic(@PathVariable Long id) { // findByMovieId() → SELECT * FROM
	 * shows WHERE movie_id = ? return showRepo.findByMovieId(id); }
	 */
	
	
	
	
    @Autowired private MovieRepository movieRepo;
    @Autowired private TheaterRepository theaterRepo;
    @Autowired private ShowRepository showRepo;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private SeatRepository seatRepo;
    // ── GET ALL MOVIES (for admin dropdown when adding show) ──────────
    // URL: GET /admin/movies
    // Returns all movies globally — not filtered by theater
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepo.findAll();
    }   
    
    
    
    /*// Get all movies for a theater
    @GetMapping("/theater/{theaterId}")
    public List<Movie> getMoviesByTheater(@PathVariable Long theaterId) {
        return movieRepo.findByTheaterId(theaterId);
    }*/
    
   //── ADD MOVIE (URL image) ─────────────────────────────────────────
    // URL: POST /admin/movies
    // Movie is now global — no theater field needed
    // Theater is assigned when adding a Show, not a Movie

    // Add movie with URL image
    @PostMapping("/add")
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
       // Theater theater = theaterRepo.findById(movie.getTheater().getId())
       //     .orElseThrow(() -> new RuntimeException("Theater not found"));
     //   movie.setTheater(theater);
        return ResponseEntity.ok(movieRepo.save(movie));
    }

    // Add movie with file upload
    @PostMapping("/upload")
    public ResponseEntity<Movie> addMovieWithImage(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Integer durationMinutes,
            @RequestParam Long theaterId,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setDurationMinutes(durationMinutes);

      //  Theater theater = theaterRepo.findById(theaterId)
           // .orElseThrow(() -> new RuntimeException("Theater not found"));
      //  movie.setTheater(theater);

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
    @Transactional
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
    //	Your delete order must now be: seats → bookings → shows → movie.This follows the correct deletion order: delete leaf nodes (seats) 
    			//		→ middle nodes (bookings) 
    					//	→ parent nodes (shows) → grandparent (movie).
    	//first delete bookings , then shows , then movie
    	
    	
    	
    	
    	// ── Step 1: Delete all shows for this movie first ─────────────────
        // Shows have a foreign key to movies (movie_id)
        // MySQL won't allow deleting a movie if shows still reference it
        // So we delete shows first, then the movie
    	//finding all shows for movie
    	
    	/*Yes — now you have exactly what is needed to fix it. 
    	 * Your BookingRepository already has findByShowId(Long showId), 
    	 * which means you can delete bookings for each show before deleting the shows and then the movie
    	 * . The foreign key error happens because bookings.show_id still points to the shows rows you are trying to delet*/
        List<Show> shows = showRepo.findByMovieId(id);
        for (Show show : shows) {
        	//Delete seats first
            seatRepo.deleteByShowId(show.getId());
        	//List<Booking> bookings  =  bookingRepo.findByShowId(show.getId());  // You need this method
        	//if(!bookings.isEmpty()) {
        	//	bookingRepo.deleteAll(bookings);
          
            // Then bookings for this show
            bookingRepo.deleteByShowId(show.getId());
        	
        	//}
        
        
        }//3. Delete all shows for this movie
        showRepo.deleteAll(shows);
    	
    	
     // ── Step 2: Now delete the movie safely ───────────────────────────
        // No shows reference this movie anymore → delete succeeds
    	
        movieRepo.deleteById(id);
        return ResponseEntity.ok("Movie deleted");
    }/*Better repository method
		Instead of loading bookings and then deleting them, you can add a derived delete method in BookingRepository:


			void deleteByShowId(Long showId);
			Spring Data JPA supports derived delete methods like deleteBy..., so this is cleaner than fetching the list first.

				Then your controller becomes:


				@DeleteMapping("/{id}")
				public ResponseEntity<String> deleteMovie(@PathVariable Long id) {

    			List<Show> shows = showRepo.findByMovieId(id);

    			for (Show show : shows) {
     	  		bookingRepo.deleteByShowId(show.getId());
    			}

    			showRepo.deleteAll(shows);
    			movieRepo.deleteById(id);

    			return ResponseEntity.ok("Movie deleted successfully");
}
    
    *
    *
    *
    *
    *
    *
    */
    // ── ADD SHOW ──────────────────────────────────────────────────────
    // URL: POST /admin/shows
    // Now takes BOTH movieId AND theaterId separately
    // This is the correct design — show links movie + theater
  /*  
    Movies section → Add Movie globally
    Theaters section → Add Show 
                       (select which movie + time + pricing) */
    
    @PostMapping("/shows")
    public ResponseEntity<Show> addShow(@RequestBody ShowRequest req){
    	
    	Movie movie = movieRepo.findById(req.getMovieId())
    			.orElseThrow(() ->new RuntimeException("Movie not found"));
    	Theater theatre = theaterRepo.findById(req.getTheaterId())
    			.orElseThrow(() -> new RuntimeException("Theatre not found"));
    	
    	Show show =  new Show();
    	show.setMovie(movie);
    	show.setTheater(theatre);
    	show.setShowTime(req.getShowTime());
    	show.setGoldPrice(req.getGoldPrice());
    	show.setSilverPrice(req.getSilverPrice());
    	show.setDiamondPrice(req.getDiamondPrice());
    	
    	
    	
		return ResponseEntity.ok(showRepo.save(show));
    	
     	
    	
    }
    
   
    
    /*

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
    }*/

    // Get shows for a movie
    @GetMapping("/{movieId}/shows")
    public List<Show> getShows(@PathVariable Long movieId) {
        return showRepo.findByMovieId(movieId);
    }
}