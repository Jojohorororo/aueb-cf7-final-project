package com.videoclub.controller;

import com.videoclub.config.TestSecurityConfig;
import com.videoclub.dto.MovieDto;
import com.videoclub.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@Import(TestSecurityConfig.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieDto movieDto;
    private List<MovieDto> movieList;

    @BeforeEach
    void setUp() {
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

        movieList = Arrays.asList(movieDto);
    }

    @Test
    @WithMockUser
    void getAllMovies_ShouldReturnMovieList() throws Exception {
        // Given
        when(movieService.getAllMovies()).thenReturn(movieList);

        // When & Then
        mockMvc.perform(get("/api/movies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("The Shawshank Redemption"))
                .andExpect(jsonPath("$[0].genre").value("Drama"))
                .andExpect(jsonPath("$[0].director").value("Frank Darabont"))
                .andExpect(jsonPath("$[0].yearReleased").value(1994))
                .andExpect(jsonPath("$[0].rating").value(9.3));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    @WithMockUser
    void getAllMovies_WhenNoMovies_ShouldReturnEmptyList() throws Exception {
        // Given
        when(movieService.getAllMovies()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/movies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    @WithMockUser
    void getMovieById_WhenMovieExists_ShouldReturnMovie() throws Exception {
        // Given
        when(movieService.getMovieById(1L)).thenReturn(Optional.of(movieDto));

        // When & Then
        mockMvc.perform(get("/api/movies/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Shawshank Redemption"))
                .andExpect(jsonPath("$.genre").value("Drama"))
                .andExpect(jsonPath("$.director").value("Frank Darabont"))
                .andExpect(jsonPath("$.yearReleased").value(1994))
                .andExpect(jsonPath("$.durationMinutes").value(142))
                .andExpect(jsonPath("$.rating").value(9.3));

        verify(movieService, times(1)).getMovieById(1L);
    }

    @Test
    @WithMockUser
    void getMovieById_WhenMovieDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(movieService.getMovieById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/movies/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).getMovieById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMovie_WithValidDataAndAdminRole_ShouldCreateMovie() throws Exception {
        // Given
        when(movieService.createMovie(any(MovieDto.class))).thenReturn(movieDto);

        // When & Then
        mockMvc.perform(post("/api/movies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Shawshank Redemption"))
                .andExpect(jsonPath("$.genre").value("Drama"));

        verify(movieService, times(1)).createMovie(any(MovieDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovie_WithUserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/movies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isForbidden());

        verify(movieService, never()).createMovie(any(MovieDto.class));
    }

    @Test
    void createMovie_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/movies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isUnauthorized());

        verify(movieService, never()).createMovie(any(MovieDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMovie_WithValidDataAndAdminRole_ShouldUpdateMovie() throws Exception {
        // Given
        MovieDto updatedMovie = new MovieDto();
        updatedMovie.setId(1L);
        updatedMovie.setTitle("Updated Movie Title");
        updatedMovie.setGenre("Updated Genre");
        
        when(movieService.updateMovie(eq(1L), any(MovieDto.class))).thenReturn(Optional.of(updatedMovie));

        // When & Then
        mockMvc.perform(put("/api/movies/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Movie Title"))
                .andExpect(jsonPath("$.genre").value("Updated Genre"));

        verify(movieService, times(1)).updateMovie(eq(1L), any(MovieDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMovie_WhenMovieDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(movieService.updateMovie(eq(999L), any(MovieDto.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/movies/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).updateMovie(eq(999L), any(MovieDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateMovie_WithUserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/movies/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isForbidden());

        verify(movieService, never()).updateMovie(anyLong(), any(MovieDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMovie_WhenMovieExistsAndAdminRole_ShouldDeleteMovie() throws Exception {
        // Given
        when(movieService.deleteMovie(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/movies/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(movieService, times(1)).deleteMovie(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMovie_WhenMovieDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(movieService.deleteMovie(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/movies/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).deleteMovie(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteMovie_WithUserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/movies/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(movieService, never()).deleteMovie(anyLong());
    }

    @Test
    @WithMockUser
    void searchMovies_WithTitleParameter_ShouldReturnFilteredMovies() throws Exception {
        // Given
        when(movieService.searchMovies("Shawshank", null, null, null))
                .thenReturn(movieList);

        // When & Then
        mockMvc.perform(get("/api/movies/search")
                .param("title", "Shawshank")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("The Shawshank Redemption"));

        verify(movieService, times(1)).searchMovies("Shawshank", null, null, null);
    }

    @Test
    @WithMockUser
    void searchMovies_WithGenreParameter_ShouldReturnFilteredMovies() throws Exception {
        // Given
        when(movieService.searchMovies(null, "Drama", null, null))
                .thenReturn(movieList);

        // When & Then
        mockMvc.perform(get("/api/movies/search")
                .param("genre", "Drama")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].genre").value("Drama"));

        verify(movieService, times(1)).searchMovies(null, "Drama", null, null);
    }

    @Test
    @WithMockUser
    void searchMovies_WithMultipleParameters_ShouldReturnFilteredMovies() throws Exception {
        // Given
        when(movieService.searchMovies("Shawshank", "Drama", "Frank Darabont", 1994))
                .thenReturn(movieList);

        // When & Then
        mockMvc.perform(get("/api/movies/search")
                .param("title", "Shawshank")
                .param("genre", "Drama")
                .param("director", "Frank Darabont")
                .param("year", "1994")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("The Shawshank Redemption"))
                .andExpect(jsonPath("$[0].genre").value("Drama"))
                .andExpect(jsonPath("$[0].director").value("Frank Darabont"))
                .andExpect(jsonPath("$[0].yearReleased").value(1994));

        verify(movieService, times(1)).searchMovies("Shawshank", "Drama", "Frank Darabont", 1994);
    }

    @Test
    @WithMockUser
    void searchMovies_WithNoResults_ShouldReturnEmptyList() throws Exception {
        // Given
        when(movieService.searchMovies("NonExistent", null, null, null))
                .thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/movies/search")
                .param("title", "NonExistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(movieService, times(1)).searchMovies("NonExistent", null, null, null);
    }
}