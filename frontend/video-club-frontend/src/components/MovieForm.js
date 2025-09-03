// src/components/MovieForm.js
import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import movieService from '../services/movieService';
import './MovieForm.css';

const MovieForm = () => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    genre: '',
    director: '',
    yearReleased: '',
    durationMinutes: '',
    rating: '',
    posterUrl: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = Boolean(id);

  useEffect(() => {
    if (isEdit) {
      loadMovie();
    }
  }, [id]);

  const loadMovie = async () => {
    try {
      const response = await movieService.getMovieById(id);
      setFormData(response.data);
    } catch (err) {
      setError('Failed to load movie');
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'yearReleased' || name === 'durationMinutes' ? 
               (value === '' ? '' : parseInt(value)) : 
               name === 'rating' ? 
               (value === '' ? '' : parseFloat(value)) : value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (isEdit) {
        await movieService.updateMovie(id, formData);
      } else {
        await movieService.createMovie(formData);
      }
      navigate('/movies');
    } catch (err) {
      setError(err.response?.data?.message || `Failed to ${isEdit ? 'update' : 'create'} movie`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="movie-form-container">
      <div className="movie-form">
        <h2>{isEdit ? 'Edit Movie' : 'Add New Movie'}</h2>
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Title*:</label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                required
                maxLength="200"
              />
            </div>
            
            <div className="form-group">
              <label>Genre:</label>
              <input
                type="text"
                name="genre"
                value={formData.genre}
                onChange={handleChange}
                maxLength="100"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Description:</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows="4"
            />
          </div>
          
          <div className="form-row">
            <div className="form-group">
              <label>Director:</label>
              <input
                type="text"
                name="director"
                value={formData.director}
                onChange={handleChange}
                maxLength="100"
              />
            </div>
            
            <div className="form-group">
              <label>Year Released:</label>
              <input
                type="number"
                name="yearReleased"
                value={formData.yearReleased}
                onChange={handleChange}
                min="1900"
                max="2030"
              />
            </div>
          </div>
          
          <div className="form-row">
            <div className="form-group">
              <label>Duration (minutes):</label>
              <input
                type="number"
                name="durationMinutes"
                value={formData.durationMinutes}
                onChange={handleChange}
                min="1"
              />
            </div>
            
            <div className="form-group">
              <label>Rating (0-10):</label>
              <input
                type="number"
                name="rating"
                value={formData.rating}
                onChange={handleChange}
                min="0"
                max="10"
                step="0.1"
              />
            </div>
          </div>
          
          <div className="form-group">
            <label>Poster URL:</label>
            <input
              type="url"
              name="posterUrl"
              value={formData.posterUrl}
              onChange={handleChange}
              maxLength="500"
              placeholder="https://example.com/poster.jpg"
            />
          </div>
          
          <div className="form-actions">
            <button type="submit" disabled={loading} className="btn btn-primary">
              {loading ? 'Saving...' : (isEdit ? 'Update Movie' : 'Add Movie')}
            </button>
            <button 
              type="button" 
              onClick={() => navigate('/movies')} 
              className="btn btn-secondary"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default MovieForm;