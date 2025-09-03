package com.videoclub.controller;

import com.videoclub.dto.MovieDto;
import com.videoclub.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Movies", description = "Movie management API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Operation(summary = "Get all movies")
    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @Operation(summary = "Get movie by ID")
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(movie -> ResponseEntity.ok(movie))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new movie (Admin only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto movieDto) {
        MovieDto createdMovie = movieService.createMovie(movieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @Operation(summary = "Update movie (Admin only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDto movieDto) {
        return movieService.updateMovie(id, movieDto)
                .map(movie -> ResponseEntity.ok(movie))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete movie (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (movieService.deleteMovie(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Search movies")
    @GetMapping("/search")
    public ResponseEntity<List<MovieDto>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) Integer year) {
        List<MovieDto> movies = movieService.searchMovies(title, genre, director, year);
        return ResponseEntity.ok(movies);
    }
}