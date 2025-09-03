package com.videoclub.dto;

import jakarta.validation.constraints.*;

public class MovieDto {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    private String description;

    @Size(max = 100, message = "Genre must be less than 100 characters")
    private String genre;

    @Size(max = 100, message = "Director must be less than 100 characters")
    private String director;

    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year must be before 2030")
    private Integer yearReleased;

    @Min(value = 1, message = "Duration must be positive")
    private Integer durationMinutes;

    @DecimalMin(value = "0.0", message = "Rating must be non-negative")
    @DecimalMax(value = "10.0", message = "Rating must be 10.0 or less")
    private Double rating;

    @Size(max = 500, message = "Poster URL must be less than 500 characters")
    private String posterUrl;

    // Constructors
    public MovieDto() {}

    public MovieDto(String title, String description, String genre, String director, 
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
}