package com.videoclub.repository;

import com.videoclub.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByGenreIgnoreCase(String genre);
    List<Movie> findByDirectorContainingIgnoreCase(String director);
    List<Movie> findByYearReleased(Integer year);
    
    @Query("SELECT m FROM Movie m WHERE " +
           "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:genre IS NULL OR LOWER(m.genre) = LOWER(:genre)) AND " +
           "(:director IS NULL OR LOWER(m.director) LIKE LOWER(CONCAT('%', :director, '%'))) AND " +
           "(:year IS NULL OR m.yearReleased = :year)")
    List<Movie> findMoviesWithFilters(@Param("title") String title,
                                     @Param("genre") String genre,
                                     @Param("director") String director,
                                     @Param("year") Integer year);
}

// UserRepository.java
