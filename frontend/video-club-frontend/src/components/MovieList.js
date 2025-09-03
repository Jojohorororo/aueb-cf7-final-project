// src/components/MovieList.js
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import movieService from '../services/movieService';
import authService from '../services/authService';
import SearchBar from './SearchBar';
import './MovieList.css';

const MovieList = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const currentUser = authService.getCurrentUser();
  const isAdmin = currentUser && currentUser.role === 'ADMIN';

  useEffect(() => {
    loadMovies();
  }, []);

  const loadMovies = async () => {
    try {
      setLoading(true);
      const response = await movieService.getAllMovies();
      setMovies(response.data);
    } catch (err) {
      setError('Failed to load movies');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (searchParams) => {
    try {
      setLoading(true);
      const response = await movieService.searchMovies(searchParams);
      setMovies(response.data);
    } catch (err) {
      setError('Search failed');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this movie?')) {
      try {
        await movieService.deleteMovie(id);
        loadMovies(); // Reload the list
      } catch (err) {
        setError('Failed to delete movie');
      }
    }
  };

  const handleLogout = () => {
    authService.logout();
    window.location.reload();
  };

  if (loading) return <div className="loading">Loading movies...</div>;

  return (
    <div className="movie-list-container">
      <header className="header">
        <h1>Video Club - Movie Collection</h1>
        <div className="header-actions">
  <span>Welcome, {currentUser.username}!</span>
  <Link to="/profile" className="btn btn-secondary">
    Profile
  </Link>
  {isAdmin && (
    <Link to="/movies/new" className="btn btn-primary">
      Add New Movie
    </Link>
  )}
  <button onClick={handleLogout} className="btn btn-secondary">
    Logout
  </button>
</div>
      </header>

      <SearchBar onSearch={handleSearch} onReset={loadMovies} />

      {error && <div className="error-message">{error}</div>}

      <div className="movies-grid">
        {movies.length === 0 ? (
          <div className="no-movies">No movies found</div>
        ) : (
          movies.map(movie => (
            <div key={movie.id} className="movie-card">
              <img 
                src={movie.posterUrl || 'https://via.placeholder.com/300x450'} 
                alt={movie.title}
                className="movie-poster"
              />
              <div className="movie-info">
                <h3>{movie.title}</h3>
                <p className="movie-genre">{movie.genre}</p>
                <p className="movie-year">{movie.yearReleased}</p>
                <p className="movie-director">Dir: {movie.director}</p>
                <p className="movie-rating">⭐ {movie.rating}/10</p>
                <p className="movie-duration">{movie.durationMinutes} min</p>
                
                {isAdmin && (
                  <div className="movie-actions">
                    <Link 
                      to={`/movies/edit/${movie.id}`} 
                      className="btn btn-edit"
                    >
                      Edit
                    </Link>
                    <button 
                      onClick={() => handleDelete(movie.id)}
                      className="btn btn-delete"
                    >
                      Delete
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default MovieList;