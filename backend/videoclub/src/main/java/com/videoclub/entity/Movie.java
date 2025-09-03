package com.videoclub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 100, message = "Genre must be less than 100 characters")
    @Column(length = 100)
    private String genre;

    @Size(max = 100, message = "Director must be less than 100 characters")
    @Column(length = 100)
    private String director;

    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year must be before 2030")
    @Column(name = "year_released")
    private Integer yearReleased;

    @Min(value = 1, message = "Duration must be positive")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @DecimalMin(value = "0.0", message = "Rating must be non-negative")
    @DecimalMax(value = "10.0", message = "Rating must be 10.0 or less")
    @Column
    private Double rating;

    @Size(max = 500, message = "Poster URL must be less than 500 characters")
    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Movie() {}

    public Movie(String title, String description, String genre, String director, 
                 Integer yearReleased, Integer durationMinutes, Double rating, String posterUrl) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.director = director;
        this.yearReleased = yearReleased;
        this.durationMinutes = durationMinutes;
        this.rating = rating;
        this.posterUrl = posterUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public Integer getYearReleased() { return yearReleased; }
    public void setYearReleased(Integer yearReleased) { this.yearReleased = yearReleased; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}