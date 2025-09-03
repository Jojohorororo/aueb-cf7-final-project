-- Video Club Database Schema

CREATE DATABASE video_club;
USE video_club;

-- Users table for authentication
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Movies table
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    genre VARCHAR(100),
    director VARCHAR(100),
    year_released INT,
    duration_minutes INT,
    rating DECIMAL(3,1),
    poster_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO users (username, email, password, role) VALUES 
('admin', 'admin@videoclub.com', '$2a$10$N.kmQY6wTJhb2MlOH5UQ0upOy8JeqCYZANPNg5QYXw.bYFYqbqjzW', 'ADMIN'),
('user', 'user@videoclub.com', '$2a$10$N.kmQY6wTJhb2MlOH5UQ0upOy8JeqCYZANPNg5QYXw.bYFYqbqjzW', 'USER');

INSERT INTO movies (title, description, genre, director, year_released, duration_minutes, rating, poster_url) VALUES 
('The Shawshank Redemption', 'Two imprisoned men bond over years, finding solace and eventual redemption.', 'Drama', 'Frank Darabont', 1994, 142, 9.3, 'https://via.placeholder.com/300x450'),
('The Godfather', 'The aging patriarch of an organized crime dynasty transfers control.', 'Crime', 'Francis Ford Coppola', 1972, 175, 9.2, 'https://via.placeholder.com/300x450'),
('Pulp Fiction', 'The lives of two mob hitmen, a boxer, and others intertwine.', 'Crime', 'Quentin Tarantino', 1994, 154, 8.9, 'https://via.placeholder.com/300x450');