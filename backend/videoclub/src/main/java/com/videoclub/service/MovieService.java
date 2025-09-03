package com.videoclub.service;

import com.videoclub.dto.MovieDto;
import com.videoclub.entity.Movie;
import com.videoclub.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {
    
    @Autowired
    private MovieRepository movieRepository;

    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<MovieDto> getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(this::convertToDto);
    }

    public MovieDto createMovie(MovieDto movieDto) {
        Movie movie = convertToEntity(movieDto);
        Movie savedMovie = movieRepository.save(movie);
        return convertToDto(savedMovie);
    }

    public Optional<MovieDto> updateMovie(Long id, MovieDto movieDto) {
        return movieRepository.findById(id)
                .map(existingMovie -> {
                    existingMovie.setTitle(movieDto.getTitle());
                    existingMovie.setDescription(movieDto.getDescription());
                    existingMovie.setGenre(movieDto.getGenre());
                    existingMovie.setDirector(movieDto.getDirector());
                    existingMovie.setYearReleased(movieDto.getYearReleased());
                    existingMovie.setDurationMinutes(movieDto.getDurationMinutes());
                    existingMovie.setRating(movieDto.getRating());
                    existingMovie.setPosterUrl(movieDto.getPosterUrl());
                    Movie updatedMovie = movieRepository.save(existingMovie);
                    return convertToDto(updatedMovie);
                });
    }

    public boolean deleteMovie(Long id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<MovieDto> searchMovies(String title, String genre, String director, Integer year) {
        return movieRepository.findMoviesWithFilters(title, genre, director, year)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Conversion methods
    private MovieDto convertToDto(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setGenre(movie.getGenre());
        dto.setDirector(movie.getDirector());
        dto.setYearReleased(movie.getYearReleased());
        dto.setDurationMinutes(movie.getDurationMinutes());
        dto.setRating(movie.getRating());
        dto.setPosterUrl(movie.getPosterUrl());
        return dto;
    }

    private Movie convertToEntity(MovieDto dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDirector(dto.getDirector());
        movie.setYearReleased(dto.getYearReleased());
        movie.setDurationMinutes(dto.getDurationMinutes());
        movie.setRating(dto.getRating());
        movie.setPosterUrl(dto.getPosterUrl());
        return movie;
    }
}