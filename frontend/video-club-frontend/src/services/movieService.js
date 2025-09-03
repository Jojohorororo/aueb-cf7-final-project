import axios from 'axios';
import authService from './authService';

const API_URL = 'http://localhost:9090/api/movies';

class MovieService {
  getAllMovies() {
    return axios.get(API_URL, { headers: authService.getAuthHeader() });
  }

  getMovieById(id) {
    return axios.get(API_URL + '/' + id, { headers: authService.getAuthHeader() });
  }

  createMovie(movieData) {
    return axios.post(API_URL, movieData, { headers: authService.getAuthHeader() });
  }

  updateMovie(id, movieData) {
    return axios.put(API_URL + '/' + id, movieData, { headers: authService.getAuthHeader() });
  }

  deleteMovie(id) {
    return axios.delete(API_URL + '/' + id, { headers: authService.getAuthHeader() });
  }

  searchMovies(params) {
    return axios.get(API_URL + '/search', { 
      params,
      headers: authService.getAuthHeader() 
    });
  }
}

export default new MovieService();