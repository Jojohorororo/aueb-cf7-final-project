package com.videoclub.service;

import com.videoclub.config.TestSecurityConfig;
import com.videoclub.dto.MovieDto;
import com.videoclub.entity.Movie;
import com.videoclub.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;
    private MovieDto movieDto;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("The Shawshank Redemption");
        movie.setDescription("Two imprisoned men bond over years, finding solace and eventual redemption");
        movie.setGenre("Drama");
        movie.setDirector("Frank Darabont");
        movie.setYearReleased(1994);
        movie.setDurationMinutes(142);
        movie.setRating(9.3);
        movie.setPosterUrl("https://example.com/poster.jpg");

        movieDto = new MovieDto();
        movieDto.setId(1L);
        movieDto.setTitle("The Shawshank Redemption");
        movieDto.setDescription("Two imprisoned men bond over years, finding solace and eventual redemption");
        movieDto.setGenre("Drama");
        movieDto.setDirector("Frank Darabont");
        movieDto.setYearReleased(1994);
        movieDto.setDurationMinutes(142);
        movieDto.setRating(9.3);
        movieDto.setPosterUrl("https://example.com/poster.jpg");
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovieDtos() {
        // Given
        List<Movie> movies = Arrays.asList(movie);
        when(movieRepository.findAll()).thenReturn(movies);

        // When
        List<MovieDto> result = movieService.getAllMovies();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("The Shawshank Redemption", result.get(0).getTitle());
        assertEquals("Drama", result.get(0).getGenre());
        assertEquals("Frank Darabont", result.get(0).getDirector());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void getMovieById_WhenMovieExists_ShouldReturnMovieDto() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // When
        Optional<MovieDto> result = movieService.getMovieById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("The Shawshank Redemption", result.get().getTitle());
        assertEquals("Drama", result.get().getGenre());
        assertEquals(1994, result.get().getYearReleased());
        assertEquals(9.3, result.get().getRating());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieById_WhenMovieDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<MovieDto> result = movieService.getMovieById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(movieRepository, times(1)).findById(999L);
    }

    @Test
    void createMovie_ShouldSaveAndReturnMovieDto() {
        // Given
        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle(movieDto.getTitle());
        savedMovie.setGenre(movieDto.getGenre());
        savedMovie.setDirector(movieDto.getDirector());
        savedMovie.setYearReleased(movieDto.getYearReleased());
        savedMovie.setRating(movieDto.getRating());
        
        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        // When
        MovieDto result = movieService.createMovie(movieDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("The Shawshank Redemption", result.getTitle());
        assertEquals("Drama", result.getGenre());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void updateMovie_WhenMovieExists_ShouldUpdateAndReturnMovieDto() {
        // Given
        MovieDto updatedDto = new MovieDto();
        updatedDto.setTitle("Updated Movie Title");
        updatedDto.setGenre("Updated Genre");
        updatedDto.setDirector("Updated Director");
        updatedDto.setYearReleased(2023);
        updatedDto.setRating(8.5);
        
        Movie updatedMovie = new Movie();
        updatedMovie.setId(1L);
        updatedMovie.setTitle("Updated Movie Title");
        updatedMovie.setGenre("Updated Genre");
        updatedMovie.setDirector("Updated Director");
        updatedMovie.setYearReleased(2023);
        updatedMovie.setRating(8.5);
        
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        // When
        Optional<MovieDto> result = movieService.updateMovie(1L, updatedDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Updated Movie Title", result.get().getTitle());
        assertEquals("Updated Genre", result.get().getGenre());
        assertEquals("Updated Director", result.get().getDirector());
        assertEquals(2023, result.get().getYearReleased());
        assertEquals(8.5, result.get().getRating());
        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void updateMovie_WhenMovieDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<MovieDto> result = movieService.updateMovie(999L, movieDto);

        // Then
        assertFalse(result.isPresent());
        verify(movieRepository, times(1)).findById(999L);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void deleteMovie_WhenMovieExists_ShouldReturnTrue() {
        // Given
        when(movieRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = movieService.deleteMovie(1L);

        // Then
        assertTrue(result);
        verify(movieRepository, times(1)).existsById(1L);
        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMovie_WhenMovieDoesNotExist_ShouldReturnFalse() {
        // Given
        when(movieRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = movieService.deleteMovie(999L);

        // Then
        assertFalse(result);
        verify(movieRepository, times(1)).existsById(999L);
        verify(movieRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchMovies_WithTitleFilter_ShouldReturnFilteredMovies() {
        // Given
        List<Movie> filteredMovies = Arrays.asList(movie);
        when(movieRepository.findMoviesWithFilters("Shawshank", null, null, null))
                .thenReturn(filteredMovies);

        // When
        List<MovieDto> result = movieService.searchMovies("Shawshank", null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("The Shawshank Redemption", result.get(0).getTitle());
        verify(movieRepository, times(1))
                .findMoviesWithFilters("Shawshank", null, null, null);
    }

    @Test
    void searchMovies_WithGenreFilter_ShouldReturnFilteredMovies() {
        // Given
        List<Movie> filteredMovies = Arrays.asList(movie);
        when(movieRepository.findMoviesWithFilters(null, "Drama", null, null))
                .thenReturn(filteredMovies);

        // When
        List<MovieDto> result = movieService.searchMovies(null, "Drama", null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Drama", result.get(0).getGenre());
        verify(movieRepository, times(1))
                .findMoviesWithFilters(null, "Drama", null, null);
    }

    @Test
    void searchMovies_WithMultipleFilters_ShouldReturnFilteredMovies() {
        // Given
        List<Movie> filteredMovies = Arrays.asList(movie);
        when(movieRepository.findMoviesWithFilters("Shawshank", "Drama", "Frank Darabont", 1994))
                .thenReturn(filteredMovies);

        // When
        List<MovieDto> result = movieService.searchMovies("Shawshank", "Drama", "Frank Darabont", 1994);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("The Shawshank Redemption", result.get(0).getTitle());
        assertEquals("Drama", result.get(0).getGenre());
        assertEquals("Frank Darabont", result.get(0).getDirector());
        assertEquals(1994, result.get(0).getYearReleased());
        verify(movieRepository, times(1))
                .findMoviesWithFilters("Shawshank", "Drama", "Frank Darabont", 1994);
    }

    @Test
    void searchMovies_WithNoResults_ShouldReturnEmptyList() {
        // Given
        when(movieRepository.findMoviesWithFilters("NonExistent", null, null, null))
                .thenReturn(Arrays.asList());

        // When
        List<MovieDto> result = movieService.searchMovies("NonExistent", null, null, null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movieRepository, times(1))
                .findMoviesWithFilters("NonExistent", null, null, null);
    }
}