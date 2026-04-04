package com.sb.MovieBooking.service;
import com.sb.MovieBooking.entity.*;
import com.sb.MovieBooking.model.ShowRequest;
import com.sb.MovieBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class MovieService {

    @Autowired private MovieRepository movieRepo;
    @Autowired private TheaterRepository theaterRepo;
    @Autowired private ShowRepository showRepo;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private SeatRepository seatRepo;

    // ── GET ALL MOVIES ─────────────────────────────────────────
    public List<Movie> getAllMovies() {
        return movieRepo.findAll();
    }

    // ── ADD MOVIE (URL) ─────────────────────────────────────────
    public Movie addMovie(Movie movie) {
        return movieRepo.save(movie);
    }

    // ── ADD MOVIE WITH IMAGE ────────────────────────────────────
    public Movie addMovieWithImage(
            String title,
            String description,
            Integer durationMinutes,
            String imageUrl,
            MultipartFile imageFile) throws IOException {

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setDurationMinutes(durationMinutes);

        if (imageFile != null && !imageFile.isEmpty()) {
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + "/uploads/movies/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            movie.setImagePath("/uploads/movies/" + fileName);
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            movie.setImageUrl(imageUrl);
        }

        return movieRepo.save(movie);
    }

    // ── DELETE MOVIE ────────────────────────────────────────────
    @Transactional
    public void deleteMovie(Long movieId) {

        List<Show> shows = showRepo.findByMovieId(movieId);

        for (Show show : shows) {
            seatRepo.deleteByShowId(show.getId());
            bookingRepo.deleteByShowId(show.getId());
        }

        showRepo.deleteAll(shows);
        movieRepo.deleteById(movieId);
    }

    // ── ADD SHOW ────────────────────────────────────────────────
    public Show addShow(ShowRequest req) {

        Movie movie = movieRepo.findById(req.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Theater theater = theaterRepo.findById(req.getTheaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found"));

        Show show = new Show();
        show.setMovie(movie);
        show.setTheater(theater);
        show.setShowTime(req.getShowTime());
        show.setGoldPrice(req.getGoldPrice());
        show.setSilverPrice(req.getSilverPrice());
        show.setDiamondPrice(req.getDiamondPrice());

        return showRepo.save(show);
    }

    // ── GET SHOWS ───────────────────────────────────────────────
    public List<Show> getShows(Long movieId) {
        return showRepo.findByMovieId(movieId);
    }
}